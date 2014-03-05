package com.acuitra.question.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acuitra.ErrorCodes;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.ParallelPipelineRunner;
import com.acuitra.pipeline.RunnablePipeline;
import com.acuitra.question.core.Answer;
import com.acuitra.question.core.Question;
import com.acuitra.stages.StageException;
import com.acuitra.stages.integrated.IntegratedQuepyStage;
import com.acuitra.stages.integrated.NLPQueryStage;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;


@Path("/ask")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Client jerseyClient;
	private String namedEntityRecognitionURL;
	private String sparqlEndpointURL;
	private String quepyURL; 
	private ListMultimap<String, String> namePredicateMapping;


	public QuestionResource(Client jerseyClient, String namedEntityRecognitionURL, String sparqlEndpointURL, String quepyURL) {
		this.jerseyClient = jerseyClient;
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
		this.sparqlEndpointURL = sparqlEndpointURL;
		this.quepyURL = quepyURL;
		
		namePredicateMapping = readPropertyMapping();
	}
	

	private ListMultimap<String, String> readPropertyMapping() {
		// read csv in format: word, predicate, preferred_term
		// should contain either predicate or preferred term, not both
		
		String filename = "word-predicate-mappings.csv"; 

		ListMultimap<String, String> namePredicateMapping = ArrayListMultimap.create();			
		HashMap<String, String> namePreferredMapping = new HashMap<>();
		
		
		
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
		try {
			if (in == null) {
				logger.error("Could not find /word-predicate-mappings.csv");
			} else {
				InputStreamReader is = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(is);

				boolean firstline = true;
				String read;
				try {
					read = br.readLine();

					while (read != null) {
						// skip the first line
						if (!firstline) {
							Iterable<String> strings = Splitter.on(',').trimResults().split(read);
							String name = null;
							String predicate = null;
							String preferredTerm = null;

							int count = 0;
							for (String string : strings) {
								// iterate along the comma separated string
								switch (count) {
								case 0:
									name = string;
									break;
								case 1:
									if (!Strings.isNullOrEmpty(string)) {
										predicate = string;
										if (!predicate.startsWith("<")) {
											predicate = "<" + predicate;
										}
										if (!predicate.endsWith(">")) {
											predicate = predicate + ">";
										}
									}
									break;

								case 2:
									if (!Strings.isNullOrEmpty(string)) {
										preferredTerm = string;
									}
									break;

								default:
									break;
								}
								count++;

							}

							if (!Strings.isNullOrEmpty(name)) {
								// name should never be null

								// should contain either predicate or preferred term, not both
								if (!Strings.isNullOrEmpty(predicate)) {
									namePredicateMapping.put(name, predicate);
								} else if (!Strings.isNullOrEmpty(preferredTerm)) {
									namePreferredMapping.put(name, preferredTerm);
								}
							}

						} else {
							// swallow the header line
							firstline = false;
						}
						read = br.readLine();

					}

					// now both maps are filled. Take the namePreferredMapping and build a predicate map out of the non-preferred names, too
					Set<String> keys = namePreferredMapping.keySet();
					for (String name : keys) {
						String preferredName = namePreferredMapping.get(name);

						ArrayList<String> predicates = new ArrayList<>();

						List<String> existingPredicates = namePredicateMapping.get(preferredName);

						predicates.addAll(existingPredicates);

						namePredicateMapping.putAll(name, predicates);

					}

					logger.info(filename + " mappings successfully loaded");

				} catch (IOException e) {
					logger.error("Error loading " + filename, e);

				}
			}
			return namePredicateMapping;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		
	}


	@GET
	@Timed
	public List<Answer> ask(@QueryParam("question") String param) {
		Question question = new Question(param);
	
		
		ContextWithJerseyClient<Question, List<Answer>> context = new  ContextWithJerseyClient<>(jerseyClient);
		context.setInput(question);
		
		RunnablePipeline<Question, List<Answer>> nlpPipeline = new RunnablePipeline<>("NLP Pipeline", context);
		RunnablePipeline<Question, List<Answer>> quepyPipeline = new RunnablePipeline<>("Quepy Pipeline", context);
		
		nlpPipeline.addStage(new NLPQueryStage(namedEntityRecognitionURL, sparqlEndpointURL, namePredicateMapping));
		quepyPipeline.addStage(new IntegratedQuepyStage(quepyURL, sparqlEndpointURL, jerseyClient));
		
		ParallelPipelineRunner<Question, List<Answer>> pipeRunner = new ParallelPipelineRunner<>(10000);
		pipeRunner.addPipeline(nlpPipeline);
		pipeRunner.addPipeline(quepyPipeline);
		
		pipeRunner.run();
		
		if (quepyPipeline.isComplete() || nlpPipeline.isComplete()) {
			// at least one pipeline is finished	
			Map<String, List<Answer>> answerMap = context.getPreviousOutputs();
			
			List<Answer> quepyAnswers = answerMap.get(IntegratedQuepyStage.class.getName()); 
			
			List<Answer> nlpAnswers = answerMap.get(NLPQueryStage.class.getName());
			

			
			List<Answer> results = new ArrayList<>();
				
			if (isEmpty(nlpAnswers) && isEmpty(quepyAnswers)) {	
				Answer answer = new Answer();
				answer.setErrorCode(ErrorCodes.NO_ANSWER_GENERATED);
				answer.setErrorMessage("Could not find answer");
				
				results.add(answer);
			} else {
				results = mergeList(results, nlpAnswers, quepyAnswers);
				
			}
			
			return results;
			
		} else {
			// what happened?!
			if (context.isError()) {
				throw context.getException();
			} else {
				throw new StageException("Processing questions took too long", ErrorCodes.PROCESSING_QUESTION_TIMEOUT);
			}
		}
	}

	
	private List<Answer> mergeList(List<Answer> results, List<Answer> ... listsToMerge) {
		List<String> index = new ArrayList<>();
		
		for (List<Answer> list : listsToMerge) {
			if (list != null) {
				for (Answer answer : list) {
					if (!index.contains(answer.getAnswer())) {				
						answer.addVote();
						
						index.add(answer.getAnswer());
						results.add(answer);
					}
				}				
			}
		}
		
		float defaultConfidence = (float) (1.0/results.size());
		for (Answer answer : results) {
			answer.setConfidence(defaultConfidence * answer.getVotes());
		}
		
		return results;
	}


	private boolean isEmpty(List<Answer> lst) {
		if (lst == null) {
			return true;
		} else {
			return (lst.size() == 0);
		}
	}
	
}

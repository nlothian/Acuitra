package com.acuitra.question.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.acuitra.ErrorCodes;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.ParallelPipelineRunner;
import com.acuitra.pipeline.RunnablePipeline;
import com.acuitra.question.core.Answer;
import com.acuitra.question.core.Question;
import com.acuitra.stages.StageException;
import com.acuitra.stages.integrated.IntegratedQuepyStage;
import com.acuitra.stages.integrated.NLPQueryStage;
import com.sun.jersey.api.client.Client;
import com.yammer.metrics.annotation.Timed;


@Path("/ask")
@Produces(MediaType.APPLICATION_JSON)
public class QuestionResource {
	
	private Client jerseyClient;
	private String namedEntityRecognitionURL;
	private String sparqlEndpointURL;
	private String quepyURL; 


	public QuestionResource(Client jerseyClient, String namedEntityRecognitionURL, String sparqlEndpointURL, String quepyURL) {
		this.jerseyClient = jerseyClient;
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
		this.sparqlEndpointURL = sparqlEndpointURL;
		this.quepyURL = quepyURL;
	}
	

	@GET
	@Timed
	public List<Answer> ask(@QueryParam("question") String param) {
		Question question = new Question(param);
	
		
		ContextWithJerseyClient<Question, List<Answer>> context = new  ContextWithJerseyClient<>(jerseyClient);
		context.setInput(question);
		
		RunnablePipeline<Question, List<Answer>> nlpPipeline = new RunnablePipeline<>("NLP Pipeline", context);
		RunnablePipeline<Question, List<Answer>> quepyPipeline = new RunnablePipeline<>("Quepy Pipeline", context);
		
		nlpPipeline.addStage(new NLPQueryStage(namedEntityRecognitionURL, sparqlEndpointURL));
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
			for (Answer answer : list) {
				if (!index.contains(answer.getAnswer())) {
					// votes is defaulted to 1, so increment only on the second time we see it
					if (answer.getVotes() == 1) {					
						answer.addVote();
					}
					index.add(answer.getAnswer());
					results.add(answer);
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

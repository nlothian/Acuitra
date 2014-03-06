package com.acuitra.stages.integrated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.acuitra.nlp.NLPUtils;
import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.Stage;
import com.acuitra.question.core.Answer;
import com.acuitra.question.core.Question;
import com.acuitra.sparql.SparqlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class NLPQueryStage implements Stage<Question, List<Answer>> {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final String PROPERTY_TYPE = "PROPERTY_TYPE";
	private static final String REQUESTED_SUBJECT = "REQUESTED_SUBJECT";
	private List<Answer> resultAnswers = new ArrayList<>();
	ContextWithJerseyClient<Question, List<Answer>> context;
	String namedEntityRecognitionURL;
	String sparqlEndpointURL;
	ListMultimap<String, String> namePredicateMapping; 
	

	public NLPQueryStage(String namedEntityRecognitionURL, String sparqlEndpointURL, ListMultimap<String, String> namePredicateMapping) {
		super();
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
		this.sparqlEndpointURL = sparqlEndpointURL;
		this.namePredicateMapping = namePredicateMapping;
	}	
	
	@Override
	public void loadContext(Context<Question, List<Answer>> ctx) {
		this.context =  (ContextWithJerseyClient<Question, List<Answer>>) ctx;		
	}

	@Override
	public void execute() {
		final Question question = context.getInput();
		
		final Answer answer = new Answer();
		answer.setQuestion(question);
		answer.addDebugInfo(this.getClass() +":QuestionText", question.getQuestion());
		
		
		final Client jerseyClient = context.getJerseyClient();
		
		WebResource webResource = jerseyClient.resource(namedEntityRecognitionURL);		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("text", question.getQuestion());
				
		
		ClientResponse response = webResource.queryParams(params).type("application/x-www-form-urlencoded").get(ClientResponse.class);
		
		String text = response.getEntity(String.class);
		//System.out.println(text);
		answer.addDebugInfo(this.getClass() +":ParsedQuestion", question.getQuestion());
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode;
		try {
			rootNode = mapper.readTree(text);
			
			// look for any proper nouns
			System.out.println(rootNode);
			List<String> possiblyCompoundNoun = NLPUtils.findTaggedWords(rootNode, true, "NNP", "NNPS");
			
			final String properNouns = Joiner.on(" ").join(possiblyCompoundNoun);
			
			logger.info("Nouns: " + properNouns);
			
			context.setAttribute(REQUESTED_SUBJECT, properNouns);
			
			
			//System.out.println("Place = " + place);
			answer.addDebugInfo(this.getClass() +":proper noun", properNouns);
			
			if (properNouns != null) {
				List<String> properties = extractTargetProperty(rootNode);
						
				// we only know how to handle a single property
				String temp = null;
				if (properties.size() > 0) {
					temp = properties.get(0);
				}
				final String property = temp;		
				
				answer.addDebugInfo(this.getClass() +":Property", property);				
				
				if (property != null) {
					// we know what property we want
					
					final List<String> rdfPredicates = mapPropertyToRDFPredicate(property);
					
					
					if (rdfPredicates == null) {
						answer.addDebugInfo(this.getClass() +":Predicate", null);
					} else {
						// we know how to map this property
												
						// build the query
						final String query = buildQuery();
						
						List<Future<List<Answer>>> list = new ArrayList<>();
						
						
						// for each predicate, run a query
						for (final String predicate : rdfPredicates) {
							Callable<List<Answer>> runQueryCallable = new Callable<List<Answer>>() {

								@Override
								public List<Answer> call() throws Exception {
									
									// run the query
									String output = SparqlUtils.runQuery(jerseyClient, sparqlEndpointURL, query, properNouns, predicate);
									
									answer.addDebugInfo(this.getClass() +":SPARQLResultSet", output);
									
									//System.out.println(output);
									List<Answer> processedAnswers = processResultSet(output, property);
									if (processedAnswers != null && processedAnswers.size() > 0) {
										// copy information we've been collecting to the first answer in the list
										Answer firstAnswer = processedAnswers.get(0);
										firstAnswer.addDebugInfo(answer.getDebugInfo());
										firstAnswer.setQuestion(question);							
									}
									
									return processedAnswers;

								}
							};
							
							ExecutorService executor = Executors.newCachedThreadPool();
							Future<List<Answer>> future = executor.submit(runQueryCallable);
							list.add(future);
						}
						
						
						for (Future<List<Answer>> future : list) {
							try {
								List<Answer> queryAnswers = future.get(10, TimeUnit.SECONDS);		
								for (Answer queryAnswer : queryAnswers) {
									resultAnswers.add(queryAnswer);
								}
							} catch (Exception e) {
								logger.warn("error occured retrieving answer", e);
							}
						}
					}					
				}				
			}
			
		} catch (IOException e) {
			logger.error("Error with query", e);
		}
		
	}

	private List<String> extractTargetProperty(JsonNode rootNode) {
		// Assume we are looking for something about a proper noun. 
		
		// NN: What is the capital of Australia
		// NNS: What species is a Kangaroo
		// VBD: Where was Abraham Lincoln buried?
		// VBP: What is the subordo of a Porcupine?
		// JJ: How tall is Bill Clinton?
		
		String[] possibleTags = {"NN", "NNS", "VBD", "VBP", "JJ"};
		for (int i = 0; i < possibleTags.length; i++) {
			List<String> properties = NLPUtils.findTaggedWords(rootNode, false, possibleTags[i]); 
			if (properties.size() > 0) {
				context.setAttribute(PROPERTY_TYPE, possibleTags[i]);
				return properties;			
			}
			
		}
		
		// nothing found
		return new ArrayList<String>();
	}

	private String buildQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		builder.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ");
		builder.append("");
		builder.append("SELECT DISTINCT ?subjectRes ?subjectLabel ?answer ?answerLabel WHERE '{' ");
		builder.append(" '{'");
		builder.append("	SELECT ?subjectRes WHERE '{'");
		builder.append("			'{'?subjectRes rdfs:label \"{0}\"@en.");
		builder.append("		'}' UNION '{'");
		builder.append("			?altName rdfs:label \"{0}\"@en .");
		builder.append("			?altName dbpedia-owl:wikiPageRedirects ?subjectRes  .");
		builder.append("		'}'");
		builder.append(" 	'}'");
		builder.append(" '}'");
		//builder.append(" ?subjectRes rdf:type dbpedia-owl:Country.");
		builder.append(" ?subjectRes  {1} ?answer. ");
		builder.append(" OPTIONAL '{'?answer rdfs:label ?answerLabel. '}'. ");
		builder.append(" OPTIONAL '{'?subjectRes rdfs:label ?subjectLabel '}'. ");
		builder.append("'}'");
		
		return builder.toString();
	}

	private List<Answer> processResultSet(String sparqlResult, String property) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(sparqlResult);
					
			
			int answerCount = rootNode.path("results").path("bindings").size();
			List<Answer> answers = new ArrayList<>();
			
			for (int i = 0; i < answerCount; i++) {
				String answerValue = rootNode.path("results").path("bindings").path(i).path("answer").path("value").asText();
				String answerLabel = rootNode.path("results").path("bindings").path(i).path("answerLabel").path("value").asText();
				String subjectLabel = rootNode.path("results").path("bindings").path(i).path("subjectLabel").path("value").asText();
				String subjectRes = rootNode.path("results").path("bindings").path(i).path("subjectRes").path("value").asText();
						
				Answer answer = new Answer();		
				if (!answerLabel.isEmpty()) {
					answer.setAnswer(answerLabel);
				} else {
					answer.setAnswer(answerValue);
				}
				
				if (!subjectLabel.isEmpty()) {
					String questionType = context.getAttribute(PROPERTY_TYPE);
					String answerToUse = answerValue;
					if (!answerLabel.isEmpty()) {
						answerToUse = answerLabel;
					}
					
					
					if ("NN".equals(questionType)) {
						answer.setLongAnswer("The " + property + " of " + subjectLabel + " is " + answerToUse);
					} else if ("NNS".equals(questionType)) {
						answer.setLongAnswer(subjectLabel + " is a " + answerToUse + " " + property);						
					} else if ("VBD".equals(questionType)) {
						answer.setLongAnswer(subjectLabel + " is " + property + " at " + answerToUse);
					} else if ("JJ".equals(questionType)) {
						answer.setLongAnswer(subjectLabel + " is " + answerToUse + " " + property);
					} else if ("VBP".equals(questionType)) {
						answer.setLongAnswer(property + " of " + subjectLabel + " is " + answerToUse);
					}
					
					
				}
				answers.add(answer);
			}
			
			return answers;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		
	}

	protected List<String> mapPropertyToRDFPredicate(String property) {
		String targettedWord = property.toLowerCase();
		List<String> result = null;
		
		List<String> predicates = namePredicateMapping.get(targettedWord);
		if (predicates.size() > 0) {
			result = predicates;
		}
		
		return result;
		
	}

	@Override
	public List<Answer> getOutput() {
		return resultAnswers;
	}

	@Override
	public String getKeyName() {		
		return this.getClass().getName();
	}
	

	

}

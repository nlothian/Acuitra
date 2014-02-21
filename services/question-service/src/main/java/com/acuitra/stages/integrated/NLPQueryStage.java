package com.acuitra.stages.integrated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.ContextWithJerseyClient;
import com.acuitra.pipeline.Stage;
import com.acuitra.question.core.Answer;
import com.acuitra.question.core.Question;
import com.acuitra.sparql.SparqlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class NLPQueryStage implements Stage<Question, List<Answer>> {
	private List<Answer> answers = new ArrayList<>();
	private ContextWithJerseyClient<Question, List<Answer>> context;
	private String namedEntityRecognitionURL;
	private String sparqlEndpointURL; 
	

	public NLPQueryStage(String namedEntityRecognitionURL, String sparqlEndpointURL) {
		super();
		this.namedEntityRecognitionURL = namedEntityRecognitionURL;
		this.sparqlEndpointURL = sparqlEndpointURL;
	}	
	
	@Override
	public void loadContext(Context<Question, List<Answer>> ctx) {
		this.context =  (ContextWithJerseyClient<Question, List<Answer>>) ctx;		
	}

	@Override
	public void execute() {
		Question question = context.getInput();
		
		Answer answer = new Answer();
		answer.setQuestion(question);
		answer.addDebugInfo(this.getClass() +":QuestionText", question.getQuestion());
		
		
		Client jerseyClient = context.getJerseyClient();
		
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
			
			// look for any places
			String place = findTaggedWord(rootNode, "NNP");
			
			//System.out.println("Place = " + place);
			answer.addDebugInfo(this.getClass() +":Place", place);
			
			if (place != null) {
				// We are looking for something about a place. 
				String property = findTaggedWord(rootNode, "NN");
				//System.out.println("Property = " + property);
				answer.addDebugInfo(this.getClass() +":Property", property);
				
				
				if (property != null) {
					// we know what property we want
					
					String rdfPredicate = mapPropertyToRDFPredicate(property);
					
					answer.addDebugInfo(this.getClass() +":Predicate", rdfPredicate);
					
					if (rdfPredicate != null) {
						// we know how to map this property
												
						StringBuilder builder = new StringBuilder();
						builder.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ");
						builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
						builder.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> ");
						builder.append("");
						builder.append("SELECT DISTINCT ?countryRes ?countryLabel ?answer ?answerLabel WHERE '{' ");
						builder.append(" '{'");
						builder.append("	SELECT ?countryRes WHERE '{'");
						builder.append("			'{'?countryRes rdfs:label \"{0}\"@en.");
						builder.append("		'}' UNION '{'");
						builder.append("			?altName rdfs:label \"{0}\"@en .");
						builder.append("			?altName dbpedia-owl:wikiPageRedirects ?countryRes  .");
						builder.append("		'}'");
						builder.append(" 	'}'");
						builder.append(" '}'");
						builder.append(" ?countryRes rdf:type dbpedia-owl:Country.");
						builder.append(" ?countryRes  {1} ?answer. ");
						builder.append(" OPTIONAL '{'?answer rdfs:label ?answerLabel. '}'. ");
						builder.append(" OPTIONAL '{'?countryRes rdfs:label ?countryLabel '}'. ");
						builder.append("'}'");
												 						
						String output = SparqlUtils.runQuery(jerseyClient, sparqlEndpointURL, builder.toString(), place, rdfPredicate);
						
						answer.addDebugInfo(this.getClass() +":SPARQLResultSet", output);
						
						System.out.println(output);
						
						answers = processResultSet(output, property);
						if (answers != null && answers.size() > 0) {
							// copy information we've been collecting to the first answer in the list
							Answer firstAnswer = answers.get(0);
							firstAnswer.addDebugInfo(answer.getDebugInfo());
							firstAnswer.setQuestion(question);							
						}
						
						
					}
					
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		
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
				String countryLabel = rootNode.path("results").path("bindings").path(i).path("countryLabel").path("value").asText();
				String countryRes = rootNode.path("results").path("bindings").path(i).path("countryRes").path("value").asText();
						
				Answer answer = new Answer();		
				if (!answerLabel.isEmpty()) {
					answer.setAnswer(answerLabel);
				} else {
					answer.setAnswer(answerValue);
				}
				
				if (!countryLabel.isEmpty()) {
					if (!answerLabel.isEmpty()) {
						answer.setLongAnswer("The " + property + " of " + countryLabel + " is " + answerLabel);
					} else {
						answer.setLongAnswer("The " + property + " of " + countryLabel + " is " + answerValue);
					}
					
				}
				answers.add(answer);
			}
			
			return answers;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		
	}

	private String mapPropertyToRDFPredicate(String property) {
		String targettedWord = property.toUpperCase();
		
		String result = null;
		
		if ("CAPITAL".equals(targettedWord)) {
			result = "<http://dbpedia.org/ontology/capital>";
		} else if ("AREA".equals(targettedWord)) {
			result = "<http://dbpedia.org/property/areaKm>";
		} else if ("POPULATION".equals(targettedWord)) {
			result = "<http://dbpedia.org/property/populationEstimate>";
		} else if ("ANTHEM".equals(targettedWord)) {
			result = "<http://dbpedia.org/property/nationalAnthem>";
		} else if ("GDP".equals(targettedWord)) {
			result = "<http://dbpedia.org/property/gdpNominal>";
		} 
		
		return result;
		
	}

	@Override
	public List<Answer> getOutput() {
		return answers;
	}

	@Override
	public String getKeyName() {		
		return this.getClass().getName();
	}
	
	private String findTaggedWord(JsonNode rootNode, String tag) {
		int size = rootNode.size();
		for (int i = 0; i < size; i++) {
			JsonNode child = rootNode.get(i);
			String word = findTaggedWord(child, tag);
			if (word != null) {
				return word;
			} else {
				if ((word = checkForTaggedWord(child, tag)) != null) {
					return word;
				}		
				
			}
		} 
		
		return null;

	}

	private String checkForTaggedWord(JsonNode child, String tag) {
		if ((child.size() == 2) && (tag.equals(child.get(1).asText()))) {
			return child.get(0).asText();
		} else {
			return null;
		}
	}	
	

}

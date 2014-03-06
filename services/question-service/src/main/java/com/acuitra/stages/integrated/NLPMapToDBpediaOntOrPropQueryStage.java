package com.acuitra.stages.integrated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.acuitra.sparql.SparqlUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ListMultimap;

public class NLPMapToDBpediaOntOrPropQueryStage extends NLPQueryStage {

	public NLPMapToDBpediaOntOrPropQueryStage(String namedEntityRecognitionURL, String sparqlEndpointURL, ListMultimap<String, String> namePredicateMapping) {
		super(namedEntityRecognitionURL, sparqlEndpointURL, namePredicateMapping);
	}
	
	
	protected List<String> mapPropertyToRDFPredicate(String property) {
		
		List<String> results = new ArrayList<>();
		
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> ");
		builder.append("SELECT ?subject WHERE '{' ");
		builder.append("?subject rdfs:label \"{0}\" @en");
		builder.append(" '}' ");
				
		String output = SparqlUtils.runQuery(this.context.getJerseyClient(), sparqlEndpointURL, builder.toString(), property);
		
		//
		// Result looks like this
		//
		//		{
		//			head: {
		//				vars: [
		//			       "subject"
		//			       ]
		//			},
		//			results: {
		//				bindings: [
		//				           {
		//				        	   subject: {
		//				        	   		type: "uri",
		//				        	   		value: "http://dbpedia.org/resource/U.S."
		//				           		}
		//				           }
		//				    ]
		//			}		
		//		
		//		}
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			
			JsonNode rootNode = mapper.readTree(output);
			int answerCount = rootNode.path("results").path("bindings").size();
			
			for (int i = 0; i < answerCount; i++) {
				String answerValue = rootNode.path("results").path("bindings").path(i).path("subject").path("value").asText();
				
				if (!answerValue.startsWith("<")) {
					answerValue = "<" + answerValue;
				}

				if (!answerValue.endsWith(">")) {
					answerValue = answerValue + ">";
				}
				
				
				results.add(answerValue);

			}
		
			
			return results;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}			
		
		
	}
	
}

package com.acuitra.stages.answer;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 * Extracts an answer from a SPARQL resultset in JSON
 * 
 * @author nlothian
 *
 */
public class ProcessSPARQLResultStage extends AbstractAnswerStage {


	@Override
	public void execute() {
		
		String sparqlResult = getContext().getPreviousOutput(RunSPARQLQueryStage.class.getName());
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(sparqlResult);
			String var = rootNode.path("head").path("vars").get(0).asText();
			
			// Note that we could have multiple answers (or the answer could be a list) and this only extracts the first result
			String result = rootNode.path("results").path("bindings").path(0).path(var).path("value").asText();
			
			setOutput(result);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}


}

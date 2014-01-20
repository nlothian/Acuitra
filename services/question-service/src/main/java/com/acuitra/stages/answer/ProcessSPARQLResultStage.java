package com.acuitra.stages.answer;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessSPARQLResultStage extends AbstractAnswerStage {


	@Override
	public void execute() {
		
		String sparqlResult = getContext().getPreviousOutput(RunSPARQLQueryStage.class.getName());
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(sparqlResult);
			String var = rootNode.path("head").path("vars").get(0).asText();
			String result = rootNode.path("results").path("bindings").path(0).path(var).path("value").asText();
			
			//String result = rootNode.path("results").path("bindings").path(0).path("city").path("value").asText();
			
			setOutput(result);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}


}

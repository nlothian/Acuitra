package com.acuitra.stages.answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private String resultSetKey;
	private String resultSetAnswerColumn; 
	
	public ProcessSPARQLResultStage(String resultSetKey) {
		super();
		
		this.resultSetKey = resultSetKey;
	}

	public ProcessSPARQLResultStage(String resultSetKey, String resultSetAnswerColumn) {
		super();
		
		this.resultSetKey = resultSetKey;
		this.resultSetAnswerColumn = resultSetAnswerColumn;
	}	
	

	@Override
	public void execute() {
		
		// this only deals with the first SPARQL query returned
		String sparqlResult = getContext().getPreviousOutput(resultSetKey).get(0);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(sparqlResult);
			String var;
			if (resultSetAnswerColumn == null) {
				var = rootNode.path("head").path("vars").get(0).asText();
			} else {
				var = resultSetAnswerColumn;
			}						
			
			int answerCount = rootNode.path("results").path("bindings").size();
			List<String> answers = new ArrayList<String>();
			
			for (int i = 0; i < answerCount; i++) {
				answers.add(rootNode.path("results").path("bindings").path(i).path(var).path("value").asText());
			}
			
			setOutput(answers);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}


}

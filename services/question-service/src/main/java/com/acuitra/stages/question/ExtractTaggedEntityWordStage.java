package com.acuitra.stages.question;

import java.io.IOException;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.Stage;
import com.acuitra.question.core.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExtractTaggedEntityWordStage implements Stage<Question, String> {

	private Context<Question, String> context;
	private String output;
	private String tag;

	public ExtractTaggedEntityWordStage(String tag) {
		super();
		this.tag = tag;
	}
	
	@Override
	public void loadContext(Context<Question, String> ctx) {
		this.context = ctx;
	}

	@Override
	public void execute() {
		String taggedQuestion = context.getPreviousOutput(NamedEntityRecognitionStage.class.getName());
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode rootNode = mapper.readTree(taggedQuestion);
			
			this.output = findTaggedWord(rootNode);
			
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
	}

	private String findTaggedWord(JsonNode rootNode) {
		int size = rootNode.size();
		for (int i = 0; i < size; i++) {
			JsonNode child = rootNode.get(i);
			String place = findTaggedWord(child);
			if (place != null) {
				return place;
			} else {
				if ((place = checkForTaggedWord(child)) != null) {
					return place;
				}		
				
			}
		} 
		
		return null;

	}

	private String checkForTaggedWord(JsonNode child) {
		if ((child.size() == 2) && (tag.equals(child.get(1).asText()))) {
			return child.get(0).asText();
		} else {
			return null;
		}
	}

	@Override
	public String getOutput() {
		return output;
	}

	@Override
	public String getKeyName() {
		return this.getClass().getName() + tag;
	}

}

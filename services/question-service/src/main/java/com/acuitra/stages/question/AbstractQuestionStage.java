package com.acuitra.stages.question;

import java.util.ArrayList;
import java.util.List;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.Stage;
import com.acuitra.question.core.Question;

public abstract class AbstractQuestionStage implements Stage<Question, List<String>> {

	private Context<Question, List<String>> context;

	private List<String> output = new ArrayList<>();

	protected Context<Question, List<String>> getContext() {
		return context;
	}
	
	
	@Override
	public void loadContext(Context<Question, List<String>> ctx) {
		this.context =  ctx;
		
	}


	@Override
	public List<String> getOutput() {
		return output;
	}


	@Override
	public String getKeyName() {
		return this.getClass().getName();
	}


	protected void setOutput(List<String> output) {
		this.output = output;
	}
	
	protected void setOutput(String output) {
		this.output.add(output);
	}


}

package com.acuitra.stages.question;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.Stage;
import com.acuitra.question.core.Question;

public abstract class AbstractQuestionStage implements Stage<Question, String> {

	private Context<Question, String> context;

	private String output;

	protected Context<Question, String> getContext() {
		return context;
	}
	
	
	@Override
	public void loadContext(Context<Question, String> ctx) {
		this.context =  ctx;
		
	}


	@Override
	public String getOutput() {
		return output;
	}


	@Override
	public String getKeyName() {
		return this.getClass().getName();
	}


	protected void setOutput(String output) {
		this.output = output;
	}

}

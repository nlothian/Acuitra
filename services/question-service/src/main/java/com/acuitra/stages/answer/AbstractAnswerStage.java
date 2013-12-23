package com.acuitra.stages.answer;

import java.util.Map;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.Stage;

public abstract class AbstractAnswerStage implements Stage<Map<String, String>, String> {

	private String output;
	private Context<Map<String, String>, String> context;


	@Override
	public void loadContext(Context<Map<String, String>, String> ctx) {
		this.setContext(ctx);		
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

	protected Context<Map<String, String>, String> getContext() {
		return context;
	}


	protected void setContext(Context<Map<String, String>, String> context) {
		this.context = context;
	}

}

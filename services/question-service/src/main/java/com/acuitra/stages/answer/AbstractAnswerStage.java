package com.acuitra.stages.answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.acuitra.pipeline.Context;
import com.acuitra.pipeline.Stage;

public abstract class AbstractAnswerStage implements Stage<Map<String, List<String>>, List<String>> {

	private List<String> output = new ArrayList<String>();
	private Context<Map<String, List<String>>, List<String>> context;


	@Override
	public void loadContext(Context<Map<String, List<String>>, List<String>> ctx) {
		this.setContext(ctx);		
	}



	@Override
	public List<String> getOutput() {
		return output;
	}


	@Override
	public String getKeyName() {
		return this.getClass().getName();
	}


	protected void setOutput(String output) {
		this.output.add(output);
	}
	
	protected void setOutput(List<String> output) {
		this.output = output;
	}
	

	protected Context<Map<String, List<String>>, List<String>> getContext() {
		return context;
	}


	protected void setContext(Context<Map<String, List<String>>, List<String>> context) {
		this.context = context;
	}

}

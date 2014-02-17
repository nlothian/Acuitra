package com.acuitra.pipeline;

import java.util.LinkedList;

public class Pipeline<T,O> {
	private LinkedList<Stage<T,O>> stages = new LinkedList<Stage<T,O>>();
	
	
	public void addStage(Stage<T, O> stage) {
		stages.add(stage);
	}

	
	public void execute(Context<T,O> ctx) {
		for (Stage<T,O> stage : stages) {			
			stage.loadContext(ctx);
			stage.execute();
			
			ctx.addOutput(stage.getKeyName(), stage.getOutput());
			
			
		}
	}

	
}

package com.acuitra.pipeline;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SlowTestStage extends TestStage {

	private int pauseTimeMillis;
	private String output = "INCOMPLETE";
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public SlowTestStage(int stageCount, int pauseTimeMillis) {
		super(stageCount);
		
		this.pauseTimeMillis = pauseTimeMillis;
	}
	
	@Override
	public void execute() {

		LinkedList<Integer> list = getCtx().getInput();
		list.add(getStageCount());		
				
		
		try {
			getCtx().addOutput(this.getKeyName(), this.getOutput()); // initilize output 
			
			Thread.sleep(pauseTimeMillis);
			
			output = "COMPLETE: " + super.getOutput();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
				
	}

	@Override
	public String getOutput() {
		return output;
	}	

}

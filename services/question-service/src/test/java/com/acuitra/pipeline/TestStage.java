package com.acuitra.pipeline;

import java.util.LinkedList;

public class TestStage  implements Stage<LinkedList<Integer>, String> {
	private Context<LinkedList<Integer>, String> ctx;
	private int stageCount;
	
	public TestStage(int stageCount) {
		this.stageCount = stageCount;
	}



	@Override
	public void execute() {
		LinkedList<Integer> list = ctx.getInput();
		list.add(stageCount);			
		//ctx.addOutput(String.valueOf(stageCount), String.valueOf(stageCount));
	}

	@Override
	public String getOutput() {
		return "stage number " + stageCount;
	}

	@Override
	public void loadContext(Context<LinkedList<Integer>, String> ctx) {
		this.ctx = ctx;
		
	}


	@Override
	public String getKeyName() {
		return String.valueOf(stageCount);
	}

	protected Context<LinkedList<Integer>, String> getCtx() {
		return ctx;
	}

	protected void setCtx(Context<LinkedList<Integer>, String> ctx) {
		this.ctx = ctx;
	}

	protected int getStageCount() {
		return stageCount;
	}

	protected void setStageCount(int stageCount) {
		this.stageCount = stageCount;
	}
}
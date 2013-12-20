package com.acuitra.pipeline;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;


public class PipelineTest {

	private final class TestStage implements Stage<LinkedList<Integer>, String> {
		private Context<LinkedList<Integer>, String> ctx;
		private int stageCount;
		
		public TestStage(int stageCount) {
			this.stageCount = stageCount;
		}



		@Override
		public void execute() {
			LinkedList<Integer> list = ctx.getInput();
			list.add(stageCount);			
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
	}

	@Test
	public void test() {
		Pipeline<LinkedList<Integer>, String> pipeline = new Pipeline<>();
				
		
		pipeline.addStage(new TestStage(1));
		pipeline.addStage(new TestStage(2));
		pipeline.addStage(new TestStage(3));
		pipeline.addStage(new TestStage(4));
		pipeline.addStage(new TestStage(5));
		
		Context<LinkedList<Integer>, String> context = new Context<>();
		context.setInput(new LinkedList<Integer>());
		pipeline.execute(context);
		
		LinkedList<Integer> stageCounts = context.getInput();
		for (int i = 0; i < stageCounts.size(); i++) {
			Assert.assertEquals(i + 1, stageCounts.get(i).intValue());
		}
		
	}

}

package com.acuitra.pipeline;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;


public class PipelineTest {

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

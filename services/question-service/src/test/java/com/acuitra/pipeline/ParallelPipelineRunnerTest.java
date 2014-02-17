package com.acuitra.pipeline;

import java.util.LinkedList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ParallelPipelineRunnerTest {

	@Test
	public void test() {
		ParallelPipelineRunner<LinkedList<Integer>, String> runner = new ParallelPipelineRunner<>(500); // 5 seconds
		
		Context<LinkedList<Integer>, String> context1 = new Context<>();
		context1.setInput(new LinkedList<Integer>());				
	
		RunnablePipeline<LinkedList<Integer>, String> pipeline = new RunnablePipeline<LinkedList<Integer>, String>("Pipe 1", context1);
						
		pipeline.addStage(new SlowTestStage(1, 100));
		pipeline.addStage(new SlowTestStage(2, 100));				
		runner.addPipeline(pipeline);

		Context<LinkedList<Integer>, String> context2 = new Context<>();
		context2.setInput(new LinkedList<Integer>());						
		
		RunnablePipeline<LinkedList<Integer>, String> pipeline2 = new RunnablePipeline<LinkedList<Integer>, String>("Pipe 2", context2);
		
		pipeline2.addStage(new SlowTestStage(1, 100));
		pipeline2.addStage(new SlowTestStage(2, 600)); // this should fail				
		runner.addPipeline(pipeline2);		
			
		runner.run();

		Assert.assertTrue(pipeline.isComplete());
		Assert.assertFalse(pipeline2.isComplete());
		
		
		Map<String, Map<String,String>> outputs = runner.getOutputs();
		
		Map<String,String> pipe1Outputs = outputs.get("Pipe 1");
		Assert.assertNotNull(pipe1Outputs);		
		
		Assert.assertEquals("COMPLETE: stage number 1", pipe1Outputs.get("1"));
		Assert.assertEquals("COMPLETE: stage number 2", pipe1Outputs.get("2"));
		

		Map<String,String> pipe2Outputs = outputs.get("Pipe 2");
		Assert.assertNotNull(pipe1Outputs);		
		Assert.assertEquals("COMPLETE: stage number 1", pipe2Outputs.get("1"));
		Assert.assertEquals("INCOMPLETE", pipe2Outputs.get("2"));
		
		
	}

}

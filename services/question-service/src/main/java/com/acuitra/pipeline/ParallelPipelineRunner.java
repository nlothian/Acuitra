package com.acuitra.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelPipelineRunner<T, O> {
	private boolean running = false;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private List<RunnablePipeline<T, O>> pipelines = new ArrayList<>();
	private Map<String, Map<String,O>> outputs = new HashMap<>();

	private int timeoutMillis;

	
	public ParallelPipelineRunner(int timeoutMillis) {
		super();
		
		this.timeoutMillis = timeoutMillis;
	}
	
	public void addPipeline(RunnablePipeline<T, O> pipeline) {
		if (running) {
			throw new IllegalStateException("Cannot add when already running");
		}		
		pipelines.add(pipeline);
	}
	
	public void run() {
		if (running) {
			throw new IllegalStateException("Already running");
		}
		running = true;
		try {
			
			ExecutorService executor = Executors.newFixedThreadPool(pipelines.size());
			for (RunnablePipeline<T, O> pipe : pipelines) {
				executor.execute(pipe);
			}
			
		    executor.shutdown();

		    try {
			    // Wait until all threads are finish
				if (!executor.awaitTermination(getTimeoutMillis(), TimeUnit.MILLISECONDS)) {				
					logger.error("Pipeline execution took too long");					
				}
				
				
				for (RunnablePipeline<T, O> pipe : pipelines) {					
					outputs.put(pipe.getName(), pipe.getContext().getPreviousOutputs());
					
				}
				
				
			} catch (InterruptedException e1) {
				logger.error("Error", e1);
			}
			
			
		} finally {
			running = false;
		}				
	}
	
	public Map<String, Map<String,O>> getOutputs() {
		return outputs;
	}

	public int getTimeoutMillis() {
		return timeoutMillis;
	}
	
	
}

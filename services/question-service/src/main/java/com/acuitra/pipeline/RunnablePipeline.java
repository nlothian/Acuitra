package com.acuitra.pipeline;


public class RunnablePipeline<T, O> extends Pipeline<T, O> implements Runnable {
	private String name;

	private Context<T,O> context;
	
	private boolean complete = false;
	
	public RunnablePipeline(String name, Context<T,O> context) {
		super();
		
		this.context = context;
		this.name = name;
	}

	Context<T,O> getContext() {
		return context;
	}
	

	@Override
	public void run() {
		String oldName = Thread.currentThread().getName();
		Thread.currentThread().setName("RunnablePipeline: " + getName());
		try {
			execute(this.context);
			setComplete(true);
		} finally { 
			Thread.currentThread().setName(oldName);
		}
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

}

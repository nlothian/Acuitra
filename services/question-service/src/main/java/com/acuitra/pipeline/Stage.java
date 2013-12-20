package com.acuitra.pipeline;

public interface Stage<T,O> {
	public void loadContext(Context<T, O> ctx);

	public void execute();
	
	public O getOutput();

	public String getKeyName();

}

package com.acuitra.pipeline;

import java.util.HashMap;
import java.util.Map;

public class Context<T, O> {
	private boolean error;
	private RuntimeException exception = null;
	
	private T input;
	private Map<String, O> previousOutputs = new HashMap<>();

	public Map<String, O> getPreviousOutputs() {
		return previousOutputs;
	}

	public void setPreviousOutputs(Map<String, O> previousOutputs) {
		this.previousOutputs = previousOutputs;
	}

	public T getInput() {
		return input;
	}

	public void setInput(T input) {
		this.input = input;
	}
	
	public O getPreviousOutput(String key) {
		return previousOutputs.get(key);
	}	
	
	public void addOutput(String key, O output) {
		previousOutputs.put(key, output);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public RuntimeException getException() {
		return exception;
	}

	public void setException(RuntimeException exception) {
		this.exception = exception;
	}


	
}

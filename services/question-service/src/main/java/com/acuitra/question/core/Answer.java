package com.acuitra.question.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Answer {	
	private List<String> answers = new ArrayList<>();
	private Question question;
	private String errorMessage;
	private String errorCode;
	
	private Map<String, Object> debugInfo = new HashMap<>();
	
	public List<String> getAnswers() {
		return answers;
	}
	public void setAnswer(List<String> answers) {
		this.answers = answers;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public Map<String, Object> getDebugInfo() {
		return debugInfo;
	}

	public void addDebugInfo(String key, Object value) {
		this.debugInfo.put(key, value);
	}
	
	public void addDebugInfo(Map<String, List<String>> debug) {
		this.debugInfo.putAll(debug);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isError() {
		return (errorMessage != null);
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
}

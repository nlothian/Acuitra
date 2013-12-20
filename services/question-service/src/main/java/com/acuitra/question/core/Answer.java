package com.acuitra.question.core;

import java.util.HashMap;
import java.util.Map;

public class Answer {
	private String answer;
	private Question question;
	private Map<String, Object> debugInfo = new HashMap<>();
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
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
	
	public void addDebugInfo(Map<String, String> debug) {
		this.debugInfo.putAll(debug);
	}
	

}

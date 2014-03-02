package com.acuitra.question.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class Answer {	
	private String answer;
	private String longAnswer;
	private Question question;
	private String errorMessage;
	private String errorCode;
	private float confidence = 1;
	private int votes = 0;
	
	private Map<String, List<String>> debugInfo = new HashMap<>();
	
	
	
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public Map<String, List<String>> getDebugInfo() {
		return debugInfo;
	}

	public void addDebugInfo(String key, String value) {
		List<String> lst = new ArrayList<>();
		lst.add(value);
		this.debugInfo.put(key, lst);
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
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getLongAnswer() {
		return longAnswer;
	}
	public void setLongAnswer(String longAnswer) {
		this.longAnswer = longAnswer;
	}
	public float getConfidence() {
		return confidence;
	}
	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}
	public int getVotes() {
		return votes;
	}
	public void setVotes(int votes) {
		this.votes = votes;
	}
	public void addVote() {
		votes++;
		
	}
	
}

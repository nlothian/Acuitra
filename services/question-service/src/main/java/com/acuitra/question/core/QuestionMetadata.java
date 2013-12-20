package com.acuitra.question.core;

public class QuestionMetadata {
	private boolean aboutACountry;
	private boolean requestedData;
	private boolean canAnswer;	
	
	public boolean isAboutACountry() {
		return aboutACountry;
	}
	public void setAboutACountry(boolean aboutACountry) {
		this.aboutACountry = aboutACountry;
	}
	public boolean isRequestedData() {
		return requestedData;
	}
	public void setRequestedData(boolean requestedData) {
		this.requestedData = requestedData;
	}
	public boolean isCanAnswer() {
		return canAnswer;
	}
	public void setCanAnswer(boolean canAnswer) {
		this.canAnswer = canAnswer;
	}


}

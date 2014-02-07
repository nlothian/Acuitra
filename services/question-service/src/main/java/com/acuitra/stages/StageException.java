package com.acuitra.stages;

public class StageException extends RuntimeException {
	private String errorCode;

	public StageException(String errorMessage, String errorCode) {
		super(errorMessage);
		
		this.errorCode = errorCode;
	}

	private static final long serialVersionUID = -8347771216493629249L;

	public String getErrorCode() {
		return errorCode;
	}

}

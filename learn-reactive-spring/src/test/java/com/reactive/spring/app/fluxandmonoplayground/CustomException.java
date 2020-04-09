package com.reactive.spring.app.fluxandmonoplayground;

public class CustomException extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6644576654829787352L;

	private String message;
	
	public CustomException(Throwable e) {
		this.message = e.getMessage();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

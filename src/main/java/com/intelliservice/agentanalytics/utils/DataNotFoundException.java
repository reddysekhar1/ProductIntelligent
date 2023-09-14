package com.intelliservice.agentanalytics.utils;

public class DataNotFoundException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private String massage;

	public DataNotFoundException(String message){
		this.massage = message;
	}

	public String getMassage() {
		return massage;
	}

	public void setMassage(String massage) {
		this.massage = massage;
	}

	
	
	

}

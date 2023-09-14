package com.intelliservice.agentanalytics.exception;

public class AgentAnalyticsException extends RuntimeException {
	
	
	private static final long serialVersionUID = 1L;

	

	private int code;
	private String message;
	private String reason;
	
		
	public AgentAnalyticsException(String message, String reason) {
		this.message = message;
		this.reason = reason;
	}

	
	public AgentAnalyticsException(int code, String message, String reason) {
		this.code = code;
		this.message = message;
		this.reason = reason;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public String getReason() {
		return reason;
	}

}

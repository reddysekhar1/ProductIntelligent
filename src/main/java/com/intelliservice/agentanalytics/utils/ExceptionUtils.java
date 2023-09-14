package com.intelliservice.agentanalytics.utils;

import com.intelliservice.agentanalytics.model.ErrorStatus;
import com.intelliservice.agentanalytics.model.Error;


public class ExceptionUtils {

	private ExceptionUtils() {}
	 public static Error handleError(String statusCode, String errors) {
		  ErrorStatus errorstatus = new ErrorStatus();
		  errorstatus.setResponseType("Failure");
		  errorstatus.setStatusCode(statusCode);
		  Error error = new Error();
		  error.setStatus(errorstatus);
		  error.setErrors(errors);
		  return error;
	  } 
}

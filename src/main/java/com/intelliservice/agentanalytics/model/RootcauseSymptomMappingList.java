package com.intelliservice.agentanalytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RootcauseSymptomMappingList {
	private	String symptoms;
	
	private String relevancy; 
	
	private String rootcause;
	@JsonProperty("leadingquestion")
	private String leadingQuestion;
	
	private String status;

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

	public String getRelevancy() {
		return relevancy;
	}

	public void setRelevancy(String relevancy) {
		this.relevancy = relevancy;
	}

	public String getRootcause() {
		return rootcause;
	}

	public void setRootcause(String rootcause) {
		this.rootcause = rootcause;
	}

	public String getLeadingQuestion() {
		return leadingQuestion;
	}

	public void setLeadingQuestion(String leadingQuestion) {
		this.leadingQuestion = leadingQuestion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
	

}

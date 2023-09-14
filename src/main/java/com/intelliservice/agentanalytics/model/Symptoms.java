package com.intelliservice.agentanalytics.model;

import java.util.List;

public class Symptoms {
	
	private String symptomsTitle;
	private String systemGeneretdSymptoms;
	private String leadingQuestion;
	private String issueDescription;
	private List<AttributsdataForSymptoms> productattributes;
	
	public String getSymptomsTitle() {
		return symptomsTitle;
	}
	public void setSymptomsTitle(String symptomsTitle) {
		this.symptomsTitle = symptomsTitle;
	}
	public String getSystemGeneretdSymptoms() {
		return systemGeneretdSymptoms;
	}
	public void setSystemGeneretdSymptoms(String systemGeneretdSymptoms) {
		this.systemGeneretdSymptoms = systemGeneretdSymptoms;
	}
	public String getLeadingQuestion() {
		return leadingQuestion;
	}
	public void setLeadingQuestion(String leadingQuestion) {
		this.leadingQuestion = leadingQuestion;
	}
	public String getIssueDescription() {
		return issueDescription;
	}
	public void setIssueDescription(String issueDescription) {
		this.issueDescription = issueDescription;
	}
	
	public List<AttributsdataForSymptoms> getProductattributes() {
		return productattributes;
	}
	public void setProductattributes(List<AttributsdataForSymptoms> productattributes) {
		this.productattributes = productattributes;
	}
	
	
	
	
}

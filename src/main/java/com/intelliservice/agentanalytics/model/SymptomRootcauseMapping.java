package com.intelliservice.agentanalytics.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SymptomRootcauseMapping {
	
	private	String symptoms;
	@JsonProperty("symptom_title")
	private	String symTitle;
	@JsonProperty("relevancy")
	private String relevancy; 
	private String rootcause;
	@JsonProperty("rootcause_title")
	private String rcTitle;
	
	private String leadingquestion;
	
	private String status;
	
	private String project;
	
	private List<ProductAttributesSet> productAttributes; 
	
	private List<String> description = new ArrayList<>();
	
	private List<String> resolution = new ArrayList<>();

	public String getSymptoms() {
		return symptoms;
	}

	public void setSymptoms(String symptoms) {
		this.symptoms = symptoms;
	}

	public String getSymTitle() {
		return symTitle;
	}

	public void setSymTitle(String symTitle) {
		this.symTitle = symTitle;
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

	public String getRcTitle() {
		return rcTitle;
	}

	public void setRcTitle(String rcTitle) {
		this.rcTitle = rcTitle;
	}

	public String getLeadingquestion() {
		return leadingquestion;
	}

	public void setLeadingquestion(String leadingquestion) {
		this.leadingquestion = leadingquestion;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

		public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public List<ProductAttributesSet> getProductAttributes() {
		return productAttributes;
	}

	public void setProductAttributes(List<ProductAttributesSet> productAttributes) {
		this.productAttributes = productAttributes;
	}

	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public List<String> getResolution() {
		return resolution;
	}

	public void setResolution(List<String> resolution) {
		this.resolution = resolution;
	}
	
	public void addResolution(String resolution) {
		this.resolution.add(resolution);
	}
	
	public void addDescription(String description) {
		this.description.add(description);
	}

	

}

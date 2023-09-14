package com.intelliservice.agentanalytics.model;

import java.util.List;

public class Insights {

    private String issueid;
	private String model;
	private String yom;
	private String resolution;
	private List<String> symptoms;
	private String description;
	private List<String> rootCause;
	private String product;
	private String project;
	
	
	public String getDescription() {
		return description;
	}
	public Insights setDescription(String description) {
		this.description = description;
		return this;
	}
	public List<String> getSymptoms() {
		return symptoms;
	}
	public Insights setSymptoms(List<String> symptoms) {
		this.symptoms = symptoms;
		return this;
	}
	public List<String> getRootCause() {
		return rootCause;
	}
	public Insights setRootCause(List<String> rootCause) {
		this.rootCause = rootCause;
		return this;
	}	
	
	public String getIssueid() {
		return issueid;
	}
	public Insights setIssueid(String issueid) {
		this.issueid = issueid;
		return this ;
	}
	
	public String getModel() {
		return model;
	}
	public Insights setModel(String model) {
		this.model = model;
		return this;
	}
	public String getYom() {
		return yom;
	}
	public Insights setYom(String yom) {
		this.yom = yom;
		return this;
	}
	public String getResolution() {
		return resolution;
	}
	public Insights setResolution(String resolution) {
		this.resolution = resolution;
		return this;
	}
	
	
	public String getProduct() {
		return product;
	}
	public Insights setProduct(String product) {
		this.product = product;
		return this;
	}
	public String getProject() {
		return project;
	}
	public Insights setProject(String project) {
		this.project = project;
		return this;
	}
	


}

package com.intelliservice.agentanalytics.model;

import java.util.List;

public class RootcauseDetails {
	private String rootcause;
	private String rootcauseTitle;
   // private String leadingquestion;
	private Symptom[] symptoms;
    private String[] resolution;
	private String project;
	private String[] description;
	//private AttributsdataForSymptoms[] productattributes;
	private List<ProductAttributesSet> productattributes;
	public String getRootcause() {
		return rootcause;
	}
	public void setRootcause(String rootcause) {
		this.rootcause = rootcause;
	}
	public String getRootcauseTitle() {
		return rootcauseTitle;
	}
	public void setRootcauseTitle(String rootcauseTitle) {
		this.rootcauseTitle = rootcauseTitle;
	}
	public Symptom[] getSymptoms() {
		return symptoms;
	}
	public void setSymptoms(Symptom[] symptoms) {
		this.symptoms = symptoms;
	}
	public String[] getResolution() {
		return resolution;
	}
	public void setResolution(String[] resolution) {
		this.resolution = resolution;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public List<ProductAttributesSet> getProductattributes() {
		return productattributes;
	}
	public void setProductattributes(List<ProductAttributesSet> productattributes) {
		this.productattributes = productattributes;
	}
	public String[] getDescription() {
		return description;
	}
	public void setDescription(String[] description) {
		this.description = description;
	}

	

}

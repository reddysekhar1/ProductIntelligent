package com.intelliservice.agentanalytics.model;

import java.util.List;

public class Source {
	private String issueid;
	private String product;
	private String model;
	private String yom;
	private String description;
	private List<String> symptoms;
	private List<String> rootCause;
	private String resolution;
	
	public String getIssueid() {
		return issueid;
	}
	public Source setIssueid(String issueid) {
		this.issueid = issueid;
		return this;
	}
	public String getProduct() {
		return product;
	}
	public Source setProduct(String product) {
		this.product = product;
		return this;
	}
	public String getModel() {
		return model;
	}
	public Source setModel(String model) {
		this.model = model;
		return this;
	}
	public String getYom() {
		return yom;
	}
	public Source setYom(String yom) {
		this.yom = yom;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Source setDescription(String description) {
		this.description = description;
		return this;
	}
	public List<String> getSymptoms() {
		return symptoms;
	}
	public Source setSymptoms(List<String> symptoms) {
		this.symptoms = symptoms;
		return this;
	}
	public List<String> getRootCause() {
		return rootCause;
	}
	public Source setRootCause(List<String> rootCause) {
		this.rootCause = rootCause;
		return this;
	}
	public String getResolution() {
		return resolution;
	}
	public Source setResolution(String resolution) {
		this.resolution = resolution;
		return this;
	}
}

package com.intelliservice.agentanalytics.model;

import java.util.List;

public class InsightsCluster {
    private String id;
	
	private String issueid;
	private String product;
	private String model;
	private String yom;
	private String description;
	private List<String> symptoms;
	private List<String> rootCause;
	private String resolution;
	private Clusteres clusteres;
	public String getId() {
		return id;
	}
	public InsightsCluster setId(String id) {
		this.id = id;
		return this;
	}
	public Clusteres getClusteres() {
		return clusteres;
	}
	public InsightsCluster setClusteres(Clusteres clusteres) {
		this.clusteres = clusteres;
		return this;
	}
	public String getIssueid() {
		return issueid;
	}
	public InsightsCluster setIssueid(String issueid) {
		this.issueid = issueid;
		return this;
	}
	public String getProduct() {
		return product;
	}
	public InsightsCluster setProduct(String product) {
		this.product = product;
		return this;
	}
	public String getModel() {
		return model;
	}
	public InsightsCluster setModel(String model) {
		this.model = model;
		return this;
	}
	public String getYom() {
		return yom;
	}
	public InsightsCluster setYom(String yom) {
		this.yom = yom;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public InsightsCluster setDescription(String description) {
		this.description = description;
		return this;
	}
	public List<String> getSymptoms() {
		return symptoms;
	}
	public InsightsCluster setSymptoms(List<String> symptoms) {
		this.symptoms = symptoms;
		return this;
	}
	public List<String> getRootCause() {
		return rootCause;
	}
	public InsightsCluster setRootCause(List<String> rootCause) {
		this.rootCause = rootCause;
		return this;
	}
	public String getResolution() {
		return resolution;
	}
	public InsightsCluster setResolution(String resolution) {
		this.resolution = resolution;
		return this;
	}
}

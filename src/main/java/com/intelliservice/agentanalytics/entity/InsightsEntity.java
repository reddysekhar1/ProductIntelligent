package com.intelliservice.agentanalytics.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Document(indexName=ProductTriageConstants.PRODUCT_TRIAGE_COLL)
public class InsightsEntity {
	@Id	
	private String id;
	@Field(name = "issueid")
	private String issueid;	

	@Field(name = "model")
	private String model;

	@Field(name = "yom")
	private String yom;

	@Field(name = "resolution")
	private String resolution;

	@Field(name = "symptoms")
	private List<String> symptoms;

	@Field(name = "rootcause")
	private List<String> rootCause;

	@Field(name = "product")
	private String product;

	@Field(name = "description")
	private String description;

	public String getDescription() {
		return description;
	}
	public InsightsEntity setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getModel() {
		return model;
	}
	public InsightsEntity setModel(String model) {
		this.model = model;
		return this;
	}
	public String getYom() {
		return yom;
	}
	public InsightsEntity setYom(String yom) {
		this.yom = yom;
		return this;
	}
	public String getResolution() {
		return resolution;
	}
	public InsightsEntity setResolution(String resolution) {
		this.resolution = resolution;
		return this;
	}
	public List<String> getSymptoms() {
		return symptoms;
	}
	public InsightsEntity setSymptoms(List<String> symptoms) {
		this.symptoms = symptoms;
		return this;
	}
	public List<String> getRootCause() {
		return rootCause;
	}
	public InsightsEntity setRootCause(List<String> rootCause) {
		this.rootCause = rootCause;
		return this;
	}

	public String getProduct() {
		return product;
	}
	public InsightsEntity setProduct(String product) {
		this.product = product;
		return this;
	}
	public InsightsEntity setIssueid(String issueid) {
		this.issueid = issueid;
		return this ;
	}

	public String getIssueid() {
		return issueid;
	}

}

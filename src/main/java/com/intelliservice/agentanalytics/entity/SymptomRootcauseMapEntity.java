package com.intelliservice.agentanalytics.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.intelliservice.agentanalytics.model.ProductAttributesSet;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Document(indexName=ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION)
public class SymptomRootcauseMapEntity {
	
	@Id
	private	String id;
	
	@Field(name = "symptoms")
	private	String symptoms;
	
	@Field(name = "symptom_title")
	private	String symTitle;
	
	@Field(name = "relevency")
	private String relevency;
	
	@Field(name = "rootcause")
	private String rootcause;
	
	@Field(name = "rootcause_title")
	private String rcTitle;
	
	@Field(name = "leadingquestion")
	private String leadingQuestion;
	
	@Field(name = "status")
	private String status;
	
	@Field(name = "project")
	private String project;
	
	@Field(name = "productattributes")
	private List<ProductAttributesSet> productAttributes;
	
	@Field(name = "description")
	private List<String> description;
	
	@Field(name = "resolution")
	private List<String> resolution;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getRelevency() {
		return relevency;
	}

	public void setRelevency(String relevency) {
		this.relevency = relevency;
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
}

package com.intelliservice.agentanalytics.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Document(indexName=ProductTriageConstants.PRODUCT_ATTRIBUTES_COLL)
public class ProductAttributesEntity {
	@Id	
	private String id;
	@Field(name = "attribute_name")
	private String attributeName;
	@Field(name = "attribute_data")
	private List<String> attributeData;
	@Field(name = "project")
	private String project;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public List<String> getAttributeData() {
		return attributeData;
	}
	public void setAttributeData(List<String> attributeData) {
		this.attributeData = attributeData;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}	


	}

package com.intelliservice.agentanalytics.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductAttributes {
	@JsonProperty("attribute_name")
	private String attributeName;
	@JsonProperty("attribute_data")
	private List<String> attributeData;
	@JsonProperty("project")
	private String project;
	public ProductAttributes() {}

	public ProductAttributes(String strName, List dataList,String project) {
		attributeName = strName;
		attributeData = dataList;
		project= project;
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

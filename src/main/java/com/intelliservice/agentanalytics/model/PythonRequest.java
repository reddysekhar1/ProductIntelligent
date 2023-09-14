package com.intelliservice.agentanalytics.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(value = Include.NON_NULL)
public class PythonRequest implements Serializable {
	
	
	private static final long serialVersionUID = -3894536477786007335L;
	
	@JsonProperty("collection") 
	private String collection= null;
	
	@JsonProperty("project") 
	private String project= null;
	
	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
}

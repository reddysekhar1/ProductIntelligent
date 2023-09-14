package com.intelliservice.agentanalytics.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceMap {
	@JsonProperty("source_name")
	private String sourceName;
	@JsonProperty("source_fields")
	private List<String> sourceFields;
	@JsonProperty("project")
	private String project;

	public SourceMap() {}
	
	public SourceMap(String srcName, List<String> srcFields,String project) {
		sourceName = srcName;
		sourceFields = srcFields;
		project = project;		
	}
	
	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public List<String> getSourceFields() {
		return sourceFields;
	}

	public void setSourceFields(List<String> sourceFields) {
		this.sourceFields = sourceFields;
	}

	@Override
	public String toString() {
		return "SourceMap [sourceName=" + sourceName + ", sourceFields=" + sourceFields  + ", project=" + project+" ]";
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}


}

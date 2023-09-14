package com.intelliservice.agentanalytics.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Document(indexName=ProductTriageConstants.SOURCEMAP_COLLECTION)
public class SourceMapEntity {
	
	@Id
	private String id;
	
	@Field(name = "source_name")
	private String sourceName;
	
	@Field(name = "source_fields")
	private List<String> sourceFields;
	@Field(name = "project")
	private String project;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	

	
	

	
}

package com.intelliservice.agentanalytics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(value = Include.NON_NULL)
public class PythonRequestModel extends PythonRequest {

	private static final long serialVersionUID = -2485893852693207538L;
	
	@JsonProperty("ngram_range_min") 
	private Integer ngramRangeMin = null;
	@JsonProperty("embedding") 
	private String embedding = null;
	@JsonProperty("ngram_range_max") 
	private Integer ngramRangeMax = null;
	@JsonProperty("no_topics") 
	private String noTopics = null;
	@JsonProperty("project") 
	private String project = null;

	
	
	
	public int getNgramRangeMin() {
		return ngramRangeMin;
	}
	public void setNgramRangeMin(int ngramRangeMin) {
		this.ngramRangeMin = ngramRangeMin;
	}
	public String getEmbedding() {
		return embedding;
	}
	public void setEmbedding(String embedding) {
		this.embedding = embedding;
	}
	public int getNgramRangeMax() {
		return ngramRangeMax;
	}
	public void setNgramRangeMax(int ngramRangeMax) {
		this.ngramRangeMax = ngramRangeMax;
	}
	public String getNoTopics() {
		return noTopics;
	}
	public void setNoTopics(String noTopics) {
		this.noTopics = noTopics;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}

	
	
}

package com.intelliservice.agentanalytics.pythonapi.model;

import java.io.Serializable;

public class PythonBuildModel implements Serializable  {
	
	private static final long serialVersionUID = -8861503822299350477L;
	private int ngramRangeMin;
	private int ngramRangeMax;
	private String embedding;
	private Serializable noTopics;
	private String collection;
	public int getNgramRangeMin() {
		return ngramRangeMin;
	}
	public void setNgramRangeMin(int ngramRangeMin) {
		this.ngramRangeMin = ngramRangeMin;
	}
	public int getNgramRangeMax() {
		return ngramRangeMax;
	}
	public void setNgramRangeMax(int ngramRangeMax) {
		this.ngramRangeMax = ngramRangeMax;
	}
	public String getEmbedding() {
		return embedding;
	}
	public void setEmbedding(String embedding) {
		this.embedding = embedding;
	}
	public Serializable getNoTopics() {
		return noTopics;
	}
	public void setNoTopics(Serializable noTopics) {
		this.noTopics = noTopics;
	}
	public String getCollection() {
		return collection;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}

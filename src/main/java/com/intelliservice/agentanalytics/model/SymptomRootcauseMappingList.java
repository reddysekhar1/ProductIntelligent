package com.intelliservice.agentanalytics.model;



public class SymptomRootcauseMappingList {
	
	
	private String symptoms;
	
    private String rootcause; 
	
	private String rootcausetitle;
	
	private String relevancy;
	
	private String status;
	
	public String getSymptoms() {
		return symptoms;
	}

	public SymptomRootcauseMappingList setSymptoms(String symptoms) {
		this.symptoms = symptoms;
		return this;
	}

	public String getRootcause() {
		return rootcause;
	}

	public SymptomRootcauseMappingList setRootcause(String rootcause) {
		this.rootcause = rootcause;
		return this;
	}

	public String getRootcausetitle() {
		return rootcausetitle;
	}

	public SymptomRootcauseMappingList setRootcausetitle(String rootcausetitle) {
		this.rootcausetitle = rootcausetitle;
		return this;
	}

	public String getRelevancy() {
		return relevancy;
	}

	public SymptomRootcauseMappingList setRelevancy(String relevancy) {
		this.relevancy = relevancy;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public SymptomRootcauseMappingList setStatus(String status) {
		this.status = status;
		return this;
	}

	
}

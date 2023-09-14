package com.intelliservice.agentanalytics.model;

import java.util.List;

public class ProductAttributesSet {
	private String attribute_name;
	private List<String> attribute_data;
	public String getAttribute_name() {
		return attribute_name;
	}
	public void setAttribute_name(String attribute_name) {
		this.attribute_name = attribute_name;
	}
	public List<String> getAttribute_data() {
		return attribute_data;
	}
	public void setAttribute_data(List<String> attribute_data) {
		this.attribute_data = attribute_data;
	}
	
	

}

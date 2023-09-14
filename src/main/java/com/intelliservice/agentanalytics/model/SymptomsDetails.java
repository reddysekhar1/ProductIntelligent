package com.intelliservice.agentanalytics.model;

import java.util.List;

public class SymptomsDetails {
	
	    private String symptoms;
	    private String leadingquestion;
		private String[] description;
		private RootCause[] rootcauses;
	    private String[] resolution;
		private String project;
	//	private AttributsdataForSymptoms[] productattributes;
		private List<ProductAttributesSet> productattributes;
		public String getSymptoms() {
			return symptoms;
		}
		public void setSymptoms(String symptoms) {
			this.symptoms = symptoms;
		}
		public String getLeadingquestion() {
			return leadingquestion;
		}
		public void setLeadingquestion(String leadingquestion) {
			this.leadingquestion = leadingquestion;
		}
		public String[] getDescription() {
			return description;
		}
		public void setDescription(String[] description) {
			this.description = description;
		}
		
		
	
		public RootCause[] getRootcauses() {
			return rootcauses;
		}
		public void setRootcauses(RootCause[] rootcauses) {
			this.rootcauses = rootcauses;
		}
		public String getProject() {
			return project;
		}
		public void setProject(String project) {
			this.project = project;
		}
		public String[] getResolution() {
			return resolution;
		}
		public void setResolution(String[] resolution) {
			this.resolution = resolution;
		}
		public List<ProductAttributesSet> getProductattributes() {
			return productattributes;
		}
		public void setProductattributes(List<ProductAttributesSet> productattributes) {
			this.productattributes = productattributes;
		}
		
	
	
		
		
		
}

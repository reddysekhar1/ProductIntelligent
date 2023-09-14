package com.intelliservice.agentanalytics.services;

import java.util.List;

import org.json.JSONArray;

import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;

public interface InsightsService {
	List<Insights> getSimilarIssues(String issueDescription);
	//String getSimilarIssues(String issueDescription,String product,String model);
	String getSimilarIssues(String issueDescription,String inputJson,String project);
	List<ProductAttributes> getProductAttribute(String project);
	JSONArray getIssueClusters(String issueDescription,String json,String project);
	String getRecommendedsolutions (String issueDescription,String json);

}

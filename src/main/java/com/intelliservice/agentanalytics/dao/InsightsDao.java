package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;

import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;

public interface InsightsDao {
	List<Insights> getSimilarIssues(String searchText);	
	//List<Insights> getSimilarIssues(String searchText,String product,String model);	
	JSONArray getSimilarIssues(String searchText,String inputJson,String project) throws IOException, ParseException;
	List<ProductAttributes> getProductAttribute(String project);
	String getIssueClusters(String issueDescription,String json,String project);
	String getRecommendedsolutions (String issueDescription,String json);

}

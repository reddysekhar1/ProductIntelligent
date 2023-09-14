package com.intelliservice.agentanalytics.dao;

import java.io.IOException;

import com.intelliservice.agentanalytics.model.SourceMap;

public interface ProductTriageDao {
	
	public String getProductTriage(String collectionName,String inputJson, SourceMap sourceMap, String rootaggs,String project) throws IOException ;
	public String saveTriageSummary(String inputJson) throws IOException ;
}

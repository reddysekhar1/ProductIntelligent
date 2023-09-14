package com.intelliservice.agentanalytics.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.TriageConfiguration;

public interface GenericCollectionService {
	List<String> getCollections() throws IOException;	
	List<String> getFields(String collectionName) throws IOException;	
	List<String> getFieldValues(String collectionName,String fieldValue,String project)throws IOException;
	String createUpdtaeConfigurationIndex(TriageConfiguration configuration) throws AgentAnalyticsException, IOException;
    String getConfigData(String collectionName,String fieldName,String project) throws AgentAnalyticsException, IOException;
    String saveConfigData(String collectionName,String fieldName,String project,String inputJSON) throws AgentAnalyticsException, IOException;

}

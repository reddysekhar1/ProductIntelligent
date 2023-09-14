package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.util.List;

import com.intelliservice.agentanalytics.model.TriageConfiguration;

public interface GenericCollectionDao {
	List<String> getCollections() throws IOException;
	List<String> getFields(String collectionName) throws IOException;
	List<String> getFieldValues(String collectionName,String fieldname)throws IOException;
	String createUpdtaeConfigurationIndex(TriageConfiguration configuration)throws IOException;
	TriageConfiguration getConfigurationData(String name);
	String getDataByIdField(String collectionName,String fieldName,String docId) throws IOException;
    String createUpdtaeByIdField(String collectionName,String fieldName,String docId,String inputJSON) throws IOException;

}

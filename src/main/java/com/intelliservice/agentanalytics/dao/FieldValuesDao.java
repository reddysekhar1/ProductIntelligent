package com.intelliservice.agentanalytics.dao;

import java.util.List;

public interface FieldValuesDao {
	
	List<String> getFieldValues(String collectionName,String fieldValue,String project);
}

package com.intelliservice.agentanalytics.services;

import java.io.IOException;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intelliservice.agentanalytics.dao.FieldValuesDao;
import com.intelliservice.agentanalytics.dao.GenericCollectionDao;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
@Service
public class GenericCollectionServiceImpl implements GenericCollectionService {
	@Autowired
	GenericCollectionDao genericCollectionDao;
	@Autowired
	FieldValuesDao fieldValuesDao;

	@Override
	public List<String> getCollections() throws IOException {		
		return genericCollectionDao.getCollections();
	}

	@Override
	public List<String> getFields(String collectionName) throws IOException {
		return genericCollectionDao.getFields(collectionName);
	}

	@Override
	public List<String> getFieldValues(String collectionName, String fieldValue,String project) {		
		return fieldValuesDao.getFieldValues(collectionName, fieldValue,project);

	}

	@Override
	public String createUpdtaeConfigurationIndex(TriageConfiguration configuration) throws AgentAnalyticsException, IOException {
		return genericCollectionDao.createUpdtaeConfigurationIndex(configuration);
		
	}
	
    @Override
    public String getConfigData(String collectionName,String fieldName,String project) throws AgentAnalyticsException, IOException {
    return genericCollectionDao.getDataByIdField(collectionName,fieldName,project);
                            
    }
    
    @Override
    public String saveConfigData(String collectionName,String fieldName,String project,String inputJSON) throws AgentAnalyticsException, IOException {
     JSONObject jsonObj = new JSONObject(inputJSON);
     String fldName = jsonObj.keys().next();
     JSONObject valJSON = jsonObj.getJSONObject(fldName);
     return genericCollectionDao.createUpdtaeByIdField(collectionName,fldName,project,valJSON.toString());
    }

}

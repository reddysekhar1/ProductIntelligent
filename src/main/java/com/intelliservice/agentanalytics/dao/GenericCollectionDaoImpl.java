package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.utils.ESQueries;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;
@Repository
public class GenericCollectionDaoImpl implements GenericCollectionDao{
	private static final Logger log = LoggerFactory.getLogger(GenericCollectionDaoImpl.class);
	@Autowired
	ESClient esClient;
	@Override
	public List<String> getCollections() throws IOException {
		String result = esClient.performRequest(ProductTriageConstants.GET, "/_all/_mapping/", null);
		List<String> list=new ArrayList<>();
		JSONObject jsonObject = new JSONObject(result);
		Iterator<String> keys = jsonObject.keys();
		while (keys.hasNext()) {
			String jsonKey = keys.next();
			list.add(jsonKey);
		}
		return list;
	}
	@Override
	public List<String> getFields(String collectionName) throws IOException {
		String result = esClient.performRequest(ProductTriageConstants.GET, "/" + collectionName + "/_mapping/", null);
		JSONObject jsonObj = new JSONObject(result);
		JSONObject quoteJson = jsonObj.getJSONObject(collectionName).getJSONObject("mappings")

				.getJSONObject("properties");
		List<String> list = new ArrayList<>();
		Iterator<String> keys = quoteJson.keys();
		while (keys.hasNext()) {
			String jsonKey = keys.next();
			list.add(jsonKey);
		}
		return list;
	}
	@Override
	public List<String> getFieldValues(String collectionName,String fieldname)throws IOException
	{
		String keyVal=null;
		List<String> list=new  ArrayList<>();
		TriageConfiguration triageConfiguration = getConfigurationData(ProductTriageConstants.SYMPTOMS_THRESHOLD); 
		String result = esClient.performRequest("POST", "/" + collectionName + "/_search/", String.format(triageConfiguration.getValue().trim(),"project"));
		JSONObject jsonObj = new JSONObject(result);
		JSONObject quoteJson = jsonObj.getJSONObject("aggregations").getJSONObject("my-agg-name");
		JSONArray recs = quoteJson.getJSONArray("buckets"); 
		for (int i = 0; i < recs.length(); ++i) {
			JSONObject jsn = recs.getJSONObject(i);
			keyVal = jsn.getString("key");
			list.add(keyVal);
		}
		return list;

	}
	@Override
	public String createUpdtaeConfigurationIndex(TriageConfiguration configuration) throws IOException {
		String message = "%1$s record successfully %2$s.";
		String url = "/"+ProductTriageConstants.TRIAGE_CONFIGURATION_COLL+ProductTriageConstants.DOC+"/"+configuration.getName();
		JSONObject inputJson = new JSONObject(configuration);
		String result = esClient.performRequest("POST",url, inputJson.toString());
		JSONObject output = new JSONObject(result);
		int dataCount = output.getJSONObject("_shards").getInt("successful");
		String status = output.getString("result");
		return String.format(message,dataCount,status);
	}
	@Override
	public TriageConfiguration getConfigurationData(String name) {
		TriageConfiguration triageConfiguration = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String url = "/"+ProductTriageConstants.TRIAGE_CONFIGURATION_COLL+ProductTriageConstants.SEARCH;
			String inputBody = "{\"query\": {\"terms\": {\"_id\": [\"%s\"] }}}";
			inputBody = String.format(inputBody, name);
			String result = esClient.performRequest("POST",url, inputBody);
			JSONObject returnJson = new JSONObject(result);
			JSONObject dataJson = returnJson.getJSONObject("hits").getJSONArray("hits").getJSONObject(0).getJSONObject("_source");
			triageConfiguration = objectMapper.readValue(dataJson.toString(), TriageConfiguration.class);
		}catch(Exception e){
			log.error("Error inside getConfigurationData : " , e);
		}
		return triageConfiguration;
	}
	
    @Override
    public String getDataByIdField(String collectionName,String fieldName,String docId) throws IOException {
    	String url = null;
        String data = null;
        if(fieldName == null || fieldName == "") {
         url = "/"+collectionName+ProductTriageConstants.DOC+"/"+docId;
         String result = esClient.performRequest("GET",url, null);
         JSONObject returnJson = new JSONObject(result);
         if(returnJson.getBoolean("found")) {
         JSONObject dataJson = returnJson.getJSONObject("_source");
         data = dataJson.toString();
         }
        }
        else {
         url = "/"+collectionName+"_source/"+docId+"?_source_includes="+fieldName;
        data = esClient.performRequest("GET",url, null);
        }                                               
        return data;                     
    }
    @Override
    public String createUpdtaeByIdField(String collectionName,String fieldName,String docId,String inputJSON) throws IOException {
    	String message = "%1$s record successfully %2$s.";
        String url = "/"+collectionName+"/_update/"+docId;
        String requestBody = "{ \"script\" : { \"source\": \"ctx._source."+fieldName+" = params.newdata\", \"lang\": \"painless\", \"params\" : {\"newdata\" : "+inputJSON+" }},\"upsert\": {\""+fieldName+"\": "+inputJSON+" } }";
        String result = esClient.performRequest("POST",url, requestBody);
        JSONObject output = new JSONObject(result);
        int dataCount = output.getJSONObject("_shards").getInt("successful");
        String status = output.getString("result");
        return String.format(message,dataCount,status);                       
    }


}

package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;


@Service
public class ProductTriageDaoImpl implements ProductTriageDao {
	
	@Autowired
	RestHighLevelClient client;
	
	@Autowired
	ESClient esClient;
	
	@Autowired
	GenericCollectionDao genericCollectionDao;

	@Override
	public String getProductTriage(String collectionName,String inputJson,SourceMap sourceMap,String rootaggs,String project) throws IOException {
		JSONObject inputObj = new JSONObject(inputJson);
		Set<String> inputKeys = inputObj.keySet();
		//String searchString=null;
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.RESULT_SIZE); 
		JSONArray filterTermsArr = new JSONArray();
	
		  if(!inputObj.keySet().isEmpty())
		  { 
			  //Iterating to product attributes filter 
			  for (String key : inputKeys) {
				  JSONObject jsonObject = new JSONObject(); 
				  jsonObject.put("terms", new JSONObject().put(key + ProductTriageConstants.KEYWORD,inputObj.getJSONArray(key))); 
				  filterTermsArr.put(jsonObject); 
				 } 
		}
		  //adding project filter 
		filterTermsArr.put(new JSONObject().put("term", new JSONObject().put("project" + ProductTriageConstants.KEYWORD, project)));
		  
		String filedString =  ",\"aggs\": {"
			+ "\"%s\":{"
				+ "\"terms\": {"
					+ "\"field\":\"%s.keyword\",\"size\":"+Integer.parseInt(triageConfiguration.getValue().trim())+"}"; 
		String endCurlyBrackets = "}";
		

		  //String searchString	  =		  "{\"size\": 0, \"query\": {\"bool\": {\"filter\": [ {\"terms\": {\"product.keyword\": [\"P1\"]}},{\"term\": {\"project.keyword\": \""	  +project+"\"}}]}} "; 
		  String searchString =		  "{\"size\": 0, \"query\": {\"bool\": {\"filter\":"+filterTermsArr.toString()	  +" }}";
		 
		searchString = searchString.concat(String.format(filedString,rootaggs,rootaggs));
		endCurlyBrackets = endCurlyBrackets.concat("}}");
		List<String> fiels = sourceMap.getSourceFields();
		for(String str : fiels) {
			String result = String.format(filedString, str,str); 
			searchString = searchString.concat(result);
			endCurlyBrackets = endCurlyBrackets.concat("}}");
		}
		searchString = searchString.concat(endCurlyBrackets);	
		System.out.println(searchString);
		return esClient.performRequest("POST", "/" + collectionName +"/_search/", searchString );
	}
	
	@Override
	public String saveTriageSummary(String inputJson) throws IOException {
		
		return esClient.performRequest("POST", "/triagesummary/_doc/", inputJson );
	}
}

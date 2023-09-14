package com.intelliservice.agentanalytics.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.BasicResponseData;
import com.intelliservice.agentanalytics.pythonapi.services.ApiProxy;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;




@Service
public class BulkApiBuilderServiceImpl extends ApiProxy implements BulkApiBuilderService{
	private static final Logger log = LoggerFactory.getLogger(BulkApiBuilderServiceImpl.class);
	
	@Override
	public String runBulkApi(String jsonObject) {
		log.info("Inside BulkApiBuilderService");
		String message = "Success";
		String fullUrl;
		HttpHeaders headers = getHeaders();
		HttpEntity<?> entity;
		String method;
		String url = "";
		Object requestData;
		JSONObject requestJson;
		ResponseEntity<BasicResponseData> pythonResponse = null;
				
		try {
			final ObjectMapper objectMapper = new ObjectMapper();
			Map<String,String>[] inputJsonArray = objectMapper.readValue(jsonObject, Map[].class);
			List<Map<String,String>> inputList = new ArrayList<>(Arrays.asList(inputJsonArray));
			inputList.sort((Map<String,String> m1, Map<String,String> m2) -> Integer.parseInt(String.valueOf(m1.get("sequence"))) - (Integer.parseInt(String.valueOf(m2.get("sequence")))));
			for(Map<String,String> inputData : inputList) {
				method = inputData.get("method");
				url = inputData.get("url");
				requestData = inputData.get("requestdata");
				requestJson = new JSONObject((Map)requestData);
				entity = new HttpEntity<>(requestData,headers);
				fullUrl = getSelfHost()+url;
			
				restTemplate.setMessageConverters(getJsonMessageConverters());
				if(ProductTriageConstants.POST.equalsIgnoreCase(method)) {
					pythonResponse = restTemplate.exchange(fullUrl, HttpMethod.POST,entity, BasicResponseData.class);
				}else if(ProductTriageConstants.PUT.equalsIgnoreCase(method)) {
					pythonResponse = restTemplate.exchange(fullUrl, HttpMethod.PUT,entity, BasicResponseData.class);
				}else if(ProductTriageConstants.GET.equalsIgnoreCase(method)) {
					if(url.contains("{")) {
						String str = url.substring(url.indexOf('{')+1, url.indexOf('}'));
						String replaceStr = url.substring(url.indexOf('{'), url.indexOf('}')+1);
						fullUrl = fullUrl.replace(replaceStr, requestJson.getString(str));
					}
					pythonResponse = restTemplate.getForEntity(fullUrl, BasicResponseData.class);
				}		
				
			}		
		
		}catch (Exception e) {
			throw new AgentAnalyticsException(e.getMessage(),"Error happen on "+url);
		}
 
		return pythonResponse!= null ? message : "";
	}
}

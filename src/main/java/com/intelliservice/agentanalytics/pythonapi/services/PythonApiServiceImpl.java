package com.intelliservice.agentanalytics.pythonapi.services;

import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelliservice.agentanalytics.dao.ProductDetailsDao;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.PythonRequest;
import com.intelliservice.agentanalytics.model.PythonRequestModel;


@Service
public class PythonApiServiceImpl extends ApiProxy implements PythonApiService {
	@Autowired
	ProductDetailsDao productDetailsDao;
	private static final Logger log = LoggerFactory.getLogger(PythonApiServiceImpl.class);
	@Override
	public String getPythonApiResponse(JSONObject pythonBuildModel,String baseUrl, PythonRequest pythonRequest,String status,String status1,String project) {
		log.info("Start the PythonApiServiceImpl.getPythonApiResponse()");
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		productDetailsDao.monitoringLog(baseUrl,status,pythonBuildModel.getString("project"),timestamp);
		ObjectMapper mapper = new ObjectMapper();
		PythonRequest model = null;
		long minutes = 0;
		long seconds = 0;
		ResponseEntity pythonResponse;
		try {
			if(pythonRequest instanceof PythonRequestModel)
				model = mapper.readValue(pythonBuildModel.toString(),PythonRequestModel.class);
			else
				model = mapper.readValue(pythonBuildModel.toString(),PythonRequest.class);
			HttpHeaders headers = getHeaders();
			HttpEntity<?> entity = new HttpEntity<>(model,headers);
			restTemplate.setMessageConverters(getJsonMessageConverters());
			
			String pythonBaseUrl = this.getPythonBaseUrl()+baseUrl;
			log.info("Python search url ::{}",pythonBaseUrl);
			log.info("pythonBuildModel data {}",pythonBuildModel);
			Long start = new Date().getTime();
			pythonResponse = restTemplate.exchange(pythonBaseUrl, HttpMethod.POST,entity, String.class);
			Long end = new Date().getTime();
			long diff = end-start;
			minutes = (diff / 1000) / 60;
			seconds = (diff / 1000) % 60;
			
		
			log.info("getPythonApiResponse Object## {}", pythonResponse.getBody());
			
			
		}catch (Exception e) {
			throw new AgentAnalyticsException(e.getMessage(),"Error inside the getPythonApiResponse");
		}finally {
			log.info("Time taken by API call minutes={} and seconds= {} ", minutes, seconds);
		}
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		productDetailsDao.monitoringLog(baseUrl,status1,pythonBuildModel.getString("project"),timestamp1);
		return pythonResponse.getBody().toString();
	}

}

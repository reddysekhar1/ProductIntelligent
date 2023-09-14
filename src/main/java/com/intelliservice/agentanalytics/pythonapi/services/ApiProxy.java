package com.intelliservice.agentanalytics.pythonapi.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
@PropertySource(value = "classpath:application.yml")
public class ApiProxy<T> {
	
	@Autowired
	protected RestTemplate restTemplate;	
	
	@Value("${pythonUrl.url}")
	private String pythonBaseUrl;
	
	@Value("${selfUrl.url}")
	private String selfHost;

	public String getPythonBaseUrl() {
		return pythonBaseUrl;
	}

	public void setPythonBaseUrl(String pythonBaseUrl) {
		this.pythonBaseUrl = pythonBaseUrl;
	}

	
	public HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;

	}

	public String getSelfHost() {
		return selfHost;
	}
	
	
	  public List<HttpMessageConverter<T>> getJsonMessageConverters() {
		  ObjectMapper objectMapper = new ObjectMapper();
		  objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		  objectMapper.configure(Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
		  List<HttpMessageConverter<T>> converters = new ArrayList<>();
		  converters.add((HttpMessageConverter<T>) new MappingJackson2HttpMessageConverter(objectMapper));
	  return converters; 
	  }
}

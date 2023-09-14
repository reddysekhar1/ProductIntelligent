package com.intelliservice.agentanalytics.config;

import java.io.IOException;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;

@Component
public class ESClient {

	private static final Logger logger = LoggerFactory.getLogger(ESClient.class);

	public static final String SECRET_KEY = "agentananalytics@erx&key#@encryption@engg";

	@Autowired
	RestClient restClient;
	
	private static final String UTFCHAR = "UTF-8";
	private static final String ERROR = "error";
	
	public String performRequest(String method, String path, String requestBody) throws IOException {
		logger.debug("method: {}" , method);
		logger.debug("path: {}" , path);
		logger.debug("requestBody: {}" , requestBody);
		try {
			if (method.equals("POST") || method.equals("PUT")) {
				Request request = new Request(method, path);
				request.setJsonEntity(requestBody);
				Response response = restClient.performRequest(request);
				return EntityUtils.toString(response.getEntity(), UTFCHAR);
			} else {
				Request request = new Request(method, path);
				Response response = restClient.performRequest(request);
				return EntityUtils.toString(response.getEntity(), UTFCHAR);
			}
		} catch (ResponseException ex) {
			JSONObject responseObj = new JSONObject(EntityUtils.toString(ex.getResponse().getEntity(), UTFCHAR));
			if (responseObj.has(ERROR)) {
				logger.error("Exception in fetching data " , ex);
				throw new AgentAnalyticsException(ex.getResponse().getStatusLine().getStatusCode(),
						responseObj.getJSONObject(ERROR).getString("reason"), responseObj.getJSONObject(ERROR).getString("reason"));
			} else {
				return EntityUtils.toString(ex.getResponse().getEntity(), UTFCHAR);
			}
		}
	}


	public String performBulkRequest(String method, String path, String requestBody) throws IOException{
		logger.debug("method: {}" , method);
		logger.debug("path: {}" , path);
		logger.debug("requestBody: {}" , requestBody);
		Request request = new Request(method, path);
		request.setJsonEntity(requestBody);
		Response response = restClient.performRequest(request);
		return EntityUtils.toString(response.getEntity(), UTFCHAR);
	}
}

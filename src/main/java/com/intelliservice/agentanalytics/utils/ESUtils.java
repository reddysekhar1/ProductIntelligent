package com.intelliservice.agentanalytics.utils;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
@Component
public class ESUtils {
	@Autowired
	ESClient esClient;
	public JSONArray executeESsearchQry(String method,String requestPath,String requestBody) throws IOException {
		JSONArray responseList = new JSONArray();
		String response = esClient.performRequest(method, requestPath, requestBody);
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject src = results.getJSONObject(result).getJSONObject("_source");
					src.put("id", results.getJSONObject(result).getString("_id"));
					//src.put("score", results.getJSONObject(result).getDouble("_score"));
					double b = results.getJSONObject(result).getDouble("_score")/resultJson.getJSONObject("hits").getDouble("max_score");
					src.put("score", Math.round(b*90)+"%");
					responseList.put(src);	
				}
			}
		}
		return responseList;
	}

}

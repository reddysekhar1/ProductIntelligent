package com.intelliservice.agentanalytics.pythonapi.services;

import org.json.JSONObject;

import com.intelliservice.agentanalytics.model.PythonRequest;

public interface PythonApiService {
	public String getPythonApiResponse(JSONObject pythonBuildModel, String pythonBaseUrl, PythonRequest pyRequest, String status, String status1, String project);

}

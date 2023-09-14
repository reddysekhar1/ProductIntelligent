package com.intelliservice.agentanalytics.controllers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intelliservice.agentanalytics.model.BasicResponseData;
import com.intelliservice.agentanalytics.model.PythonRequest;
import com.intelliservice.agentanalytics.model.PythonRequestModel;
import com.intelliservice.agentanalytics.pythonapi.services.PythonApiService;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;



@RestController
@RequestMapping(value ="/product-intelligent-triage/")
public class PythonApiControllers {
	
	private static final Logger log = LoggerFactory.getLogger(PythonApiControllers.class);
	@Autowired
	PythonApiService pythonApiService;
	
	@PostMapping("/api/symptoms/model_build")
	public ResponseEntity symptomsModelBuild(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "{}") @RequestBody String inputString)
	{
		log.debug("Enter in to symptomsModelBuild");
		String returnmessage;
		String pythonBaseUrl = "/api/symptoms/model_build/";
		String status="Symptom model is in progress";
		String status1="Symptom model  is completed";
		String project="testprj";
		JSONObject inputJson = new JSONObject(inputString);
		try {
			returnmessage = pythonApiService.getPythonApiResponse(inputJson,pythonBaseUrl, new PythonRequestModel(),status,status1,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, returnmessage, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/api/root_cause/model_build")
	public ResponseEntity rootCausesModelBuild(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "{}") @RequestBody String inputString)
	{
		log.debug("Enter in to rootCausesModelBuild");
		String returnmessage;
		String pythonBaseUrl = "/api/root_cause/model_build/";
		String status="Resolution model is in progress";
		String status1="Resolution model is completed";
		String project="testprj";
		JSONObject inputJson = new JSONObject(inputString);
		try {
			returnmessage = pythonApiService.getPythonApiResponse(inputJson,pythonBaseUrl, new PythonRequestModel(),status,status1,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, returnmessage, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/api/symptoms/prediction")
	public ResponseEntity symptomsPrediction(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "{}") @RequestBody String inputString)
	{
		log.debug("Enter in to symptomsPrediction");
		String returnmessage;
		String pythonBaseUrl = "/api/symptoms/prediction/";
		String status="Symptom predictions in progress";
		String status1="Symptom predictions is completed";
		String project="testprj";
		try {
			JSONObject pythonBuildModel = new JSONObject(inputString);
			returnmessage = pythonApiService.getPythonApiResponse(pythonBuildModel,pythonBaseUrl, new PythonRequest(),status,status1,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, returnmessage, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/api/root_cause/prediction")
	public ResponseEntity rootCausesPrediction(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "{}") @RequestBody String inputString)
	{
		log.debug("Enter in to rootCausesPrediction");
		String returnmessage;
		String pythonBaseUrl = "/api/root_cause/prediction/";
		String status="Resolution predictions in progress";
		String status1="Resolution predictions is completed";
		String project="testprj";
		try {
			JSONObject pythonBuildModel = new JSONObject(inputString);
			returnmessage = pythonApiService.getPythonApiResponse(pythonBuildModel,pythonBaseUrl,new PythonRequest(),status,status1,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, returnmessage, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/api/question_gen")
	public ResponseEntity questionGen(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "{}") @RequestBody String inputString)
	{
		log.debug("Enter in to question_gen");
		String returnmessage;
		String pythonBaseUrl = "/api/question_gen";
		String status="question_gen in progress";
		String status1="question_gen is completed";
		String project="testprj";
		try {
			JSONObject pythonBuildModel = new JSONObject(inputString);
			returnmessage = pythonApiService.getPythonApiResponse(pythonBuildModel,pythonBaseUrl,new PythonRequest(),status,status1,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, returnmessage, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

}

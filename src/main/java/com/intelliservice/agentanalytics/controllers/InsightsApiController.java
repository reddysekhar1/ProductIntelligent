package com.intelliservice.agentanalytics.controllers;

import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intelliservice.agentanalytics.model.BasicResponseData;
import com.intelliservice.agentanalytics.model.BasicResponseStringJson;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.services.InsightsService;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@RestController
@RequestMapping(value="/product-intelligent-triage/insights")
public class InsightsApiController {
	private static final Logger log = LoggerFactory.getLogger(InsightsApiController.class);

	@Autowired
	InsightsService insightsService;
	@PostMapping("similarissues/{issuedescription}/{project}")
	public ResponseEntity<BasicResponseStringJson<String>> getSimilarIssues(@RequestBody  String json,@PathVariable("issuedescription") String issueDescription,@PathVariable("project") String project){
		try {
			String response = insightsService.getSimilarIssues(issueDescription,json,project);	
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	@GetMapping("/productdetails/{project}")
	public ResponseEntity<BasicResponseData<List<ProductAttributes>>> getProductAttribute(@PathVariable("project") String project) {
		try {
			List<ProductAttributes> productdetailsList = insightsService.getProductAttribute(project);
			BasicResponseData basicResponse = new BasicResponseData(
					ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, productdetailsList, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(
					ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseData<List<ProductAttributes>>>(basicResponse,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//TODO project For both Yes and No and Normal query format is different. So not sure which one is correct
	@PostMapping("/issueclusters/{issuedescription}/{project}")
	public ResponseEntity<BasicResponseStringJson<String>> getIssueClusters(@RequestBody  String json,@PathVariable("issuedescription") String issueDescription,@PathVariable("project") String project){
		try {
			JSONArray reponse = insightsService.getIssueClusters(issueDescription,json,project);	
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, reponse.toString(), "");
			
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	
	@PostMapping("/recommendedsolutions/{issuedescription}")
	public ResponseEntity getRecommendedsolutions(@RequestBody  String json,@PathVariable("issuedescription") String issueDescription){
		try {
			String reponse = insightsService.getRecommendedsolutions(issueDescription,json);	
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, reponse, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 

}

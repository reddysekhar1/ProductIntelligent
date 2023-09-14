package com.intelliservice.agentanalytics.controllers;

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
import com.intelliservice.agentanalytics.services.BulkApiBuilderService;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;


@RestController
@RequestMapping(value ="/product-intelligent-triage/")
public class BulkApiController {
	
	private static final Logger log = LoggerFactory.getLogger(BulkApiController.class);
	
	@Autowired
	BulkApiBuilderService bulkApiBuilderService;
	
	@PostMapping("/bulkapi")
	public ResponseEntity callBulkApi(@RequestBody String string){
		log.debug("Enter in to callBulkApi");
		String returnmessage = "" ;
		try {
			bulkApiBuilderService.runBulkApi(string);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, returnmessage, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error inside callBulkApi: ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
	}

}


package com.intelliservice.agentanalytics.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intelliservice.agentanalytics.model.BasicResponseData;
import com.intelliservice.agentanalytics.model.BasicResponseStringJson;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.services.GenericCollectionService;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@RestController
@RequestMapping(value ="/product-intelligent-triage/settings")
public class GenericCollectionAPIController {
	private static final Logger log = LoggerFactory.getLogger(GenericCollectionAPIController.class);

	@Autowired
	GenericCollectionService genericCollectionService;

	
	@GetMapping(value = "/collections")
	public ResponseEntity getCollections()	{
		log.debug("Enter in to getCollections");
		List<String> collectionsList = null;
		try {
			collectionsList = genericCollectionService.getCollections();
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, collectionsList, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/fields/{indexName}")
	public ResponseEntity getFields(@PathVariable("indexName") String indexName)	{
		log.debug("Enter in to getFields");
		List<String> collectionsList = null;
		try {
			collectionsList = genericCollectionService.getFields(indexName);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, collectionsList, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/collections/{collectionName}/field/{fieldname}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity getFieldValues(@PathVariable("collectionName") String collectionName,@PathVariable("fieldname") String fieldname,String project)
	{
		
		List<String> values = new ArrayList<>();
		try {
			values = genericCollectionService.getFieldValues(collectionName, fieldname,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, values, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping(value = "/configurationCollection",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity createUpdtaeConfigurationIndex(@RequestBody TriageConfiguration configuration){
		
		log.info("Entry createUpdtaeConfigurationIndex");
		String response = "";
		try {
			response = genericCollectionService.createUpdtaeConfigurationIndex(configuration);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in createUpdtaeConfigurationIndex", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
    @GetMapping(value = "/insightsconfiguration/project/{project}")
    public ResponseEntity getInsightsConfigData(@PathVariable("project") String project)     {
     log.debug("Enter in to getInsightsConfigData");
     String configData = "";
     try {
         configData = genericCollectionService.getConfigData("insightsconfiguration","",project);
         BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, configData, "");
         return new ResponseEntity(basicResponse, HttpStatus.OK);
		 
        } catch (Exception e) {
        log.error(e.getMessage());
       BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
       return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }                        
    }
    
    @PostMapping("/insightsconfiguration/project/{project}")
    public ResponseEntity saveInsightsConfigData(@PathVariable("project") String project,@RequestBody String json) {
    	log.debug("Enter in to saveInsightsConfigData");
        String response = "";
        try {
         response = genericCollectionService.saveConfigData("insightsconfiguration","",project,json);
         BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response,"");
        return new ResponseEntity(basicResponse, HttpStatus.OK);
                                
        } catch (Exception e) {
        log.error("Error inside saveInsightsConfigData : ",e);
        BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
        return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }                        
                            
    }


}

package com.intelliservice.agentanalytics.controllers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.intelliservice.agentanalytics.model.BasicResponseData;
import com.intelliservice.agentanalytics.model.BasicResponseStringJson;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.RootcauseDetails;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
import com.intelliservice.agentanalytics.model.Symptoms;
import com.intelliservice.agentanalytics.model.SymptomsDetails;
import com.intelliservice.agentanalytics.services.ProductDetailsService;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@RestController
@RequestMapping(value ="/product-intelligent-triage/settings")
public class ProductDetailsApiController {
	private static final Logger log = LoggerFactory.getLogger(ProductDetailsApiController.class);

	@Autowired
	ProductDetailsService productDetailsService;

	//@PostMapping(value="/productdetails")
	public ResponseEntity saveProductDetails( @RequestBody ProductAttributes productAttributes)
	{
		log.debug("Enter in to saveProductDetails");
		try {
			productAttributes = productDetailsService.saveProductDetails(productAttributes);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, productAttributes, "");
			return new ResponseEntity<>(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error inside saveProductDetails : ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//@GetMapping(value = "/productdetails")
	public ResponseEntity getProductDetails()
	{
		log.debug("Enter in to getProductDetails");
		List<ProductAttributes> productDetailList = null;
		try {
			productDetailList = productDetailsService.getProductDetails();
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, productDetailList, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error inside getProductDetails : ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//@PostMapping(value = "/producttriage/{indexName}/rootaggs/{rootaggs}/project/{project}")
	public ResponseEntity getProductTriage(@PathVariable("indexName") String collectionName, @PathVariable("rootaggs") String rootaggs,@PathVariable("project") String project,@RequestBody String json) {
		log.debug("Enter in to getProductTriage");
		List<Map<String, Set<String>>> productDetail = null;
		try {
			productDetail = productDetailsService.getProductTriage(collectionName,json,rootaggs,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, productDetail, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error inside ProductTriageService: ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/sourcemap")
	public ResponseEntity saveSourcesMap(@RequestBody List<SourceMap> sourceMap) {
		
		log.debug("Enter in to getSourcesMap");
		List<SourceMap> models  = null;
		try {
			models = productDetailsService.saveSourcesMap(sourceMap);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside saveSourcesMap : ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/sourcemap/{project}")
	public ResponseEntity  getSourcesMap(@PathVariable("project") String project) {
		log.debug("Enter in to getSourcesMap");
		List<SourceMap> models  = null;
		try {
			models = productDetailsService.getSourcesMap(project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getSourcesMap : ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/reindex")
	public ResponseEntity collectionReindex(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "{Provide json as input \"sourceIndex\":\"\",\"desitnationIndex\":\"\"}") @RequestBody String inputString){
		log.debug("Enter in to collectionReindex");
		String message = "";
		JSONObject inputJson = new JSONObject(inputString); 
		try {
			String sourceIndex = inputJson.getString("sourceIndex");
			String desitnationIndex = inputJson.getString("desitnationIndex");
			String project = inputJson.getString("project");
			message = productDetailsService.collectionReindex(sourceIndex, desitnationIndex,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_CREATED, message, "");
			return new ResponseEntity(basicResponse, HttpStatus.CREATED);
			
		} catch (Exception e) {
			log.error("Error inside collectionReindex : ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/symptomsrootcausemap/{collection}/project/{project}")
	public ResponseEntity symptomRootcauseMapping(@PathVariable("collection") String collectionId, @PathVariable("project") String project) {
		log.debug("Enter in to symptomRootcauseMapping");
		List<SymptomRootcauseMapping> symptomRootcauseList;
		try {
			symptomRootcauseList = productDetailsService.symptomRootcauseMapping(collectionId,project);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, symptomRootcauseList, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside symptomRootcauseMapping : ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * @author anilakumar.biradar
	 * @param json
	 * @return
	 */
	
	public ResponseEntity updateSymptoms(@RequestBody Symptoms symptomsRequest){
		try {
			String response = productDetailsService.updateSymptoms(symptomsRequest);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage());
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	
	
	@DeleteMapping("/rootaggs")
	public ResponseEntity deleteSymptoms(@RequestBody  String json){ 
		try {
			String response = productDetailsService.deleteSymptoms(json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in deleteSymptoms", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	
	
	//@PostMapping("/Symptoms")
	public ResponseEntity createSymptoms(@RequestBody Insights insightsModal ){   
		try {
			String response = productDetailsService.createSymptoms(insightsModal);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in createSymptoms", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	
	//@PostMapping("/symptomsrootcause")
	public ResponseEntity createSymptomsrootcause(@RequestBody SymptomRootcauseMapping symptomRootcauseMapping ){   
		try {
			String response = productDetailsService.createSymptomsrootcause(symptomRootcauseMapping);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in create createSymptomsrootcause", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//@PutMapping("/symptomsrootcause")
	public ResponseEntity updateSymptomsrootcause(@RequestBody  SymptomRootcauseMapping symptomRootcauseMapping ){   
		try {
			String response = productDetailsService.updateSymptomsrootcause(symptomRootcauseMapping);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in update updateSymptomsrootcause", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	

	//@DeleteMapping("/symptomsrootcause")
	public ResponseEntity deleteSymptomsrootcause(@RequestBody  String json){ 
		try {
			String response = productDetailsService.deleteSymptomsrootcause(json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in delete deleteSymptomsrootcause", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	
	@PutMapping("/statusAndScore")
	public ResponseEntity updateStatusAndScore(@RequestBody  SymptomRootcauseMapping symptomRootcauseMapping ){
		try {
			String response = productDetailsService.updateStatusAndScore(symptomRootcauseMapping);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in update Status And Score", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/ProductdetailsData/{project}")
	public ResponseEntity createAttributes(@PathVariable("project") String project ){
		try {
			String response = productDetailsService.createAttributes(project);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in update Symptoms", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/symptomsrootcausemappinglist/project/{project}")
	public ResponseEntity getSymptomsList(@PathVariable("project") String project,@RequestBody String json ) {
		log.debug("Enter in to getSymptomsList");
		String models  = null;
		try {
			models = productDetailsService.getSymptomsList(project,json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getSymptomsList : ",e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/rootcausesymptomsmappinglist/project/{project}")
	public ResponseEntity getRootCauseList(@PathVariable("project") String project,@RequestBody String json) {
		log.debug("Enter in to getRootCauseList");
		String models  = null;
		try {
			models = productDetailsService.getRootcauseList(project,json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getRootCauseList : ",e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	//@PutMapping("/rootcause")
	public ResponseEntity updateRootcause(@RequestBody  SymptomRootcauseMapping symptomRootcauseMapping ){   
		try {
			String response = productDetailsService.updateRootcause(symptomRootcauseMapping);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in update updateRootcause", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 	
	
    //@DeleteMapping("/rootcause")
	public ResponseEntity deleteRootcause(@RequestBody  String json){ 
		try {
			String response = productDetailsService.deleteRootcause(json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in delete deleteRootcause", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/RecommendedSolutions/{project}")
	public ResponseEntity populateRecommendedSolutions(@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Give all rootcause in (,) separated") @RequestBody String input,@PathVariable("project") String project){
		log.debug("Enter to populateRecommendedSolutions");
		try {
			JSONArray inputArray = new JSONArray(input);
			String response = productDetailsService.populateRecommendedSolutions(inputArray.toString(),project);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in delete populateRecommendedSolutions", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping("/addSymptoms")
	public ResponseEntity<BasicResponseStringJson<String>> addSymptoms(@RequestBody SymptomsDetails symptomsDetailsModal ){   
		try {
			String response = productDetailsService.addSymptoms(symptomsDetailsModal);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in createSymptoms", e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	
	@GetMapping(value = "/SimilarSymptomsSearch/{symptoms}/project/{project}")
	public  ResponseEntity<BasicResponseStringJson<String>>  getSimilarSymptomsSearch(@PathVariable("symptoms") String symptoms,@PathVariable("project") String project)	{
		log.debug("Enter in to getSimilarSymptomsSearch");
		String models  = null;
		try {
			models = productDetailsService.getSimilarSymptomsSearch(symptoms,project);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getSymptoms : ",e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	
	
	@GetMapping(value = "/SimilarRootCauseSearch/{rootcause}/project/{project}")
	public  ResponseEntity<BasicResponseStringJson<String>>  getSimilarRootCauseSearch(@PathVariable("rootcause") String rootcause,@PathVariable("project") String project)	{
		log.debug("Enter in to getSimilarSymptomsSearch");
		String models  = null;
		try {
			models = productDetailsService.getSimilarRootCauseSearch(rootcause,project);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getRootCause: ",e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}	
	@PostMapping("/addRootcause")
	public ResponseEntity<BasicResponseStringJson<String>> addRootcause(@RequestBody RootcauseDetails RootcauseDetailsModal ){   
		try {
			String response = productDetailsService.addRootcause(RootcauseDetailsModal);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in addRootcause", e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/producttriage/triagesummary")
	public ResponseEntity saveTriageSummary(@RequestBody String json) {
		log.debug("Enter in to getProductTriage");
		String triagesummary = null;
		try {
			triagesummary = productDetailsService.saveTriageSummary(json);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, triagesummary, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error inside ProductTriageService: ",e);
			BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/symptomsUnmap")
	public ResponseEntity symptomsUnmap(@RequestBody  String json){ 
		try {
			String response = productDetailsService.symptomsUnmap(json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in symptomsUnmap", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} 
	
	@DeleteMapping("/rootcauseUnmap")
	public ResponseEntity rootcauseUnmap(@RequestBody  String json){ 
		try {
			String response = productDetailsService.rootcauseUnmap(json);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in rootcauseUnmap", e);
			BasicResponseStringJson basicResponse = new BasicResponseStringJson(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	
	@GetMapping(value = "/getSymptomsdetails/{symptoms}/project/{project}")
	public  ResponseEntity<BasicResponseStringJson<String>>  getSymptomsdetails(@PathVariable("symptoms") String symptoms,@PathVariable("project") String project)	{
		log.debug("Enter in to getSymptomsdetails");
		String models  = null;
		try {
			models = productDetailsService.getSymptomsdetails(symptoms,project);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getSymptomsdetails : ",e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping(value = "/getSymptomsdetailsSet/project/{project}")
	public  ResponseEntity<BasicResponseStringJson<String>>  getSymptomsdetailsSet(@PathVariable("project") String project,@RequestBody String inpJson)	{
		log.debug("Enter in to getSymptomsdetails");
		String models  = null;
		try {
			models = productDetailsService.getSymptomsdetailsSet(project,inpJson);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error inside getSymptomsdetails : ",e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
@GetMapping("/monitoringlogs/project/{project}")
	public ResponseEntity<BasicResponseStringJson<String>> getWizardFlowStatus(@PathVariable("project") String project){   
		try {
			String response = productDetailsService.getWizardFlowStatus(project);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, response, "");
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred in addRootcause", e);
			BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
			return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
@GetMapping(value = "/getRootCausedetails/{rootcause}/project/{project}")
public  ResponseEntity<BasicResponseStringJson<String>>  getRootcausedetails(@PathVariable("rootcause") String rootcause,@PathVariable("project") String project)	{
	log.debug("Enter in to getSymptomsdetails");
	String models  = null;
	try {
		models = productDetailsService.getRootcausedetails(rootcause,project);
		BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
		return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
		
	} catch (Exception e) {
		log.error("Error inside getSymptomsdetails : ",e);
		BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
		return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}

@PostMapping(value = "/getRootCausedetailsSet/project/{project}")
public  ResponseEntity<BasicResponseStringJson<String>>  getRootcausedetailsSet(@PathVariable("project") String project,@RequestBody String inpJson)	{
	log.debug("Enter in to getSymptomsdetails");
	String models  = null;
	try {
		models = productDetailsService.getRootcausedetailsSet(project,inpJson);
		BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
		return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
		
	} catch (Exception e) {
		log.error("Error inside getSymptomsdetails : ",e);
		BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
		return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
@PostMapping(value = "/getSymptom/project/{project}/issue/{issue}")
public  ResponseEntity<BasicResponseStringJson<String>>  getSymptom(@PathVariable("project") String project,@PathVariable("issue") String issue,@RequestBody String inpJson)	{
	log.debug("Enter in to getSymptomsdetails");
	String models  = null;
	try {
		models = productDetailsService.getSymptom(project,issue,inpJson);
		BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, models,"");
		return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.OK);
		
	} catch (Exception e) {
		log.error("Error inside getSymptom: ",e);
		BasicResponseStringJson<String> basicResponse = new BasicResponseStringJson<String>(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
		return new ResponseEntity<BasicResponseStringJson<String>>(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}

@PostMapping(value = "/questions/project/{project}")
public ResponseEntity updateQuestions(@PathVariable("project") String project) {
	log.debug("Enter in to updateQuestions");
	String updatequestions = null;
	try {
		updatequestions = productDetailsService.updatequestions(project);
		BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.SUCCESS, ProductTriageConstants.STATUS_CODE_OK, updatequestions, "");
		return new ResponseEntity(basicResponse, HttpStatus.OK);

	} catch (Exception e) {
		log.error("Error inside updateQuestions: ",e);
		BasicResponseData basicResponse = new BasicResponseData(ProductTriageConstants.FAILURE, ProductTriageConstants.STATUS_CODE_SERVER_ERROR, null, e.getMessage());
		return new ResponseEntity(basicResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

	

}

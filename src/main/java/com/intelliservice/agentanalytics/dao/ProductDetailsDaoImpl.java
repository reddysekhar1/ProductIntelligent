package com.intelliservice.agentanalytics.dao;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.entity.ProductAttributesEntity;
import com.intelliservice.agentanalytics.entity.SymptomRootcauseMapEntity;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.ProductAttributesSet;
import com.intelliservice.agentanalytics.model.RootCause;
import com.intelliservice.agentanalytics.model.RootcauseDetails;
import com.intelliservice.agentanalytics.model.RootcauseSymptomMappingList;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMappingList;
import com.intelliservice.agentanalytics.model.Symptoms;
import com.intelliservice.agentanalytics.model.SymptomsDetails;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.repository.ProductDetailsRepository;
import com.intelliservice.agentanalytics.repository.SymptomRootcauseMappingRepository;
import com.intelliservice.agentanalytics.utils.ESQueries;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;
import java.sql.Timestamp;

@Service
public class ProductDetailsDaoImpl implements ProductDetailsDao {
	private static final Logger log = LoggerFactory.getLogger(ProductDetailsDaoImpl.class);

	@Autowired
	ProductDetailsRepository productDetailsRepository;

	@Autowired
	ESClient esClient;

	@Autowired
	SymptomRootcauseMappingRepository symptomRootcauseMappingRepository;

	@Autowired
	SourceMapDao sourceMapDao;

	@Autowired
	FieldValuesDao fieldValuesDao;

	@Autowired
	GenericCollectionDao genericCollectionDao;

	@Override
	public ProductAttributes saveProductDetails(ProductAttributes productAttributes) {
		ProductAttributesEntity productAttributesEntity = new ProductAttributesEntity();
		try {
			ProductAttributesEntity entity = mapToEntity(productAttributes);			
			List<ProductAttributesEntity> entityList = productDetailsRepository.findByAttributeName(productAttributes.getAttributeName().toLowerCase());
			entityList.stream().forEach(u -> entity.setId(u.getId()));
			if (entity.getId() != null)
				productDetailsRepository.deleteById(entity.getId());		
			productAttributesEntity = productDetailsRepository.save(entity);

		} catch (NoSuchIndexException e) {
			log.debug("Message from saveProductDetails ProductDetailsDaoImpl {} ", e.getMessage());
		}		

		return mapToModel(productAttributesEntity);
	}

	private ProductAttributesEntity mapToEntity(ProductAttributes productAttributes) {
		ProductAttributesEntity productAttributesEntity = new ProductAttributesEntity();
		productAttributesEntity.setAttributeData(productAttributes.getAttributeData().stream().collect(Collectors.toList()));
		productAttributesEntity.setAttributeName(productAttributes.getAttributeName());
		productAttributesEntity.setProject(productAttributes.getProject());
		return productAttributesEntity;		
	}

	private ProductAttributes mapToModel(ProductAttributesEntity productAttributesEntity) {	
		ProductAttributes productAttributes = new ProductAttributes();
		productAttributes.setAttributeData(productAttributesEntity.getAttributeData().stream().collect(Collectors.toList()));
		productAttributes.setAttributeName(productAttributesEntity.getAttributeName());
		productAttributes.setProject(productAttributesEntity.getProject());
		return productAttributes;
	}

	@Override
	public List<ProductAttributes> getProductDetails() {
		List<ProductAttributes> productAttributesList = new ArrayList<>();
		try {
			Iterable<ProductAttributesEntity> iterEntity = productDetailsRepository.findAll();
			iterEntity.forEach(p -> productAttributesList.add(mapToModel(p)));			
		} catch (Exception e) {
			log.error(e.getMessage());
		}		
		return productAttributesList;
	}

	@Override
	public String symptomRootcauseMapping(String collectionId,String project) throws AgentAnalyticsException, IOException {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.SYMPTOMROOTCAUSEMAPPING);
		TriageConfiguration maxsizeConfig = genericCollectionDao.getConfigurationData(ProductTriageConstants.RESULT_SIZE);
		String inputQuery = String.format(triageConfiguration.getValue(),maxsizeConfig.getValue(),project);


		return esClient.performRequest(ProductTriageConstants.POST, "/" + collectionId + ProductTriageConstants.SEARCH, inputQuery);


	}

	@Override
	public List<SymptomRootcauseMapping> saveSymptomRootcauseMapping(List<SymptomRootcauseMapping> symptomRootcauseList) {
		log.debug("Enter to saveSymptomRootcauseMapping");
		List<SymptomRootcauseMapping> modelList = new ArrayList<>();
		try {
			List<SymptomRootcauseMapEntity> entityList = symptomRootcauseList.stream().collect(Collectors.mapping(entity -> mapToEntity(entity), Collectors.toList()));
			List<SymptomRootcauseMapEntity> returnEntityList = (List<SymptomRootcauseMapEntity>) symptomRootcauseMappingRepository.saveAll(entityList);
			modelList = returnEntityList.stream().collect(Collectors.mapping(entity -> mapToModel(entity), Collectors.toList()));		
		} catch (Exception e) {
			log.error(e.getMessage());
		}		

		return modelList;
	}

	private SymptomRootcauseMapping mapToModel(SymptomRootcauseMapEntity symptomRootcauseEntity) {
		SymptomRootcauseMapping symptomRootcauseMapping = new SymptomRootcauseMapping();
		symptomRootcauseMapping.setLeadingquestion(symptomRootcauseEntity.getLeadingQuestion());
		symptomRootcauseMapping.setRootcause(symptomRootcauseEntity.getRootcause());
		symptomRootcauseMapping.setSymptoms(symptomRootcauseEntity.getSymptoms());
		symptomRootcauseMapping.setLeadingquestion(symptomRootcauseEntity.getLeadingQuestion() != null ? symptomRootcauseEntity.getLeadingQuestion() : symptomRootcauseEntity.getSymptoms().concat("_question"));
		symptomRootcauseMapping.setRelevancy(symptomRootcauseEntity.getRelevency());
		symptomRootcauseMapping.setRcTitle(symptomRootcauseEntity.getRcTitle());
		symptomRootcauseMapping.setStatus(symptomRootcauseEntity.getStatus());
		symptomRootcauseMapping.setSymTitle(symptomRootcauseEntity.getSymTitle());
		symptomRootcauseMapping.setProject(symptomRootcauseEntity.getProject());
		symptomRootcauseMapping.setProductAttributes(symptomRootcauseEntity.getProductAttributes());
		symptomRootcauseMapping.setResolution(symptomRootcauseEntity.getResolution());
		symptomRootcauseMapping.setDescription(symptomRootcauseEntity.getDescription());

		return symptomRootcauseMapping;
	}

	private SymptomRootcauseMapEntity mapToEntity(SymptomRootcauseMapping symptomRootcause) {
		SymptomRootcauseMapEntity symptomRootcauseMapEntity = new SymptomRootcauseMapEntity();
		symptomRootcauseMapEntity.setLeadingQuestion(symptomRootcause.getLeadingquestion());
		symptomRootcauseMapEntity.setRootcause(symptomRootcause.getRootcause());
		symptomRootcauseMapEntity.setSymptoms(symptomRootcause.getSymptoms());
		symptomRootcauseMapEntity.setRelevency(symptomRootcause.getRelevancy());
		symptomRootcauseMapEntity.setRcTitle(symptomRootcause.getRcTitle());
		symptomRootcauseMapEntity.setStatus(symptomRootcause.getStatus());
		symptomRootcauseMapEntity.setSymTitle(symptomRootcause.getSymTitle());
		symptomRootcauseMapEntity.setProject(symptomRootcause.getProject());
		symptomRootcauseMapEntity.setProductAttributes(symptomRootcause.getProductAttributes());
		symptomRootcauseMapEntity.setResolution(symptomRootcause.getResolution());
		symptomRootcauseMapEntity.setDescription(symptomRootcause.getDescription());

		return symptomRootcauseMapEntity;		
	}

	@Override
	public void deleteAllSymptomRootcauseMapping(String project) {
		try {
		//	List<SymptomRootcauseMapEntity> entirys =  symptomRootcauseMappingRepository.findByProject(project);
			//List<SymptomRootcauseMapEntity> entirys =  symptomRootcauseMappingRepository.findAll();
			symptomRootcauseMappingRepository.deleteByProject(project);
		} catch (NoSuchIndexException e) {
			log.error(e.getMessage());
		}

	}
	@Override
	public void monitoringLog(String apiurl,String status,String project,Timestamp timestamp)
	{
		String requestUrl = "/" + ProductTriageConstants.MONITORINGLOG_COLL + ProductTriageConstants.DOC;
		
		JSONObject requestJson = new JSONObject();
		
			requestJson.put("apiurl",apiurl);
			requestJson.put("status", status);
			requestJson.put("project", project);
			requestJson.put("timestamp", timestamp);
		try {
			esClient.performRequest("POST", requestUrl, requestJson.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public List<SymptomRootcauseMapping> getAllSymptomRootcauseMapping(String project) {
		log.debug("Enter to getAllSymptomRootcauseMapping");
		List<SymptomRootcauseMapping> modelList = new ArrayList<>();
		try {
			PageRequest pageable = PageRequest.of(0, 10000);
			List<SymptomRootcauseMapEntity> entityList = symptomRootcauseMappingRepository.findByProject(project,pageable);
			//List<SymptomRootcauseMapEntity> entityList = symptomRootcauseMappingRepository.findAll();

			modelList = entityList.stream().collect(Collectors.mapping(entity -> mapToModel(entity), Collectors.toList()));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return modelList;
	}

	@Override
	public String updateSymptoms(Symptoms symptomsModal) {

		log.debug("Enter to updateSymptoms");
		String response = null;
		try {
			String requestUrl = "/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.UPDATEBYQUERY;
			TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.UPDATESYMPTOMSQUERY);
			String requestBody = String.format(triageConfiguration.getValue(), symptomsModal.getSystemGeneretdSymptoms(),symptomsModal.getSymptomsTitle());
			JSONObject requestJson = new JSONObject(requestBody);		
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in updateSymptoms {}", e);
		}
		return response;
	}

	@Override
	public String deleteSymptoms(String inputJson) throws AgentAnalyticsException {
		log.debug("Enter to deleteSymptoms");
		String response = null;
		try {
			JSONObject inputObj = new JSONObject(inputJson);
			String requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DELETEBYQUERY;
			String requestBody =null;
			
			if(inputJson.contains("symptoms")) {
				 requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+inputObj.getString("project")+"\"}},{\"term\":{\"symptoms.keyword\": \""+inputObj.getString("symptoms")+"\"}}]}}}";
				
			}else{
				 requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+inputObj.getString("project")+"\"}},{\"term\":{\"rootcause.keyword\": \""+inputObj.getString("rootcause")+"\"}}]}}}";
					
			}
			JSONObject requestJson = new JSONObject(requestBody);			
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
			log.debug("response in deleteSymptoms {}", response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in deleteSymptoms {}", e);
		}
		return response;
	}

	@Override
	public String createSymptoms(Insights insightsModal) {
		log.debug("Enter to createSymptoms");
		String response = null;
		try {
			JSONObject requestJson = new JSONObject(insightsModal);
			String requestUrl ="/"+ ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.DOC;		
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
			log.debug("response in createSymptoms {}",response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in createSymptoms {}", e);
		}
		return ProductTriageConstants.SUCCESS;
	}

	@Override
	public String createSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping) {
		log.debug("Enter to createAttributes");
		JSONObject requestJson = new JSONObject(symptomRootcauseMapping);
		String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DOC;
		String response = null;
		try {
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
			log.debug("response in createSymptomsrootcause {}",response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in createSymptomsrootcause {}", e);
		}
		return ProductTriageConstants.SUCCESS;
	}

	
	public String updateSymptomsrootcausepOld(SymptomRootcauseMapping symptomRootcauseMapping) {
		log.debug("Enter to updateSymptomsrootcause");
		String requestUrl ="/"+ ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.UPDATESYMPTOMSROOTCAUSEQUERY); 
		String requestBody = String.format(triageConfiguration.getValue(), symptomRootcauseMapping.getSymptoms(),symptomRootcauseMapping.getSymTitle(),symptomRootcauseMapping.getLeadingquestion(),symptomRootcauseMapping.getProject());
		JSONObject requestJson = new JSONObject(requestBody);
		String response = null;
		try {
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in updateSymptomsrootcause {}", e);
		}
		return response;
	}

	@Override
	public String deleteSymptomsrootcause(String inputJson) {
		log.debug("Enter to deleteSymptomsrootcause");
		JSONObject inputObj = new JSONObject(inputJson);
		String requestUrl = null;
		String requestUrl1 = null;
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.DELETESYMPTOMSROOTCAUSE_MAPPING_COLL); 
		TriageConfiguration triageConfiguration1 = genericCollectionDao.getConfigurationData(ProductTriageConstants.DELETESYMPTOMSROOTCAUSE_PRODUCT_TRIAGE_COLL);
		String requestBody=null;
		String requestBody1=null;
		if(inputObj.has("newSymptoms")) {
			requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
			requestUrl1 = "/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.UPDATEBYQUERY;
			requestBody ="{\"script\": {\"source\": \"if (ctx._source.symptoms == params.symptoms) { ctx._source.symptoms=params.newSymptoms ;ctx._source.leadingquestion=params.leadingquestion }\", \"lang\": \"painless\", \"params\": {\"symptoms\": \""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\",\"newSymptoms\": \""+inputObj.getString("newSymptoms")+"\",\"leadingquestion\": \""+inputObj.getString(ProductTriageConstants.LEADINGQUESTION)+"\"}},\"query\": {\"bool\": {\"filter\": [ {\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"symptoms.keyword\": \""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}}]}}}";
			requestBody1 = "{\"script\": {\"source\": \"if (ctx._source.symptoms.contains(params.symptoms)) { ctx._source.symptoms.add(params.newSymptoms);ctx._source.symptoms.remove(ctx._source.symptoms.indexOf(params.symptoms))}\", \"lang\": \"painless\", \"params\": {\"symptoms\": \""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\",\"newSymptoms\":\""+inputObj.getString("newSymptoms")+"\"}},\"query\": {\"bool\": {\"filter\": [ {\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"symptoms.keyword\": \""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}}]}}}";
		}else {
			requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DELETEBYQUERY;
			requestUrl1 = "/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.UPDATEBYQUERY;
			requestBody ="{\"query\": {\"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"symptoms.keyword\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}}]}}}";
			requestBody1="{\"script\": {\"source\": \"if (ctx._source.symptoms.contains(params.tag)) { ctx._source.symptoms.remove(ctx._source.symptoms.indexOf(params.tag)) }\",\"lang\": \"painless\",\"params\": {\"tag\": \""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}},\"query\": { \"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\" }}]}}}";
			// requestBody = String.format(triageConfiguration.getValue(),inputObj.getString(ProductTriageConstants.PROJECT), inputObj.getString(ProductTriageConstants.SYMPTOMS));
			// requestBody1 = String.format(triageConfiguration1.getValue(), inputObj.getString(ProductTriageConstants.SYMPTOMS), inputObj.getString(ProductTriageConstants.PROJECT));
		}
		JSONObject requestJson = new JSONObject(requestBody);
		JSONObject requestJson1 = new JSONObject(requestBody1);
		String response = null;
		try {
			esClient.performRequest(ProductTriageConstants.POST, requestUrl1, requestJson1.toString());
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in deleteSymptomsrootcause {}", e);
		}
		return response;
	}

	@Override
	public String updateStatusAndScore(SymptomRootcauseMapping symptomRootcauseMapping) {
		log.debug("Enter to updateStatusAndScore");
		String response = null;
		try {
			String requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
			TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.UPDATESTATUSANDSCOREQUERY); 
			String requestBody =String.format(triageConfiguration.getValue(), symptomRootcauseMapping.getRelevancy(),symptomRootcauseMapping.getStatus(),symptomRootcauseMapping.getRootcause(),symptomRootcauseMapping.getSymptoms(),symptomRootcauseMapping.getProject());
			JSONObject requestJson = new JSONObject(requestBody);		
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in updateStatusAndScore {}", e);
		}
		return response;
	}

	@Override
	public String createAttributes(String project) {
		log.debug("Enter to createAttributes");
		SourceMap sourceMap;
		String response = null;
		List<SourceMap> sourcemapvalues;
		JSONObject requestJson = new JSONObject();
		try {
		sourcemapvalues = sourceMapDao.getSourcesMap(project);
		sourceMap = sourcemapvalues.stream().filter(p-> p.getSourceName().contains(ProductTriageConstants.PRODUCT)).findAny().orElseThrow(() -> new IllegalArgumentException( "values are not available"));
		JSONArray resrsArray = new JSONArray(sourceMap.getSourceFields());
		String requestUrl = "/"+ProductTriageConstants.PRODUCT_ATTRIBUTES_COLL;
		String requestBody="{ \"query\": { \"match\": {\"project.keyword\": \""+project+"\"} } }";
		esClient.performRequest(ProductTriageConstants.POST, requestUrl+ProductTriageConstants.DELETEBYQUERY,requestBody);
		for (Object object : resrsArray) {
		List<String> values= fieldValuesDao.getFieldValues( ProductTriageConstants.PRODUCT_TRIAGE_COLL , object.toString(),project);
		requestJson.put(ProductTriageConstants.ATTRIBUTENAME, object.toString());
		requestJson.put(ProductTriageConstants.ATTRIBUTEDATA, values);
		requestJson.put(ProductTriageConstants.PROJECT, project);
		response = esClient.performRequest(ProductTriageConstants.POST, requestUrl+ProductTriageConstants.DOC, requestJson.toString());
		log.info("createAttributes Object## {}", response);
		}
		} catch (Exception e ) {
		log.debug("createAttributes ProductDetailsDao##{}", e.getMessage());
		}
		return response;
		}
	

	@Override
	public String getSymtomsRootcauseList(String project,String json) {
		log.debug("Enter to getSymtomsRootcauseList");
		JSONObject inputObj = new JSONObject(json);
		Set<String> inputKeys = inputObj.keySet();
		String response=null;
		String response1=null;
		String searchString=null;
		String searchString1=null;
		JSONArray filterTermsname = new JSONArray();
		JSONArray filterTermsdata = new JSONArray();
		List<SymptomRootcauseMapEntity> entityIter = null;
		PageRequest pageable = PageRequest.of(0, 10000);
		entityIter = symptomRootcauseMappingRepository.findByProject(project,pageable);

		JSONArray filterTermsArr = new JSONArray();
		String filterStr="";
		List<String> list=new  ArrayList<>();
		if(!inputObj.keySet().isEmpty())
		{ 
			for (String key : inputKeys) {
				JSONArray TermsValue =  inputObj.getJSONArray(key);
				filterTermsname.put(key); 
				filterStr=filterStr+" (productattributes.attribute_name.keyword:\\\""+key+"\\\" AND (productattributes.attribute_data.keyword:";
				for (int result = 0; result < TermsValue.length(); result++) {
					filterTermsdata.put(TermsValue.get(result));
					if (result == 0)
						filterStr=filterStr+"\\\""+TermsValue.get(result)+"\\\"";
					else
						filterStr=filterStr+" OR \\\""+TermsValue.get(result)+"\\\"";
					if(result == TermsValue.length()-1)
						filterStr=filterStr+")) AND";
				}
			} 
		searchString="{\"size\": 0,\"query\":{\"bool\":{\"filter\": {\"query_string\" : {\"query\" : \""+filterStr+" project.keyword:\\\""+project+"\\\"\"}}}},\"aggs\": { \"symptoms\": {\"terms\": {\"field\": \"symptoms.keyword\",\"size\":10000}}}}";
		
		 try {
				response=esClient.performRequest("POST", "/symptomrootcausemapping/_search/", searchString);
				//response1=esClient.performRequest("POST", "/producttriage/_search/", searchString1);
				System.out.println(response);
				//System.out.println(response1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String keyVal=null;
			JSONObject jsonObj = new JSONObject(response);
			JSONObject quoteJson = jsonObj.getJSONObject("aggregations").getJSONObject("symptoms");
			JSONArray recs = quoteJson.getJSONArray("buckets"); 
			for (int i = 0; i < recs.length(); ++i) {
				JSONObject jsn = recs.getJSONObject(i);
				keyVal = jsn.getString("key");
				list.add(keyVal);
			}
			System.out.println(list);
		}
		
		/*String keyVal1=null;
		List<String> list2=new  ArrayList<>();
		JSONObject jsonObj1 = new JSONObject(response1);
		JSONObject quoteJson1 = jsonObj1.getJSONObject("aggregations").getJSONObject("rootcause");
		JSONArray recs1 = quoteJson1.getJSONArray("buckets"); 
		for (int i = 0; i < recs1.length(); ++i) {
			JSONObject jsn1 = recs1.getJSONObject(i);
			keyVal1 = jsn1.getString("key");
			list2.add(keyVal1);
		}
		System.out.println(list2);*/
		
		List<SymptomRootcauseMappingList> sr = new ArrayList<>();
		for (SymptomRootcauseMapEntity srm : entityIter) {
			if(inputObj.keySet().isEmpty() || (!inputObj.keySet().isEmpty() && list.contains(srm.getSymptoms())))
			{
			SymptomRootcauseMappingList srcm = new SymptomRootcauseMappingList();
			srcm.setSymptoms(srm.getSymptoms());
			srcm.setRootcause(srm.getRootcause());
			srcm.setRootcausetitle(srm.getRcTitle());
			srcm.setRelevancy(srm.getRelevency());
			srcm.setStatus(srm.getStatus());
			sr.add(srcm);
			}
		}
		Map<String, List<SymptomRootcauseMappingList>> resultSet = sr.stream()
				.collect(Collectors.groupingBy(SymptomRootcauseMappingList::getSymptoms));

		JSONArray jsArray1 = new JSONArray();
		for (Entry<String,List<SymptomRootcauseMappingList>> values : resultSet.entrySet()) {
			List<SymptomRootcauseMappingList> list1 = new ArrayList<>();
			list1.addAll(values.getValue());
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put(ProductTriageConstants.SYMPTOMS, values.getKey());
			JSONArray jsArray2 = new JSONArray();
			JSONArray jsArray = new JSONArray(list1);
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsArray.get(i).toString());
				jsonObject.remove(ProductTriageConstants.SYMPTOMS);
				jsArray2.put(jsonObject);
			}
			jsonObject1.put(ProductTriageConstants.ROOTCAUSEDATA, jsArray2);
			jsArray1.put(jsonObject1);


		}
		return jsArray1.toString();

	}

	@Override
	public String getRootcauseSymtomsList(String project,String json) {
		JSONObject inputObj = new JSONObject(json);
		Set<String> inputKeys = inputObj.keySet();
		String response=null;
		String response1=null;
		String searchString=null;
		String searchString1=null;
		JSONArray filterTermsname = new JSONArray();
		JSONArray filterTermsdata = new JSONArray();
		log.debug("Enter to getRootcauseSymtomsList");
		List<SymptomRootcauseMapEntity> entityIter = null;
		PageRequest pageable = PageRequest.of(0, 10000);
		entityIter = symptomRootcauseMappingRepository.findByProject(project,pageable);
		//entityIter = symptomRootcauseMappingRepository.findAll();
		JSONArray filterTermsArr = new JSONArray();
		String filterStr="";
		List<String> list=new  ArrayList<>();
		if(!inputObj.keySet().isEmpty())
		{ 
			for (String key : inputKeys) {
				JSONArray TermsValue =  inputObj.getJSONArray(key);
				filterTermsname.put(key); 
				filterStr=filterStr+" (productattributes.attribute_name.keyword:\\\""+key+"\\\" AND (productattributes.attribute_data.keyword:";
				for (int result = 0; result < TermsValue.length(); result++) {
					filterTermsdata.put(TermsValue.get(result));
					if (result == 0)
						filterStr=filterStr+"\\\""+TermsValue.get(result)+"\\\"";
					else
						filterStr=filterStr+" OR \\\""+TermsValue.get(result)+"\\\"";
					if(result == TermsValue.length()-1)
						filterStr=filterStr+")) AND";
				}
			} 
		searchString="{\"size\": 0,\"query\":{\"bool\":{\"filter\": {\"query_string\" : {\"query\" : \""+filterStr+" project.keyword:\\\""+project+"\\\"\"}}}},\"aggs\": { \"rootcause\": {\"terms\": {\"field\": \"rootcause.keyword\",\"size\":10000}}}}";
		
		 try {
				response=esClient.performRequest("POST", "/symptomrootcausemapping/_search/", searchString);
				//response1=esClient.performRequest("POST", "/producttriage/_search/", searchString1);
				System.out.println(response);
				//System.out.println(response1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String keyVal=null;
			JSONObject jsonObj = new JSONObject(response);
			JSONObject quoteJson = jsonObj.getJSONObject("aggregations").getJSONObject("rootcause");
			JSONArray recs = quoteJson.getJSONArray("buckets"); 
			for (int i = 0; i < recs.length(); ++i) {
				JSONObject jsn = recs.getJSONObject(i);
				keyVal = jsn.getString("key");
				list.add(keyVal);
			}
			System.out.println(list);
		}
		
		/*String keyVal1=null;
		List<String> list2=new  ArrayList<>();
		JSONObject jsonObj1 = new JSONObject(response1);
		JSONObject quoteJson1 = jsonObj1.getJSONObject("aggregations").getJSONObject("rootcause");
		JSONArray recs1 = quoteJson1.getJSONArray("buckets"); 
		for (int i = 0; i < recs1.length(); ++i) {
			JSONObject jsn1 = recs1.getJSONObject(i);
			keyVal1 = jsn1.getString("key");
			list2.add(keyVal1);
		}
		System.out.println(list2);
		*/
		
		List<RootcauseSymptomMappingList> sr = new ArrayList<>();
		for (SymptomRootcauseMapEntity srm : entityIter) {
			if(inputObj.keySet().isEmpty() || (!inputObj.keySet().isEmpty() && list.contains(srm.getRootcause())))
			{
			RootcauseSymptomMappingList srcm = new RootcauseSymptomMappingList();
			srcm.setSymptoms(srm.getSymptoms());
			srcm.setRootcause(srm.getRootcause());
			srcm.setLeadingQuestion(srm.getLeadingQuestion());
			srcm.setRelevancy(srm.getRelevency());
			srcm.setStatus(srm.getStatus());
			sr.add(srcm);
			}
		}
		Map<String, List<RootcauseSymptomMappingList>> resultSet = sr.stream()
				.collect(Collectors.groupingBy(RootcauseSymptomMappingList::getRootcause));

		JSONArray jsArray1 = new JSONArray();
		for (Entry<String,List<RootcauseSymptomMappingList>> values : resultSet.entrySet()) {
			List<RootcauseSymptomMappingList> list1 = new ArrayList<>();
			list1.addAll(values.getValue());
			JSONObject jsonObject1 = new JSONObject();
			jsonObject1.put(ProductTriageConstants.ROOTCAUSE, values.getKey());
			JSONArray jsArray2 = new JSONArray();
			JSONArray jsArray = new JSONArray(list1);
			for (int i = 0; i < jsArray.length(); i++) {
				JSONObject jsonObject = new JSONObject(jsArray.get(i).toString());
				jsonObject.remove(ProductTriageConstants.ROOTCAUSE);
				jsArray2.put(jsonObject);
			}
			jsonObject1.put(ProductTriageConstants.ROOTCAUSEDATA, jsArray2);
			jsArray1.put(jsonObject1);



		}
		return jsArray1.toString();

	}

	@Override
	public String updateRootcause(SymptomRootcauseMapping symptomRootcauseMapping) {
		log.debug("Enter to updateRootcause");
		String requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.UPDATEROOTCAUSEQUERY); 
		String requestBody = String.format(triageConfiguration.getValue(), symptomRootcauseMapping.getRootcause(),symptomRootcauseMapping.getRcTitle(),symptomRootcauseMapping.getProject());
		JSONObject requestJson = new JSONObject(requestBody);
		String response = null;
		try {
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in updateRootcause##{}", e.getMessage());
		}
		return response;
	}

	@Override
	public String deleteRootcause(String inputJson) {
		log.debug("Enter to deleteRootcause");
		JSONObject inputObj = new JSONObject(inputJson);
		String requestUrl = null;
		String requestUrl1 = null;
		//TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.DELETEROOTCAUSEQUERY); 
		//TriageConfiguration triConfigProductTriage1 = genericCollectionDao.getConfigurationData(ProductTriageConstants.DELETEROOTCAUSEQUERY_PRODUCT_TRIAGE_COLL);
		String requestBody=null;
		String requestBody1=null;
		if(inputObj.has("newRootcause")) {
			requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
			requestUrl1 = "/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.UPDATEBYQUERY;
			requestBody="{\"script\": {\"source\": \"if (ctx._source.rootcause == params.rootcause) { ctx._source.rootcause=params.newRootcause }\", \"lang\": \"painless\", \"params\": {\"rootcause\": \""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\",\"newRootcause\": \""+inputObj.getString("newRootcause")+"\"}},\"query\": {\"bool\": {\"filter\": [ {\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"rootcause.keyword\": \""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
			requestBody1 = "{\"script\": {\"source\": \"if (ctx._source.rootcause.contains(params.rootcause)) { ctx._source.rootcause.add(params.newRootcause);ctx._source.rootcause.remove(ctx._source.rootcause.indexOf(params.rootcause))}\", \"lang\": \"painless\", \"params\": {\"rootcause\": \""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\",\"newRootcause\":\""+inputObj.getString("newRootcause")+"\"}},\"query\": {\"bool\": {\"filter\": [ {\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"rootcause.keyword\": \""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
		}else {
			requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DELETEBYQUERY;
			requestUrl1 = "/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.UPDATEBYQUERY;
			requestBody ="{\"query\": {\"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"rootcause.keyword\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
			requestBody1="{\"script\": {\"source\": \"if (ctx._source.rootcause.contains(params.tag)) { ctx._source.rootcause.remove(ctx._source.rootcause.indexOf(params.tag)) }\",\"lang\": \"painless\",\"params\": {\"tag\": \""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}},\"query\": { \"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\" }}]}}}";
			//String requestBody = String.format(triageConfiguration.getValue(), inputObj.getString(ProductTriageConstants.ROOTCAUSE),inputObj.getString(ProductTriageConstants.PROJECT));
			//String requestBody1 = String.format(triConfigProductTriage1.getValue(), inputObj.getString(ProductTriageConstants.ROOTCAUSE),inputObj.getString(ProductTriageConstants.PROJECT));
		}
		JSONObject requestJson = new JSONObject(requestBody);
		JSONObject requestJson1 = new JSONObject(requestBody1);
		String response = null;
		try {
			esClient.performRequest("POST", requestUrl1, requestJson1.toString());
			response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in deleteRootcause ", e);
		}
		return response;
	}

	@Override
	public String populateRecommendedSolutions(String inputList,String project) {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.RECOMMENDED_SOLUTION); 
		TriageConfiguration sizeConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.RESULT_SIZE);
		String requestUrl = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION+"/_search?size="+sizeConfiguration.getValue();
		String requestBody = String.format(triageConfiguration.getValue(), inputList,project);
		String response = null;
		try {
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestBody);
		} catch (AgentAnalyticsException | IOException e) {
			log.error("Exception in populateRecommendedSolutions", e);
		}
		log.info("populateRecommendedSolutions response-->{}",response);
		return response;
	}


	public String addSymptomsOld(SymptomsDetails symptomsDetailsModal) throws AgentAnalyticsException {
		log.debug("Enter to createSymptoms");
		String response = null;
		try {
			JSONObject sourceObject = new JSONObject(symptomsDetailsModal);
			JSONObject requestJson = new JSONObject();
			requestJson.put("symptoms", symptomsDetailsModal.getSymptoms());
			requestJson.put("leadingquestion", symptomsDetailsModal.getLeadingquestion());
			requestJson.put("relevency", "0");
			requestJson.put("status", "Yet to Review");
			requestJson.put("rootcause", symptomsDetailsModal.getRootcauses());
			requestJson.put("project", symptomsDetailsModal.getProject());
			JSONObject requestJson1 = new JSONObject();
			JSONArray attributeArray = new JSONArray(sourceObject.get("productattributes").toString());
			for (int result = 0; result < attributeArray.length(); result++) {
				JSONObject src = attributeArray.getJSONObject(result);
				requestJson1.put(src.get("attributename").toString(), src.get("attributevalue"));
			}
			List<String> list=new ArrayList<String>();
			List<String> list1=new ArrayList<String>();
		//	list.add(symptomsDetailsModal.getSymptoms());
	//		list1.add( symptomsDetailsModal.getRootCause());
			requestJson1.put("symptoms",list );
			requestJson1.put("description", symptomsDetailsModal.getDescription());
			requestJson1.put("rootcause",list1);
			requestJson1.put("project", symptomsDetailsModal.getProject());
			//requestJson1.put("resolution", symptomsDetailsModal.getResolution());
			String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DOC;
			String requestUrl1 ="/"+ ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.DOC;
			response = esClient.performRequest("POST", requestUrl, requestJson.toString());
			response = esClient.performRequest("POST", requestUrl1, requestJson1.toString());
			log.debug("response in createSymptoms {}",response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in createSymptoms {}", e);
		}
		return ProductTriageConstants.SUCCESS;
	}
	
	
	public String addSymptomsForTwoCollection(SymptomsDetails symptomsDetailsModal) throws AgentAnalyticsException {
		log.debug("Enter to createSymptoms");
		String response = null;
		try {
			String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DOC;
			String requestUrl1 ="/"+ ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.DOC;
			JSONObject sourceObject = new JSONObject(symptomsDetailsModal);
			JSONObject requestJson = new JSONObject();
			JSONObject requestJson1 = new JSONObject();
			List<String> rclist=new ArrayList<String>();
			JSONArray rootcauseArray = new JSONArray(sourceObject.get("rootcause").toString());
			JSONArray descriptionArray = new JSONArray(sourceObject.get("description").toString());
			JSONArray attributeArray = new JSONArray(sourceObject.get("productattributes").toString());
			for (int result = 0; result < rootcauseArray.length(); result++) {
				JSONObject src = rootcauseArray.getJSONObject(result);
				requestJson.put("symptoms",symptomsDetailsModal.getSymptoms());
				requestJson.put("rootcause",src.get("rootcause"));
				rclist.add(src.get("rootcause").toString());
				requestJson.put("rootcause_title",src.get("rootcauseTitle"));
				requestJson.put("relevency", "0");
				requestJson.put("status", "Yet to Review");
				requestJson.put("leadingquestion", symptomsDetailsModal.getLeadingquestion());
				requestJson.put("project", symptomsDetailsModal.getProject());
				response = esClient.performRequest("POST", requestUrl, requestJson.toString());
			}
			for (int desresult = 0; desresult < descriptionArray.length(); desresult++) {
				requestJson1.put("description",descriptionArray.get(desresult));
				List<String> symlist=new ArrayList<String>();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				symlist.add(symptomsDetailsModal.getSymptoms());
				requestJson1.put("symptoms",symlist);
				requestJson1.put("rootcause",rclist);
				for (int attributeresult = 0; attributeresult < attributeArray.length(); attributeresult++) {
					JSONObject src1 = attributeArray.getJSONObject(attributeresult);
					requestJson1.put(src1.get("attributename").toString(), src1.get("attributevalue"));
				}
				requestJson1.put("project", symptomsDetailsModal.getProject());
				requestJson1.put("addedBy", "user");
				requestJson1.put("timestamp", timestamp);
				response = esClient.performRequest("POST", requestUrl1, requestJson1.toString());
			}
			log.debug("response in AddSymptoms {}",response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in AddSymptoms {}", e);
		}
		return ProductTriageConstants.SUCCESS;
	}


	@Override
	public String getSimilarSymptomsSearch(String symptoms,String project) throws AgentAnalyticsException, IOException {
		JSONArray responseList = new JSONArray();
		List<String> symlist=new ArrayList<String>();
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.SIMILARSYMPTOMSSEARCHQUERY); 
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		//String requestBody = String.format(triageConfiguration.getValue(), ProductTriageConstants.SYMPTOMS);
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}}]}},\"size\": 50 }";
		//String requestBody="{\"query\": {\"bool\": {\"should\": [{\"more_like_this\": {\"like\": \""+symptoms+"\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"analyzer\": \"cess_analyzer\",\"fields\": [\"symptoms\",\"leadingquestion\"]}},{\"multi_match\": {\"fields\": [\"symptoms\",\"leadingquestion\"] ,\"query\": \""+symptoms+"\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\"\\\" "+symptoms+" \\\"\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1}},\"size\": 50 }";
		JSONObject requestJson1 = new JSONObject(requestBody);
		String response = esClient.performRequest("GET", requestUrl, requestJson1.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject res= new JSONObject();
					if(!symlist.contains(src.getString("symptoms"))) {
						res.put("symptoms", src.getString("symptoms"));
						res.put("symptom_title", src.getString("symptom_title"));
						res.put("leadingquestion", src.getString("leadingquestion"));
						responseList.put(res);
					}
				}
			}
		}
		return responseList.toString();
	}

	@Override
	public String getSimilarRootCauseSearch(String rootcause,String project) throws AgentAnalyticsException, IOException {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.SIMILARROOTCAUSESEARCHQUERY); 
		JSONArray responseList = new JSONArray();
		List<String> rclist=new ArrayList<String>();
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		//String requestBody = String.format(triageConfiguration.getValue(), ProductTriageConstants.ROOTCAUSE);
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}}]}},\"size\": 50 }";
		//String requestBody="{\"query\": {\"bool\": {\"should\": [{\"more_like_this\": {\"like\": \""+rootcause+"\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"analyzer\": \"cess_analyzer\",\"fields\": [\"rootcause\",\"rootcause_title\"]}},{\"multi_match\": {\"fields\": [\"rootcause\",\"rootcause_title\"] ,\"query\": \""+rootcause+"\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\"\\\""+rootcause+" \\\"\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1}},\"size\": 50 }";
		JSONObject requestJson1 = new JSONObject(requestBody);
		String response = esClient.performRequest("GET", requestUrl, requestJson1.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject res= new JSONObject();
					if(!rclist.contains(src.getString("rootcause"))) {
						rclist.add( src.getString("rootcause"));
						res.put("rootcause", src.getString("rootcause"));
						res.put("rootcause_title", src.getString("rootcause_title"));
						responseList.put(res);
					}
				}
			}
		}
		return responseList.toString();
	}

	
	public String addRootcauseForTwoCollection(RootcauseDetails RootcauseDetailsModal) throws AgentAnalyticsException {
		log.debug("Enter to createRootcause");
		String response = null;
		try {
			String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DOC;
			String requestUrl1 ="/"+ ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.DOC;
			JSONObject sourceObject = new JSONObject(RootcauseDetailsModal);
			JSONObject requestJson = new JSONObject();
			JSONObject requestJson1 = new JSONObject();
			List<String> symlist=new ArrayList<String>();
			JSONArray symptomsArray = new JSONArray(sourceObject.get("symptoms").toString());
			JSONArray resolutonArray = new JSONArray(sourceObject.get("resolution").toString());
			JSONArray attributeArray = new JSONArray(sourceObject.get("productattributes").toString());
			for (int result = 0; result < symptomsArray.length(); result++) {
				JSONObject src = symptomsArray.getJSONObject(result);
				requestJson.put("rootcause",RootcauseDetailsModal.getRootcause());
				requestJson.put("symptoms",src.get("symptoms"));
				symlist.add(src.get("symptoms").toString());
				requestJson.put("relevency", "0");
				requestJson.put("status", "Yet to Review");
				requestJson.put("rootcause_title", RootcauseDetailsModal.getRootcauseTitle());
				requestJson.put("leadingquestion", src.get("symptomsTitle"));
				requestJson.put("project", RootcauseDetailsModal.getProject());
				response = esClient.performRequest("POST", requestUrl, requestJson.toString());
			}
			for (int resresult = 0; resresult < resolutonArray.length(); resresult++) {
				requestJson1.put("resolution",resolutonArray.get(resresult));
				List<String> rclist=new ArrayList<String>();
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				rclist.add(RootcauseDetailsModal.getRootcause());
				requestJson1.put("symptoms",symlist);
				requestJson1.put("rootcause",rclist);
				for (int attributeresult = 0; attributeresult < attributeArray.length(); attributeresult++) {
					JSONObject src1 = attributeArray.getJSONObject(attributeresult);
					requestJson1.put(src1.get("attributename").toString(), src1.get("attributevalue"));
				}
				requestJson1.put("project", RootcauseDetailsModal.getProject());
				requestJson1.put("addedBy", "user");
				requestJson1.put("timestamp", timestamp);
				response = esClient.performRequest("POST", requestUrl1, requestJson1.toString());
			}
			log.debug("response in AddRootcause {}",response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in AddRootcause {}", e);
		}
		return ProductTriageConstants.SUCCESS;
	}
	

	
	@Override
	public String symptomsUnmap(String inputJson) {
		log.debug("Enter to symptomsUnmap");
		JSONObject inputObj = new JSONObject(inputJson);
		String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DELETEBYQUERY;
		String requestUrl1 = "/" + ProductTriageConstants.PRODUCT_TRIAGE_COLL + "/_update_by_query";
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"symptoms.keyword\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}},{\"term\": {\"rootcause.keyword\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
		String requestBody1 = "{\"script\": {\"source\": \"if (ctx._source.symptoms.contains(params.symptoms) && ctx._source.rootcause.contains(params.rootcause)) { ctx._source.symptoms.remove(ctx._source.symptoms.indexOf(params.symptoms)) }\",\"lang\": \"painless\",\"params\": {\"symptoms\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\",\"rootcause\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\" }},\"query\": { \"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\" }},{\"term\": {\"symptoms.keyword\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}},{\"term\":{\"rootcause.keyword\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
		JSONObject requestJson = new JSONObject(requestBody);
		JSONObject requestJson1 = new JSONObject(requestBody1);
		String response = null;
		try {
			esClient.performRequest("POST", requestUrl1, requestJson1.toString());
			response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in symptomsUnmap ", e);
		}
		return response;
	}
	
	
	@Override
	public String rootcauseUnmap(String inputJson) {
		log.debug("Enter to rootcauseUnmap");
		JSONObject inputObj = new JSONObject(inputJson);
		String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DELETEBYQUERY;
		String requestUrl1 = "/" + ProductTriageConstants.PRODUCT_TRIAGE_COLL + "/_update_by_query";
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\"}},{\"term\": {\"symptoms.keyword\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}},{\"term\": {\"rootcause.keyword\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
		String requestBody1 = "{\"script\": {\"source\": \"if (ctx._source.symptoms.contains(params.symptoms) && ctx._source.rootcause.contains(params.rootcause)) { ctx._source.rootcause.remove(ctx._source.rootcause.indexOf(params.rootcause)) }\",\"lang\": \"painless\",\"params\": {\"symptoms\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\",\"rootcause\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\" }},\"query\": { \"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+inputObj.getString(ProductTriageConstants.PROJECT)+"\" }},{\"term\": {\"symptoms.keyword\":\""+inputObj.getString(ProductTriageConstants.SYMPTOMS)+"\"}},{\"term\":{\"rootcause.keyword\":\""+inputObj.getString(ProductTriageConstants.ROOTCAUSE)+"\"}}]}}}";
		JSONObject requestJson = new JSONObject(requestBody);
		JSONObject requestJson1 = new JSONObject(requestBody1);
		String response = null;
		try {
			esClient.performRequest("POST", requestUrl1, requestJson1.toString());
			response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in rootcauseUnmap ", e);
		}
		return response;
	}
	
	@Override
	public String updateSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping) {
		log.debug("Enter to updateSymptomsrootcause");
		String requestUrl ="/"+ ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.UPDATESYMPTOMSROOTCAUSEQUERY); 
		//String requestBody="{ \"script\": { \"source\": \"if (ctx._source.symptoms == params.symptoms) { ctx._source.symptom_title=params.symptom_title ;ctx._source.leadingquestion=params.leadingquestion;ctx._source.rootcause=params.rootcause }\",  \"lang\": \"painless\", \"params\": { \"symptoms\": \""+symptomRootcauseMapping.getSymptoms()+"\",\"symptom_title\": \""+symptomRootcauseMapping.getSymTitle()+"\",\"leadingquestion\":\""+symptomRootcauseMapping.getLeadingquestion()+"\",\"rootcause\":\""+symptomRootcauseMapping.getRootcause()+"\" } }, \"query\": {\"bool\": {\"filter\": [ {\"term\": {\"project.keyword\": \""+symptomRootcauseMapping.getProject()+"\"}},{\"term\": {\"symptoms.keyword\": \""+symptomRootcauseMapping.getSymptoms()+"\"}}]}}}";
		String requestBody = String.format(triageConfiguration.getValue(), symptomRootcauseMapping.getSymptoms(),symptomRootcauseMapping.getSymTitle(),symptomRootcauseMapping.getLeadingquestion(),symptomRootcauseMapping.getProject(),symptomRootcauseMapping.getRootcause());
		JSONObject requestJson = new JSONObject(requestBody);
		String response = null;
		try {
			response = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in updateSymptomsrootcause {}", e);
		}
		return response;
	} 
	

	public String addSymptoms(SymptomsDetails symptomsDetailsModal) throws AgentAnalyticsException {
		log.debug("Enter to createSymptoms");
	String response = null;
	try {
		String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DOC;
		JSONObject sourceObject = new JSONObject(symptomsDetailsModal);
		JSONObject requestJson = new JSONObject();
		JSONObject requestJson1 = new JSONObject();
		List<String> rclist=new ArrayList<String>();
		JSONArray rootcauseArray = new JSONArray(sourceObject.get("rootcauses").toString());
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		for (int result = 0; result < rootcauseArray.length(); result++) {
			JSONObject src = rootcauseArray.getJSONObject(result);
			requestJson.put("symptoms",symptomsDetailsModal.getSymptoms());
			requestJson.put("rootcause",src.get("rootcause"));
			rclist.add(src.get("rootcause").toString());
			requestJson.put("rootcause_title",src.get("rootcauseTitle"));
			requestJson.put("relevency", rootcauseArray.length());
			requestJson.put("status", "Yet to Review");
			requestJson.put("leadingquestion", symptomsDetailsModal.getLeadingquestion());
			requestJson.put("project", symptomsDetailsModal.getProject());
			requestJson.put("addedBy", "user");
			String rcDet = getRootcausedetails(src.get("rootcause").toString(), symptomsDetailsModal.getProject());
			JSONArray rcJson = new JSONArray(rcDet);
			if (rcJson.length() > 0)
				requestJson.put("resolution",rcJson.getJSONObject(0).get("resolution"));
			else
				requestJson.put("resolution", symptomsDetailsModal.getResolution());
			
			requestJson.put("timestamp", timestamp);
			requestJson.put("productattributes", symptomsDetailsModal.getProductattributes());
			requestJson.put("description", symptomsDetailsModal.getDescription());
			response = esClient.performRequest("POST", requestUrl, requestJson.toString());
			
		}
		
		log.debug("response in AddSymptoms {}",response);
	} catch (AgentAnalyticsException | IOException e) {
		log.debug("Exception in AddSymptoms {}", e);
	}
	return ProductTriageConstants.SUCCESS;

	}

	@Override
	public String addRootcause(RootcauseDetails RootcauseDetailsModal) throws AgentAnalyticsException {
		log.debug("Enter to createRootcause");
		String response = null;
		try {
			String requestUrl = "/" + ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.DOC;
			JSONObject sourceObject = new JSONObject(RootcauseDetailsModal);
			JSONObject requestJson = new JSONObject();
			List<String> symlist=new ArrayList<String>();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			JSONArray symptomsArray = new JSONArray(sourceObject.get("symptoms").toString());
			for (int result = 0; result < symptomsArray.length(); result++) {
				JSONObject src = symptomsArray.getJSONObject(result);
				requestJson.put("rootcause",RootcauseDetailsModal.getRootcause());
				requestJson.put("symptoms",src.get("symptoms"));
				requestJson.put("leadingquestion",src.get("leadingquestion"));
				symlist.add(src.get("symptoms").toString());
				requestJson.put("relevency", symptomsArray.length());
				requestJson.put("status", "Yet to Review");
				requestJson.put("rootcause_title", RootcauseDetailsModal.getRootcauseTitle());
				requestJson.put("addedBy", "user");
				requestJson.put("timestamp", timestamp);
				requestJson.put("productattributes", RootcauseDetailsModal.getProductattributes());
				requestJson.put("resolution",RootcauseDetailsModal.getResolution());
				String symDet = getSymptomsdetails(src.get("symptoms").toString(), RootcauseDetailsModal.getProject());
				JSONArray symptomJson = new JSONArray(symDet);
				if (symptomJson.length() > 0)
					requestJson.put("description",symptomJson.getJSONObject(0).get("description"));
				else
					requestJson.put("description",RootcauseDetailsModal.getDescription());
				
				requestJson.put("project", RootcauseDetailsModal.getProject());
				response = esClient.performRequest("POST", requestUrl, requestJson.toString());
			}
			
			log.debug("response in AddRootcause {}",response);
		} catch (AgentAnalyticsException | IOException e) {
			log.debug("Exception in AddRootcause {}", e);
		}
		return ProductTriageConstants.SUCCESS;
	}
	

	@Override
	public String getSymptomsdetails(String symptoms,String project) throws AgentAnalyticsException, IOException {
		JSONArray rootList = new JSONArray();
		JSONArray responseList = new JSONArray();
		JSONObject res= null;
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}},{\"term\":{\"symptoms.keyword\": \""+symptoms+"\"}}]}},\"size\": 10000 }";
		JSONObject requestJson = new JSONObject(requestBody);
		String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					    res= new JSONObject();
					    if(src.has("rootcause")&&src.getString("rootcause") != null) {
					    	res.put("rootcause", src.getString("rootcause"));
					    }
					    if(src.has("rootcause_title")&&src.getString("rootcause_title") != null) {
					    	res.put("rootcauseTitle", src.getString("rootcause_title"));
					    }
						rootList.put(res);
				}
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject resp= new JSONObject();
					 if(src.has("symptoms")&&src.getString("symptoms") != null) {
						 resp.put("symptoms", src.getString("symptoms"));
					 }
					 if(src.has("leadingquestion")&&src.getString("leadingquestion") != null) {
						 resp.put("leadingquestion", src.getString("leadingquestion"));
					 }
					 if(src.has("rootcause")&&src.getString("rootcause") != null) {
							resp.put("rootcauses", rootList);
					 }
					 if(src.has("productattributes")&&src.get("productattributes") != null) {
						 resp.put("productattributes", src.get("productattributes")); 
					 }
					if(src.has("description")) {
						resp.put("description", src.get("description"));
					}
					responseList.put(resp);
					break;
				}
			}
		}
		return responseList.toString();
	}
	
	@Override
	public String getSymptomsdetailsSet(String project,String json) throws AgentAnalyticsException, IOException {
		JSONArray responseList = new JSONArray();
		JSONObject inputObj = new JSONObject(json);
		Set<String> inputKeys = inputObj.keySet();
		List<String> symlist=new ArrayList<String>();
		JSONArray filterTermsname = new JSONArray();
		JSONArray filterTermsdata = new JSONArray();
		String requestBody=null;
		String filterStr="";
		if(!inputObj.keySet().isEmpty())
		{ 
			for (String key : inputKeys) {
				JSONArray TermsValue =  inputObj.getJSONArray(key);
				filterTermsname.put(key); 
				filterStr=filterStr+" (productattributes.attribute_name.keyword:\\\""+key+"\\\" AND (productattributes.attribute_data.keyword:";
				for (int result = 0; result < TermsValue.length(); result++) {
					filterTermsdata.put(TermsValue.get(result));
					if (result == 0)
						filterStr=filterStr+"\\\""+TermsValue.get(result)+"\\\"";
					else
						filterStr=filterStr+" OR \\\""+TermsValue.get(result)+"\\\"";
					if(result == TermsValue.length()-1)
						filterStr=filterStr+")) AND";
				}
			}
			requestBody="{\"query\":{\"bool\":{\"filter\": {\"query_string\" : {\"query\" : \""+filterStr+" project.keyword:\\\""+project+"\\\"\"}}}},\"size\": 10000}";
		//requestBody="{\"query\":{\"bool\":{\"filter\":[{\"terms\":{\"productattributes.attribute_name.keyword\":"+filterTermsname +"}},{\"terms\":{\"productattributes.attribute_data.keyword\":"+filterTermsdata +"}},{\"term\":{\"project.keyword\":\""+project+"\"}}]}},\"size\": 10000}";
		}else {
			requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}}]}},\"size\": 10000}";
			
		}
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		JSONObject requestJson = new JSONObject(requestBody);
		String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject resp= new JSONObject();
					if(!symlist.contains(src.getString("symptoms"))) {
						if(src.has("symptoms")&&src.getString("symptoms") != null) {
							symlist.add(src.get("symptoms").toString());
							resp.put("symptoms", src.getString("symptoms"));
						}
						if(src.has("leadingquestion")&&src.getString("leadingquestion") != null) {
							resp.put("leadingquestion", src.getString("leadingquestion"));
						}
						if(src.has("timestamp")&&src.getString("timestamp") != null) {
							resp.put("timestamp", src.getString("timestamp"));
						}
						
						if(src.has("productattributes")&&src.get("productattributes") != null) {
						JSONArray attributeArray = new JSONArray(src.get("productattributes").toString());
						for (int attributeresult = 0; attributeresult < attributeArray.length(); attributeresult++) {
							JSONObject src1 = attributeArray.getJSONObject(attributeresult);
							resp.put(src1.get("attribute_name").toString(), src1.get("attribute_data"));
						}
						}
						//if(src.has("description")&&src.get("description") != null) {
							//resp.put("description", src.get("description"));
						//}
						responseList.put(resp);
					}
				}
			}
		}
		return responseList.toString();
	}
	
		@Override
	public String getProductattributes(SourceMap sourceMap, String project,String collectionId) {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.RESULT_SIZE); 
		String filedString =  ",\"aggs\": {"
				+ "\"%s\":{"
					+ "\"terms\": {"
						+ "\"field\":\"%s.keyword\",\"size\":"+Integer.parseInt(triageConfiguration.getValue().trim())+"}"; 
		String endCurlyBrackets = "}";
		String searchString = "{\"size\": 0";
      searchString = searchString.concat(String.format(filedString,ProductTriageConstants.SYMPTOMS,ProductTriageConstants.SYMPTOMS));
      endCurlyBrackets = endCurlyBrackets.concat("}}");
       List<String> fiels = sourceMap.getSourceFields();
		for(String str : fiels) {
			String result = String.format(filedString, str,str); 
			searchString = searchString.concat(result);
			endCurlyBrackets = endCurlyBrackets.concat("}}");
		}
		searchString = searchString.concat(endCurlyBrackets);	
		log.info("getProductattributes searchString {}",searchString);
		try {
			return esClient.performRequest("POST", "/" + collectionId +"/_search/", searchString );
		} catch (IOException e) {
			log.error("Exception at getProductattributes :-", e);
			return "";
		}

		
	}

	@Override
	public Map<String,String> getResolutionAndDescription(SymptomRootcauseMapping symptomRootcauseMapping, String project,String collectionId) {
		Map<String,String> map = new HashMap<>();
		try {
			String resolutionQuery = "{\"size\": 0,\"query\": {\"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+ project+"\"}},{\"term\": {\"rootcause.keyword\": \""+symptomRootcauseMapping.getRootcause()+"\"}}]}},\"aggs\": {\"my-agg-name\": {\"terms\": {\"field\": \"resolution.keyword\",\"size\":\"10\"}}}}";
			String descriptionQuery = "{\"size\": 0,\"query\": {\"bool\": {\"filter\": [{\"term\": {\"project.keyword\": \""+project+"\"}},{\"term\": {\"symptoms.keyword\": \""+symptomRootcauseMapping.getSymptoms()+"\"}}]}},\"aggs\": {\"my-agg-name\": {\"terms\": {\"field\": \"description.keyword\",\"size\":\"10\"}}}}";
			log.info("resolutionQuery --->{}",resolutionQuery);
			log.info("descriptionQuery --->{}",descriptionQuery);
			String resolutionResult = esClient.performRequest("POST", "/" + collectionId +"/_search/", resolutionQuery );
			String descriptionResult = esClient.performRequest("POST", "/" + collectionId +"/_search/", descriptionQuery );
			
			map.put("resolutionResult", resolutionResult);
			map.put("descriptionResult", descriptionResult);
			
			
		} catch (Exception e) {
			log.error("Exception at getResolutionAndDescription :-", e);
		}
		return map;
	}


	
	@Override
	public String getWizardFlowStatus(String project)throws IOException
	{
		JSONObject	src=null;
		List<JSONObject> responseList = new ArrayList<JSONObject>();
		List<String> symlist=new ArrayList<String>();
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}}]}},\"size\": 50 }";
		String response = esClient.performRequest("POST", "/monitoringlogflow/_search/", requestBody);
		System.out.println(""+response);
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject res= new JSONObject();
					if(!symlist.contains(src.getString("timestamp"))) {
						res.put("apiurl", src.getString("apiurl"));
						res.put("status", src.getString("status"));
						res.put("project", src.getString("project"));
						res.put("timestamp", src.getString("timestamp"));
						 responseList.add(res);
					}
					Collections.sort(responseList, new Comparator<JSONObject>() {
		                @Override
		                public int compare(JSONObject o1, JSONObject o2) {
		                    return o2.getString("timestamp").compareTo(o1.getString("timestamp"));
		                }
		            });
					//attributeArray.stream().sorted(Comparator.comparing(a -> ((JSONObject) a).get("distance")));
				}
			}
		}
		
		return responseList.toString();

	}

	@Override
	public String getRootcausedetailsSet(String project, String inpJson) throws IOException, JSONException {
		JSONArray responseList = new JSONArray();
		JSONObject inputObj = new JSONObject(inpJson);
		Set<String> inputKeys = inputObj.keySet();
		List<String> rootlist=new ArrayList<String>();
		JSONArray filterTermsname = new JSONArray();
		JSONArray filterTermsdata = new JSONArray();
		String requestBody=null;
		String filterStr="";
		if(!inputObj.keySet().isEmpty())
		{ 
			for (String key : inputKeys) {
				JSONArray TermsValue =  inputObj.getJSONArray(key);
				filterTermsname.put(key); 
				filterStr=filterStr+" (productattributes.attribute_name.keyword:\\\""+key+"\\\" AND (productattributes.attribute_data.keyword:";
				for (int result = 0; result < TermsValue.length(); result++) {
					filterTermsdata.put(TermsValue.get(result));
					if (result == 0)
						filterStr=filterStr+"\\\""+TermsValue.get(result)+"\\\"";
					else
						filterStr=filterStr+" OR \\\""+TermsValue.get(result)+"\\\"";
					if(result == TermsValue.length()-1)
						filterStr=filterStr+")) AND";
				}
			} 
			requestBody="{\"query\":{\"bool\":{\"filter\": {\"query_string\" : {\"query\" : \""+filterStr+" project.keyword:\\\""+project+"\\\"\"}}}},\"size\": 10000}";
			//requestBody="{\"query\":{\"bool\":{\"filter\":[{\"terms\":{\"productattributes.attribute_name.keyword\":"+filterTermsname +"}},{\"terms\":{\"productattributes.attribute_data.keyword\":"+filterTermsdata +"}},{\"term\":{\"project.keyword\":\""+project+"\"}}]}},\"size\": 10000}";
		}else {
			requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}}]}},\"size\": 10000}";

		}
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		JSONObject requestJson = new JSONObject(requestBody);
		String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject resp= new JSONObject();
					if(!rootlist.contains(src.getString("rootcause"))) {
						if(src.has("rootcause")&&src.getString("rootcause") != null) {
							rootlist.add(src.get("rootcause").toString());
							resp.put("rootcause", src.getString("rootcause"));
						}
						if(src.has("rootcause_title")&&src.getString("rootcause_title") != null) {
							resp.put("rootcauseTitle", src.getString("rootcause_title"));
						}
						if(src.has("timestamp")&&src.getString("timestamp") != null) {
							resp.put("timestamp", src.getString("timestamp"));
						}
						if(src.has("productattributes")&&src.get("productattributes") != null) {
							JSONArray attributeArray = new JSONArray(src.get("productattributes").toString());
							for (int attributeresult = 0; attributeresult < attributeArray.length(); attributeresult++) {
								JSONObject src1 = attributeArray.getJSONObject(attributeresult);
								resp.put(src1.get("attribute_name").toString(), src1.get("attribute_data"));
							}
						}
						//if(src.has("resolution")&&src.get("resolution") != null) {
							//resp.put("resolution", src.get("resolution"));
						//}
						responseList.put(resp);
					}
				}
			}
		}
		return responseList.toString();

	}

	@Override
	public String getRootcausedetails(String rootcause, String project) throws IOException, JSONException {
		JSONArray symList = new JSONArray();
		JSONArray responseList = new JSONArray();
		JSONObject res= null;
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		String requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}},{\"term\":{\"rootcause.keyword\": \""+rootcause+"\"}}]}},\"size\": 10000 }";
		JSONObject requestJson = new JSONObject(requestBody);
		String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					
					    res= new JSONObject();
					    if(src.has("symptoms")&&src.getString("symptoms") != null) {
					    	res.put("symptoms", src.getString("symptoms"));
					    }
					    if(src.has("leadingquestion")&&src.getString("leadingquestion") != null) {
					    	res.put("leadingquestion", src.getString("leadingquestion"));
					    }
					    symList.put(res);
				}
				for (int result = 0; result < results.length(); result++) {
					JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
					JSONObject resp= new JSONObject();
					 if(src.has("rootcause")&&src.getString("rootcause") != null) {
						 resp.put("rootcause", src.getString("rootcause"));
					 }
					 if(src.has("rootcause_title")&&src.getString("rootcause_title") != null) {
						 resp.put("rootcauseTitle", src.getString("rootcause_title"));
					 }
					 if(src.has("symptoms")&&src.getString("symptoms") != null) {
							resp.put("symptoms", symList);
					 }
					 if(src.has("productattributes")&&src.get("productattributes") != null) {
						 resp.put("productattributes", src.get("productattributes")); 
					 }
					if(src.has("resolution")&&src.get("resolution") != null) {
						resp.put("resolution", src.get("resolution"));
					}
					responseList.put(resp);
					break;
				}
			}
		}
		return responseList.toString();
	}
	
    @Override
	public String getSymptom(String project, String issue, String inpJson ) throws IOException, JSONException {
    	TriageConfiguration triageConfiguration;
    	JSONObject inputObj = new JSONObject(inpJson);
    	Set<String> inputKeys = inputObj.keySet();
		JSONArray responseList = new JSONArray();
		String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
		JSONArray filterTermsname = new JSONArray();
		JSONArray filterTermsdata = new JSONArray();
		String requestBody;
		String filterStr="";
		if(!inputObj.keySet().isEmpty())
		{ 
			for (String key : inputKeys) {
				JSONArray TermsValue =  inputObj.getJSONArray(key);
				filterTermsname.put(key); 
				filterStr=filterStr+" (productattributes.attribute_name.keyword:\\\""+key+"\\\" AND (productattributes.attribute_data.keyword:";
				for (int result = 0; result < TermsValue.length(); result++) {
					filterTermsdata.put(TermsValue.get(result));
					if (result == 0)
						filterStr=filterStr+"\\\""+TermsValue.get(result)+"\\\"";
					else
						filterStr=filterStr+" OR \\\""+TermsValue.get(result)+"\\\"";
					if(result == TermsValue.length()-1)
						filterStr=filterStr+")) AND";
				}
			} 
			String filterQry="\""+filterStr+" project.keyword:\\\""+project+"\\\"\"";
			triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.GETSYMPTOMS);
			//requestBody = String.format(triageConfiguration.getValue(),issue,filterTermsname,filterTermsdata,project );
			requestBody = String.format(triageConfiguration.getValue(),issue,filterQry );
			 // requestBody="{\"aggs\":{\"symptoms\":{\"terms\":{\"field\":\"symptoms.keyword\"},\"aggs\":{\"leading\":{\"terms\":{\"field\":\"leadingquestion.keyword\"}}}}},\"query\":{\"bool\":{\"should\":[{\"more_like_this\": {\"like\": \""+issue+"\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"analyzer\": \"cess_analyzer\",\"fields\": [\"description\"] }},{\"multi_match\": {\"fields\":  [\"description\"] ,\"query\": \""+issue+"\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\""+issue+"\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\":[{\"terms\":{\"productattributes.attribute_name.keyword\":"+filterTermsname+"}},{\"terms\":{\"productattributes.attribute_data.keyword\":"+filterTermsdata+"}},{\"term\":{\"project.keyword\":\""+project+"\"}}]}},\"size\":0}";
		}else {
			triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.GETSYMPTOMSWITHOUTFILTER);
			requestBody = String.format(triageConfiguration.getValue(),issue,project );
			// requestBody="{\"aggs\":{\"symptoms\":{\"terms\":{\"field\":\"symptoms.keyword\"},\"aggs\":{\"leading\":{\"terms\":{\"field\":\"leadingquestion.keyword\"}}}}},\"query\":{\"bool\":{\"should\":[{\"more_like_this\": {\"like\": \""+issue+"\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"analyzer\": \"cess_analyzer\",\"fields\": [\"description\"] }},{\"multi_match\": {\"fields\":  [\"description\"] ,\"query\": \""+issue+"\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\""+issue+"\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\":[{\"term\":{\"project.keyword\":\""+project+"\"}}]}},\"size\":0}";
		}
		JSONObject requestJson = new JSONObject(requestBody);
		String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("aggregations")) {
				JSONArray results = resultJson.getJSONObject("aggregations").getJSONObject("symptoms").getJSONArray("buckets");
				for (int result = 0; result < results.length(); result++) {
					JSONObject Json = results.getJSONObject(result);
					JSONObject res= new JSONObject();
					res.put("symptoms", Json.get("key"));
					res.put("score", Json.get("doc_count"));
					JSONArray leadingresults = Json.getJSONObject("leading").getJSONArray("buckets");
					for (int lqresult = 0; lqresult < leadingresults.length(); lqresult++) {
						JSONObject lqJson = leadingresults.getJSONObject(lqresult);
						res.put("leadingquestion", lqJson.get("key"));
					}
					responseList.put(res);
				}
			}
		}
		return responseList.toString();
	}

    @Override
    public String updatequestions(String project) throws IOException {
    	List<String> symlist=new ArrayList<String>();
    	String requestBody=null;
    	requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}}]}},\"size\": 10000}";
    	String requestUrl ="/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.SEARCH;
    	String requestUrl1 = "/"+ProductTriageConstants.SYMPTOM_ROOTCAUSE_COLLECTION + ProductTriageConstants.UPDATEBYQUERY;
    	JSONObject requestJson = new JSONObject(requestBody);
    	String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
    	if (null != response) {
    		JSONObject resultJson = new JSONObject(response);
    		if (resultJson.has("hits")) {
    			JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
    			for (int result = 0; result < results.length(); result++) {
    				JSONObject	src = results.getJSONObject(result).getJSONObject("_source");
    				if(!symlist.contains(src.getString("symptoms"))) {
    					if(src.has("symptoms")&&src.getString("symptoms") != null) {
    						symlist.add(src.get("symptoms").toString());
    						String sym=src.getString("symptoms");
    						String questions =getQuestions(project, src.getString("symptoms"));
    						String requestBody1="{\"script\": {\"source\": \"if (ctx._source.symptoms == params.symptoms && ctx._source.project == params.project ) {ctx._source.leadingquestion=params.leadingquestion}\",\"lang\": \"painless\",\"params\": {\"leadingquestion\":\""+questions+"\",\"symptoms\":\""+sym+"\",\"project\":\""+project+"\"}},\"query\": {\"bool\": {\"filter\": [{\"term\": {\"symptoms.keyword\": \""+sym+"\"}},{\"term\": {\"project.keyword\":\""+project+"\"}}]}}}";
    						JSONObject requestJson1 = new JSONObject(requestBody1);
    						response = esClient.performRequest(ProductTriageConstants.POST, requestUrl1, requestJson1.toString());
    					}
    				}
    			}
    		}
    	}
    	return response;
    }

    public String getQuestions(String project,String symptom) throws IOException {
    	JSONArray src=null;
    	String requestBody=null;
    	requestBody="{\"query\": {\"bool\": {\"filter\": [{\"term\":{\"project.keyword\": \""+project+"\"}},{\"term\":{\"name.keyword\": \""+symptom+"\"}}]}},\"size\": 10000 }";
    	String requestUrl ="/"+ProductTriageConstants.PRODUCTTRIAGE_UTTERANCES_QUESTIONS_COLLECTION + ProductTriageConstants.SEARCH;
    	JSONObject requestJson = new JSONObject(requestBody);
    	String response = esClient.performRequest("POST", requestUrl, requestJson.toString());
    	if (null != response) {
    		JSONObject resultJson = new JSONObject(response);
    		if (resultJson.has("hits")) {
    			JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
    			for (int result = 0; result < results.length(); result++) {
    				src = results.getJSONObject(result).getJSONObject("_source").getJSONArray("questions");
    			}
    		}
    	}
    	return src.get(0).toString();
    }


		


	
	

}
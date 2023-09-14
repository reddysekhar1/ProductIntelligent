package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.entity.InsightsEntity;
import com.intelliservice.agentanalytics.entity.ProductAttributesEntity;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.repository.InsightsRepository;
import com.intelliservice.agentanalytics.repository.ProductDetailsRepository;
import com.intelliservice.agentanalytics.utils.ESQueries;
import com.intelliservice.agentanalytics.utils.ESUtils;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Repository
public class InsightsDaoImpl implements InsightsDao {
	private static final Logger log = LoggerFactory.getLogger(InsightsDaoImpl.class);
	@Autowired
	ProductDetailsRepository productDetailsRepository;
	@Autowired
	InsightsRepository insightsRepository;
	@Autowired
	ESUtils esUtils;

	@Autowired
	ESClient esClient;

	@Autowired
	GenericCollectionDao genericCollectionDao;

	@Override
	public List<Insights> getSimilarIssues(String searchText) {
		List<InsightsEntity> similarIssues = insightsRepository.getSimilarIssues(searchText);
		return similarIssues.stream()
				.collect(Collectors.mapping(entity -> mapToPojo(entity), Collectors.toList()));



	}

	/*@Override
	public List<Insights> getSimilarIssues(String searchText, String product, String model) {
		Pageable pageable = null;
		Page<InsightsEntity> similarIssues = insightsRepository.getSimilarIssues(searchText, product, model, pageable);
		List<InsightsEntity> list = similarIssues.getContent();
		List<Insights> similarIssuesList = list.stream()
				.collect(Collectors.mapping(entity -> mapToPojo(entity), Collectors.toList()));

		return similarIssuesList;
	}*/

	JSONArray getSourceMap(String project) throws IOException {
		TriageConfiguration triageConfiguration;
		triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.GETSOURCEMAP); 
		String requestBody = String.format(triageConfiguration.getValue(),project);
		String sourceMapUrl = "/" + ProductTriageConstants.SOURCEMAP_COLLECTION + ProductTriageConstants.SEARCH;
		//String sourceMapQry ="{    \"query\": {     \"bool\": {       \"must\": { \"term\":{\"project.keyword\": \""+project+"\"}        }      }    }}";
		return esUtils.executeESsearchQry(ProductTriageConstants.POST, sourceMapUrl, requestBody);

	}

	@Override
	public JSONArray getSimilarIssues(String searchText, String inputJson,String project) {
		TriageConfiguration triageConfiguration;
		JSONArray sourceFields = new JSONArray();
		JSONArray symFields = new JSONArray();
		JSONArray rootCauseFields = new JSONArray();
		List<String> symList = new ArrayList<>();
		List<String> rcList = new ArrayList<>();
		JSONArray similarIssueArr = new JSONArray();
		JSONArray symtomsArr = new JSONArray();
		try {
			JSONArray sourceMapResults = getSourceMap(project);
			for (int i = 0; i < sourceMapResults.length(); i++) {
				JSONObject srcObj = sourceMapResults.getJSONObject(i);
				String sourceName = srcObj.getString(ProductTriageConstants.SOURCE_NAME);
				JSONArray srcFieldsArray = srcObj.getJSONArray(ProductTriageConstants.SOURCE_FIELDS);
				switch (sourceName) {
				case ProductTriageConstants.PRODATTRIBUTES:
					sourceFields = srcFieldsArray;
					break;
				case ProductTriageConstants.SYMPTOMS:
					symFields = srcFieldsArray;
					break;
				case ProductTriageConstants.ROOTCAUSE:
					rootCauseFields = srcFieldsArray;
					break;
				default:
					srcObj.getJSONArray(ProductTriageConstants.DEFAULT);
					break;
				}
			}
			symFields.forEach(symArrayItem -> symList.add(symArrayItem.toString()));
			rootCauseFields.forEach(rcArrayitem -> rcList.add(rcArrayitem.toString()));
			//symList.addAll(rcList);//code remove as request for Suresh testing. this will Remove resolution from the list.
			JSONObject inputObj = new JSONObject(inputJson);
			Set<String> inputKeys = inputObj.keySet();
			symtomsArr = traverseSourceForSymptomsArr(inputKeys, inputObj);
			JSONArray fields = new JSONArray();
			symList.forEach(listItem -> fields.put(listItem));
			String requestUrl ="/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.SEARCH;
			JSONArray filterTermsArr = new JSONArray();
			if(!inputObj.keySet().isEmpty())
			{ 
				for (String key : inputKeys) {
					//if(key.equals("product")|| key.equals("model") || key.equals("yom")) {
						 if(!key.equals(ProductTriageConstants.YES)) {
						JSONObject jsonObject = new JSONObject(); 
						jsonObject.put("terms", new JSONObject().put(key + ProductTriageConstants.KEYWORD,inputObj.get(key))); 
						filterTermsArr.put(jsonObject);
					}
				} 
			}
			filterTermsArr.put(new JSONObject().put("term", new JSONObject().put("project" + ProductTriageConstants.KEYWORD, project)));
			String requestBody = null;
			if (inputKeys.contains(ProductTriageConstants.YES) || inputKeys.contains(ProductTriageConstants.NO)
					|| inputKeys.contains(ProductTriageConstants.SKIP)) {
				triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.SIMILARISSUEQUERY_YES_NO); 
				requestBody = String.format(triageConfiguration.getValue(), symtomsArr,searchText,fields,filterTermsArr);
				//requestBody="{\"query\": {\"bool\": {\"must\": { \"terms\": {\"symptoms.keyword\":"+symtomsArr+" }},\"should\": [{\"more_like_this\": {\"like\": \""+searchText+"\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"analyzer\": \"cess_analyzer\",\"fields\": "+fields+"}},{\"multi_match\": {\"fields\": "+fields+",\"query\": \""+searchText+"\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\": \""+searchText+"\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\":"+filterTermsArr+"}},\"size\": 50}";
			}else {
				triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.SIMILARISSUEQUERY); 
				requestBody = String.format(triageConfiguration.getValue(), searchText,fields,filterTermsArr);
				//requestBody="{\"query\": {\"bool\": {\"should\": [{\"more_like_this\": {\"like\": \""+searchText+"\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"analyzer\": \"cess_analyzer\",\"fields\":"+fields+" }},{\"multi_match\": {\"fields\":"+fields+",\"query\": \""+searchText+"\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\""+searchText+"\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\":"+filterTermsArr+"}},\"size\": 50}";
			}
			JSONObject requestJson = new JSONObject(requestBody);
			similarIssueArr = esUtils.executeESsearchQry(ProductTriageConstants.POST, requestUrl,
					requestJson.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return similarIssueArr;
	}

	public JSONArray traverseSourceForSymptomsArr(Set<String> inputKeys, JSONObject inputObj) {
		JSONArray symtomsArrTemp = new JSONArray();
		for (String key : inputKeys) {
			if (key.equals(ProductTriageConstants.YES) || key.equals(ProductTriageConstants.NO)
					|| key.equals(ProductTriageConstants.SKIP))
				symtomsArrTemp = inputObj.getJSONArray(key);
		}
		return symtomsArrTemp;
	}

	public JSONArray traverseSourceForTermsArr(JSONArray sourceFields, Set<String> inputKeys, JSONObject inputObj,String project) {
		JSONArray termsArrTemp = new JSONArray();
		sourceFields.forEach(srcField -> {
			for (String key : inputKeys) {
				if (srcField.toString().equalsIgnoreCase(key)) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put(ProductTriageConstants.TERM,
							new JSONObject().put(key + ProductTriageConstants.KEYWORD, inputObj.getString(key)));
					termsArrTemp.put(jsonObject);
				}
			}
		});
		if(project != null)
			termsArrTemp.put(new JSONObject().put(ProductTriageConstants.TERM,
					new JSONObject().put("project" + ProductTriageConstants.KEYWORD, project)));
		return termsArrTemp;
	}

	@Override
	public String getIssueClusters(String issueDescription, String inputJson,String project) {
		TriageConfiguration triageConfiguration;
		JSONArray sourceFields = new JSONArray();
		JSONArray symFields = new JSONArray();
		JSONArray rootCauseFields = new JSONArray();
		JSONArray termsArr;
		JSONArray symtomsArr;
		String data=null;
		try {

			JSONArray sourceMapResults = getSourceMap(project);
			for (int i = 0; i < sourceMapResults.length(); i++) {
				JSONObject srcObj = sourceMapResults.getJSONObject(i);
				String sourceName = srcObj.getString(ProductTriageConstants.SOURCE_NAME);
				JSONArray srcFieldsArray = srcObj.getJSONArray(ProductTriageConstants.SOURCE_FIELDS);
				switch (sourceName) {
				case ProductTriageConstants.PRODATTRIBUTES:
					sourceFields = srcFieldsArray;
					break;
				case ProductTriageConstants.SYMPTOMS:
					symFields = srcFieldsArray;
					break;
				case ProductTriageConstants.ROOTCAUSE:
					rootCauseFields = srcFieldsArray;
					break;
				default:
					srcObj.getJSONArray(ProductTriageConstants.DEFAULT);
					break;

				}

			}

		} catch (Exception e1) {
			log.error(e1.getMessage());
		}

		JSONObject inputObj = new JSONObject(inputJson);
		Set<String> inputKeys = inputObj.keySet();
		// TODO :Null check
		symtomsArr = traverseSourceForSymptomsArr(inputKeys, inputObj);
		termsArr = traverseSourceForTermsArr(sourceFields, inputKeys, inputObj,project);
		JSONArray filterTermsArr = new JSONArray();
		if(!inputObj.keySet().isEmpty())
		{ 
			for (String key : inputKeys) {
					 if(!key.equals(ProductTriageConstants.YES)) {
					JSONObject jsonObject = new JSONObject(); 
					jsonObject.put("terms", new JSONObject().put(key + ProductTriageConstants.KEYWORD,inputObj.get(key))); 
					filterTermsArr.put(jsonObject);
				}
			} 
		}
		filterTermsArr.put(new JSONObject().put("term", new JSONObject().put("project" + ProductTriageConstants.KEYWORD, project)));
		String requestUrl ="/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.SEARCH_WITH_CLUSTERS;
		String requestBody = null;
		// TODO : Use Stringbuffer
		if (inputKeys.contains(ProductTriageConstants.YES) || inputKeys.contains(ProductTriageConstants.NO)
				|| inputKeys.contains(ProductTriageConstants.SKIP)) {
			triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.ISSUECLUSTERSQUERY_YES_NO); 
			requestBody = String.format(triageConfiguration.getValue(), issueDescription,symtomsArr,termsArr.toString(),symFields.getString(0),rootCauseFields.getString(0),project);
		}else {
			triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.ISSUECLUSTERSQUERY); 
			requestBody = String.format(triageConfiguration.getValue(), issueDescription,termsArr.toString(),symFields.getString(0),rootCauseFields.getString(0),filterTermsArr);
			//added filter 
			//requestBody ="{\"search_request\": {\"query\":{\"bool\":{\"must\":[{\"query_string\":{\"query\":\"issue\"}}],\"filter\":"+filterTermsArr+"}},\"size\":50},\"include_hits\": \"true\",\"query_hint\": \"true\",\"field_mapping\": {\"title\": [\"_source.description\"]}}";
			//String formater query
			//requestBody="{\"search_request\": {\"query\":{\"bool\":{\"must\":[{\"query_string\":{\"query\":%1$s}}],\"filter\":%5$s}},\"size\":50},\"include_hits\": \"true\",\"query_hint\": \"true\",\"field_mapping\": {\"title\": [\"_source.%3$s\"]}}";
			//this query from suresh
			//requestBody="{\"search_request\": {\"query\": {\"query_string\": {\"query\": \"issue\"}},\"size\": 50 },\"include_hits\": \"true\",\"query_hint\": \"true\",\"field_mapping\": {\"title\": [\"_source.description\"]}}";
		}
	
		JSONObject requestJson = new JSONObject(requestBody);
		
		try {
				data = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());
				log.info("data"+data);
			
		} catch (AgentAnalyticsException | IOException e) {
			log.error(e.getMessage());
		}

		return data;
	}

	@Override
	public String getRecommendedsolutions(String issueDescription, String json) {
		JSONArray sourceFields = new JSONArray();
		JSONArray termsArr;
		JSONArray symtomsArr;
		try {

			JSONArray sourceMapResults = getSourceMap("testprj");
			for (int i = 0; i < sourceMapResults.length(); i++) {

				JSONObject srcObj = sourceMapResults.getJSONObject(i);
				String sourceName = srcObj.getString(ProductTriageConstants.SOURCE_NAME);
				JSONArray srcFieldsArray = srcObj.getJSONArray(ProductTriageConstants.SOURCE_FIELDS);

				if (sourceName.equalsIgnoreCase(ProductTriageConstants.PRODATTRIBUTES))	
					sourceFields = srcFieldsArray;
			}

		} catch (IOException  e1) {
			log.error(e1.getMessage());
		}
		JSONObject inputObj = new JSONObject(json);
		Set<String> inputKeys = inputObj.keySet();
		symtomsArr = traverseSourceForSymptomsArr(inputKeys, inputObj);
		termsArr = traverseSourceForTermsArr(sourceFields, inputKeys, inputObj,null);
		String requestUrl ="/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL + ProductTriageConstants.SEARCH;
		String requestBody = null;
		if (inputKeys.contains(ProductTriageConstants.YES) || inputKeys.contains(ProductTriageConstants.NO)
				|| inputKeys.contains(ProductTriageConstants.SKIP))
			requestBody = ESQueries.INSIGHT_RECOMMENDED_SOLN_IF_PART1 + issueDescription
			+ ESQueries.INSIGHT_RECOMMENDED_SOLN_IF_PART2 + symtomsArr
			+ ESQueries.INSIGHT_RECOMMENDED_SOLN_IF_PART3 + termsArr.toString()
			+ ESQueries.INSIGHT_RECOMMENDED_SOLN_IF_PART4;
		else
			requestBody = ESQueries.INSIGHT_RECOMMENDED_SOLN_ELSE_PART1 + issueDescription
			+ ESQueries.INSIGHT_RECOMMENDED_SOLN_ELSE_PART2 + termsArr.toString()
			+ ESQueries.INSIGHT_RECOMMENDED_SOLN_ELSE_PART3;

		JSONObject requestJson = new JSONObject(requestBody);
		String data = null;
		try {
			data = esClient.performRequest(ProductTriageConstants.POST, requestUrl, requestJson.toString());

		} catch (AgentAnalyticsException | IOException e) {
			log.error(e.getMessage());
		}

		return data;

	}

	@Override
	public List<ProductAttributes> getProductAttribute(String project) {
		List<ProductAttributesEntity> productdetails = productDetailsRepository.findByProject(project);
		return productdetails.stream()
				.collect(Collectors.mapping(productentity -> mapToPojo(productentity), Collectors.toList()));


	}

	private Insights mapToPojo(InsightsEntity entity) {
		return new Insights()
				.setIssueid(entity.getIssueid())
				.setProduct(entity.getProduct())
				.setModel(entity.getModel())
				.setRootCause(entity.getRootCause())
				.setSymptoms(entity.getSymptoms())
				.setResolution(entity.getResolution());
	}


	private ProductAttributes mapToPojo(ProductAttributesEntity productentity) {
		ProductAttributes productAttributes = new ProductAttributes();
		productAttributes.setAttributeData(productentity.getAttributeData());
		productAttributes.setAttributeName(productentity.getAttributeName());

		return productAttributes;		
	}


}

package com.intelliservice.agentanalytics.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelliservice.agentanalytics.dao.GenericCollectionDao;
import com.intelliservice.agentanalytics.dao.ProductDetailsDao;
import com.intelliservice.agentanalytics.dao.ProductTriageDao;
import com.intelliservice.agentanalytics.dao.SourceMapDao;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.ProductAttributesSet;
import com.intelliservice.agentanalytics.model.RootcauseDetails;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
import com.intelliservice.agentanalytics.model.Symptoms;
import com.intelliservice.agentanalytics.model.SymptomsDetails;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.utils.DataNotFoundException;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Service
public class ProductDetailsServiceImpl implements ProductDetailsService {
	private static final Logger log = LoggerFactory.getLogger(ProductDetailsServiceImpl.class);

	@Autowired
	ProductDetailsDao productDetailsDao;
	
	@Autowired
	ProductTriageDao productTriageDao;
	
	@Autowired
	SourceMapDao sourceMapDao;
	
	Map<String, Map<String, Set<String>>> resultMap = new HashMap<>();
    @Autowired
	GenericCollectionDao genericCollectionDao;
	
	@Override
	public ProductAttributes saveProductDetails(ProductAttributes productAttributes) {
		log.debug("Enter to saveProductDetails");
		return productDetailsDao.saveProductDetails(productAttributes);
	}

	@Override
	public List<ProductAttributes> getProductDetails() {
		log.debug("Enter to getProductDetails");		
		return productDetailsDao.getProductDetails();
	}
	
	
	@Override
	public List<Map<String, Set<String>>> getProductTriage(String collectionName,String inputJson,String rootaggs,String project) throws IOException {
		JSONObject inputObj = new JSONObject(inputJson);
		List<Map<String, Set<String>>> resultList = new ArrayList<>();		
		resultMap = new HashMap<>();
		SourceMap sourceMap = new SourceMap();
		
		try {
			if(inputObj.keySet().isEmpty())
				inputJson = ProductTriageConstants.EMPTY_JSON;
			//Get all Symptom root cause Mapping data
			List<SymptomRootcauseMapping> symptomRootcauseList = productDetailsDao.getAllSymptomRootcauseMapping(project);		
			List<SourceMap> sourceMapModel  = sourceMapDao.getSourcesMap(project);
			sourceMap = sourceMapModel.stream().filter(srcName-> srcName.getSourceName().contains(ProductTriageConstants.PRODUCT)).findAny().orElse(null); 
			String resultString = productTriageDao.getProductTriage(collectionName,inputJson,sourceMap,rootaggs,project);
			
			JSONObject resultJson = new JSONObject(resultString);
			JSONObject aggregations = (JSONObject) resultJson.get(ProductTriageConstants.AGGREGATIONS);
			JSONObject symptoms = (JSONObject) aggregations.get(rootaggs);
			JSONArray buckets = (JSONArray) symptoms.get(ProductTriageConstants.BUCKETS);
			int jsonSize = buckets.length();
			
			while (jsonSize > 0) {
				jsonSize--;
				JSONObject jsonObject = (JSONObject) buckets.get(jsonSize);
				key = String.valueOf(jsonObject.get(ProductTriageConstants.KEY));				
				setData(sourceMap.getSourceFields(),jsonObject,0);
			}
			
			for(Map.Entry<String, Map<String, Set<String>>> resultMapEntry : resultMap.entrySet()) {
				Map symptomsDataMap = new HashMap();
				String symptomsData = resultMapEntry.getKey();
				symptomsDataMap.put(rootaggs, resultMapEntry.getKey());
				
	                for (Map.Entry<String,Set<String>> entryChild : resultMapEntry.getValue().entrySet()) {
	                	symptomsDataMap.put(entryChild.getKey(), entryChild.getValue());
	                }
	                
	                
	                for(SymptomRootcauseMapping src : symptomRootcauseList) {	                	
	                	if(symptomsData.equalsIgnoreCase(src.getSymptoms()) && !symptomsDataMap.containsKey(ProductTriageConstants.LEADINGQUESTION ))
	                		symptomsDataMap.put(ProductTriageConstants.LEADINGQUESTION , src.getLeadingquestion());	                	
	                	
	                	if(symptomsData.equalsIgnoreCase(src.getRootcause()) && !symptomsDataMap.containsKey(ProductTriageConstants.ROOTCAUSETITLE ))
	                		symptomsDataMap.put(ProductTriageConstants.ROOTCAUSETITLE , src.getRcTitle());
	                	
	                } 
				resultList.add(symptomsDataMap);
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return resultList;
	}
	
	//TODO: Why recursive ? Sonar Qube - Cognitive Complexity
	String key;
	public void setData(List<String> sourceFields, JSONObject jsonObject,int index) {
		int size = sourceFields.size();
		Map<String, Set<String>> jsomMap = new HashMap<>();
		
		
		if(index < size) {
			
			String fieldValue = sourceFields.get(index);
			jsomMap = resultMap.containsKey(key) ? resultMap.get(key) : jsomMap;			
			
			JSONObject products = (JSONObject) jsonObject.get(fieldValue);
			JSONArray productbuckets = (JSONArray) products.get(ProductTriageConstants.BUCKETS);
			int productSize = productbuckets.length();
			index++;
			
			while(productSize >0) {
				productSize--;
				JSONObject productObject = (JSONObject) productbuckets.get(productSize);
				String productKey = String.valueOf(productObject.get(ProductTriageConstants.KEY));
								
				Set<String> jsonSet = jsomMap.containsKey(fieldValue) ?  jsomMap.get(fieldValue) : new HashSet<>();					
				jsonSet.add(productKey);
				
				jsomMap.put(fieldValue, jsonSet);
				resultMap.put(key, jsomMap);
				setData(sourceFields, productObject,index); 
			}
		}		
	}
	
	@Override
	public List<SourceMap> saveSourcesMap(List<SourceMap> sourceMap) throws DataNotFoundException {
		return sourceMapDao.saveSourcesMap(sourceMap);
	}

	@Override
	public List<SourceMap> getSourcesMap(String project) {
		return sourceMapDao.getSourcesMap(project);
	}

	@Override
	public String collectionReindex(String sourceCollection, String destinationCollection,String project) throws IOException {
		log.debug("Enter to collectionReindex");
		String apiurl="/product-intelligent-triage/settings/reindex";
		String status="Data preparation is in progress";
		String status1="Data preparation is completed";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		productDetailsDao.monitoringLog(apiurl,status,project,timestamp);
		String responceString = sourceMapDao.collectionReindex(sourceCollection,destinationCollection,project);
		JSONObject jsonObject = new JSONObject(responceString);
		
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		productDetailsDao.monitoringLog(apiurl,status1,project,timestamp1);
		
		return "Created : "+jsonObject.get("created")+", Updated : "+jsonObject.get("updated")+", Deleted : "+jsonObject.get("deleted");
	}

	@Override
	public List<SymptomRootcauseMapping> symptomRootcauseMapping(String collectionId,String project) throws IOException {
		log.debug("Enter to symptomRootcauseMapping");
		String apiurl="/product-intelligent-triage/settings/symptomsrootcausemap/{collection}/project/${projectName}";
		String status="symptom resolutions mapping model is in progress";
		String status1="symptom resolutions mapping model is completed";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		productDetailsDao.monitoringLog(apiurl,status,project,timestamp);
		String resultString = productDetailsDao.symptomRootcauseMapping(collectionId,project);
		//AKS
		log.info("Ajit--resultString---->"+resultString);
		SourceMap sourceMap = new SourceMap();
		List<SourceMap> sourceMapMode  = sourceMapDao.getSourcesMap(project);
		sourceMap = sourceMapMode.stream().filter(srcName-> srcName.getSourceName().contains(ProductTriageConstants.PRODUCT)).findAny().orElse(null); 
		//AKS
		List<SymptomRootcauseMapping> symptomRootcauseList = new ArrayList<>();
		List<SymptomRootcauseMapping> returnSymptomRootcauseList = null;
		SymptomRootcauseMapping symptomRootcause = null;
		JSONObject resultJson = new JSONObject(resultString);
		JSONObject aggregations = (JSONObject) resultJson.get(ProductTriageConstants.AGGREGATIONS);
		JSONObject symptoms = (JSONObject) aggregations.get(ProductTriageConstants.SYMPTOMS);
		JSONArray symptomsBuckets = (JSONArray) symptoms.get(ProductTriageConstants.BUCKETS);
		int jsonSize = symptomsBuckets.length();
		int num=0;
		while(jsonSize > 0 ) {
			jsonSize--;
			JSONObject symptomJson = (JSONObject) symptomsBuckets.get(jsonSize);
			String symptomskey = String.valueOf(symptomJson.get(ProductTriageConstants.KEY));
			JSONObject rootcausJson = (JSONObject) symptomJson.get(ProductTriageConstants.ROOTCAUSE);
			JSONArray rootcauseBuckets = (JSONArray) rootcausJson.get(ProductTriageConstants.BUCKETS);
			int rootcauseSize = rootcauseBuckets.length();
			while(rootcauseSize > 0) {
				rootcauseSize--;
				JSONObject rootcauseJson = (JSONObject) rootcauseBuckets.get(rootcauseSize);
				String rootcausekey = String.valueOf(rootcauseJson.get(ProductTriageConstants.KEY));
				String relevancy = String.valueOf(rootcauseJson.get(ProductTriageConstants.DOC_COUNT));
				num=Math.max(Integer.parseInt(relevancy),num);
				double percentage=Integer.parseInt(relevancy)*100/num;
				symptomRootcause = new SymptomRootcauseMapping();
					symptomRootcause.setSymptoms(symptomskey);
					symptomRootcause.setRootcause(rootcausekey);
					symptomRootcause.setLeadingquestion(symptomskey+ProductTriageConstants.QUESTION);
					symptomRootcause.setRelevancy(String.valueOf(percentage));
					symptomRootcause.setRcTitle(rootcausekey+ProductTriageConstants.TITLE);
					symptomRootcause.setStatus(ProductTriageConstants.DEFAULTREVIEW);
					symptomRootcause.setSymTitle(symptomskey+ProductTriageConstants.TITLE);
					symptomRootcause.setProject(project);
				symptomRootcauseList.add(symptomRootcause);
			}
		}
		//AKS
		//Get all product attribute 
		String productattributesString = productDetailsDao.getProductattributes(sourceMap,project,collectionId);
		setData(productattributesString,symptomRootcauseList,sourceMap);
		updateListWithProductAttribute(symptomRootcauseList);
		for(SymptomRootcauseMapping symptomRootcauseMapping : symptomRootcauseList ) {
			Map<String,String> resolutionAndDescriptionMap = productDetailsDao.getResolutionAndDescription(symptomRootcauseMapping,project,collectionId);
			updateList(resolutionAndDescriptionMap,symptomRootcauseMapping);
		}
		
		//AKS
		productDetailsDao.deleteAllSymptomRootcauseMapping(project);
		returnSymptomRootcauseList = productDetailsDao.saveSymptomRootcauseMapping(symptomRootcauseList);	
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		productDetailsDao.monitoringLog(apiurl,status1,project,timestamp1);
	return returnSymptomRootcauseList;
	}
	
	@Override
	public String updateSymptoms(Symptoms symptomsModal) {  
		return productDetailsDao.updateSymptoms(symptomsModal);
	}
	@Override
	public String deleteSymptoms(String inputJson) {
		return productDetailsDao.deleteSymptoms(inputJson);
	}
	
	@Override
	public String createSymptoms(Insights insightsModal) {
		return productDetailsDao.createSymptoms(insightsModal);
	}

	@Override
	public String createSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping) {
		return productDetailsDao.createSymptomsrootcause(symptomRootcauseMapping);
	}

	@Override
	public String updateSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping) {
		return productDetailsDao.updateSymptomsrootcause(symptomRootcauseMapping);
	}

	@Override
	public String deleteSymptomsrootcause(String inputJson) {
		return productDetailsDao.deleteSymptomsrootcause(inputJson);
	}

	@Override
	public String updateStatusAndScore(SymptomRootcauseMapping symptomRootcauseMapping) {
		return productDetailsDao.updateStatusAndScore(symptomRootcauseMapping);
	}
	
	@Override
	public String createAttributes(String project) {
		return productDetailsDao.createAttributes(project);
	}

	
	@Override
	public String getSymptomsList(String project,String json) {
		JSONObject inputObj = new JSONObject(json);
		String symptoms = "";
		try {
			if(inputObj.keySet().isEmpty())
				json = ProductTriageConstants.EMPTY_JSON;
			symptoms = productDetailsDao.getSymtomsRootcauseList(project,json);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return symptoms;
	}
	
	
	@Override
	public String getRootcauseList(String project,String json) {
		JSONObject inputObj = new JSONObject(json);
		String rootCause = "";
		try {
			if(inputObj.keySet().isEmpty())
				json = ProductTriageConstants.EMPTY_JSON;
			rootCause = productDetailsDao.getRootcauseSymtomsList(project,json);
		} catch (Exception e) {
			log.error(e.getMessage());			
		}
		return rootCause;
	}
	

	@Override
	public String updateRootcause(SymptomRootcauseMapping symptomRootcauseMapping) {
		return productDetailsDao.updateRootcause(symptomRootcauseMapping);
	}

	
	@Override
	public String deleteRootcause(String inputJson) {
		return productDetailsDao.deleteRootcause(inputJson);
	}

	@Override
	public String populateRecommendedSolutions(String input,String project) throws JsonProcessingException {
		List<SymptomRootcauseMapping> symptomRootcauseMappingList = new ArrayList<>();
		final ObjectMapper objectMapper = new ObjectMapper();
		String responseRes = productDetailsDao.populateRecommendedSolutions(input,project);
		String response = responseRes.replaceAll("relevency", "relevancy");
		if (null != response) {
			JSONObject resultJson = new JSONObject(response);
			if (resultJson.has("hits")) {
				JSONArray results = resultJson.getJSONObject("hits").getJSONArray("hits");
				for (int result = 0; result < results.length(); result++) {
					JSONObject src = results.getJSONObject(result).getJSONObject("_source");
					SymptomRootcauseMapping symptomRootcauseMapping = objectMapper.readValue(src.toString(), SymptomRootcauseMapping.class);
					symptomRootcauseMappingList.add(symptomRootcauseMapping);	
				}
			}
		}
		Map<String, List<SymptomRootcauseMapping>> groupedMap = symptomRootcauseMappingList.stream().collect(Collectors.groupingBy(SymptomRootcauseMapping::getRootcause));
		return getTopRootCauses(groupedMap).toString(); 
	}

	private JSONArray getTopRootCauses(Map<String, List<SymptomRootcauseMapping>> groupedMap) {
		Integer count = 0;
		Integer maxVal =0;
		JSONArray arrayData = new JSONArray();
		String regex = "[0-9]+";
		Pattern p = Pattern.compile(regex);
		Map<String,Integer> countMap = new HashMap<>();
		SymptomRootcauseMapping symptomRootcauseMapping;
		for(Entry<String, List<SymptomRootcauseMapping>> entry : groupedMap.entrySet()) {
			count = 0;
			for(SymptomRootcauseMapping symRootMapping : entry.getValue() ) {
				if(symRootMapping.getRelevancy() != null && p.matcher(symRootMapping.getRelevancy()).matches() )
					count += Integer.parseInt(symRootMapping.getRelevancy());
				else
					count = 0;
			}
			if (maxVal < count)
				maxVal = count;
			countMap.put(entry.getKey(), count);
		}
		final Map<String, Integer> sortedByValue = countMap.entrySet()
				.stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.ROOTCAUSE_THRESHOLD);
		for(Entry<String,Integer> entry: sortedByValue.entrySet()) {
			symptomRootcauseMapping = groupedMap.get(entry.getKey()).get(0);
			if(maxVal == 0) {
				count = entry.getValue();
				JSONObject jsonData = new JSONObject();
				jsonData.put(ProductTriageConstants.ROOTCAUSE, symptomRootcauseMapping.getRootcause());
				jsonData.put(ProductTriageConstants.ROOTCAUSETITLE, symptomRootcauseMapping.getRcTitle());
				jsonData.put(ProductTriageConstants.RELEVANCY, entry.getValue());
				arrayData.put(jsonData);
			//}else if((count - entry.getValue()) <= Integer.parseInt(triageConfiguration.getValue().trim())) {
			}else if((100 - (entry.getValue().floatValue()/maxVal.floatValue())*100) <= Integer.parseInt(triageConfiguration.getValue().trim())) {
				JSONObject jsonData = new JSONObject();
				jsonData.put(ProductTriageConstants.ROOTCAUSE, symptomRootcauseMapping.getRootcause());
				jsonData.put(ProductTriageConstants.ROOTCAUSETITLE, symptomRootcauseMapping.getRcTitle());
				//jsonData.put(ProductTriageConstants.RELEVANCY, entry.getValue());
				float b = entry.getValue().floatValue()/maxVal.floatValue();
				jsonData.put(ProductTriageConstants.RELEVANCY, Math.round(b*90)+"%");
				arrayData.put(jsonData);
			}

		}

		return arrayData;
	}
	
	
	@Override
	public String addSymptoms(SymptomsDetails symptomsDetailsModal) {
		return productDetailsDao.addSymptoms(symptomsDetailsModal);
	
	}
	
	@Override
	public String getSimilarSymptomsSearch(String symptoms,String project) {
		try {
			symptoms = productDetailsDao.getSimilarSymptomsSearch(symptoms,project);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return symptoms;
	}

	@Override
	public String getSimilarRootCauseSearch(String rootcause,String project) {
		try {
			rootcause = productDetailsDao.getSimilarRootCauseSearch(rootcause,project);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return rootcause;
	}

	@Override
	public String addRootcause(RootcauseDetails rootcauseDetailsModal) {
		return productDetailsDao.addRootcause(rootcauseDetailsModal);
	}
	@Override
	public String saveTriageSummary(String inputJson) throws IOException {

		return productTriageDao.saveTriageSummary(inputJson);
	}
	@Override
	public String symptomsUnmap(String inputJson) {
		return productDetailsDao.symptomsUnmap(inputJson);
	}

	@Override
	public String rootcauseUnmap(String inputJson) {
		return productDetailsDao.rootcauseUnmap(inputJson);
	}

	@Override
	public String getSymptomsdetails(String symptoms,String project) {
		try {
			symptoms = productDetailsDao.getSymptomsdetails(symptoms,project);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return symptoms;
	}
	
	@Override
	public String getSymptomsdetailsSet(String project,String json) {
		String symptoms=null;
		try {
			 symptoms = productDetailsDao.getSymptomsdetailsSet(project,json);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return symptoms;
	}
	
	private void setData(String productattributesString, List<SymptomRootcauseMapping> symptomRootcauseList,SourceMap sourceMap) {
		resultDataMap = new HashMap<>();
		try {
			int attributesSize = sourceMap.getSourceFields().size();
			JSONObject resultJson = new JSONObject(productattributesString);
			JSONObject aggregations = (JSONObject) resultJson.get(ProductTriageConstants.AGGREGATIONS);
			JSONObject symptoms = (JSONObject) aggregations.get(ProductTriageConstants.SYMPTOMS);
			JSONArray buckets = (JSONArray) symptoms.get(ProductTriageConstants.BUCKETS);
			int jsonSize = buckets.length();
			
			for(int i = 0; jsonSize > i ; i++) {
				JSONObject jsonObject = (JSONObject) buckets.get(i);
				primaryKey = String.valueOf(jsonObject.get(ProductTriageConstants.KEY));	
				setAttributesData(sourceMap.getSourceFields(),jsonObject,0);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}

	Map<String,Map<String,Set<String>>> resultDataMap = new HashMap<>();
	String primaryKey;
	public void setAttributesData(List<String> sourceFields, JSONObject jsonObject,int index) {
		int size = sourceFields.size();
		Map<String, Set<String>> jsomMap = new HashMap<>();
		
		
		if(index < size) {
			
			String fieldValue = sourceFields.get(index);
			jsomMap = resultDataMap.containsKey(primaryKey) ? resultDataMap.get(primaryKey) : jsomMap;			
			
			JSONObject products = (JSONObject) jsonObject.get(fieldValue);
			JSONArray productbuckets = (JSONArray) products.get(ProductTriageConstants.BUCKETS);
			int productSize = productbuckets.length();
			index++;
			
			while(productSize >0) {
				productSize--;
				JSONObject productObject = (JSONObject) productbuckets.get(productSize);
				String productKey = String.valueOf(productObject.get(ProductTriageConstants.KEY));
								
				Set<String> jsonSet = jsomMap.containsKey(fieldValue) ?  jsomMap.get(fieldValue) : new HashSet<>();					
				jsonSet.add(productKey);
				
				jsomMap.put(fieldValue, jsonSet);
				resultDataMap.put(primaryKey, jsomMap);
				setAttributesData(sourceFields, productObject,index); 
			}
		}		
	}
	
	private void updateListWithProductAttribute(List<SymptomRootcauseMapping> symptomRootcauseList) {
		List<ProductAttributesSet> list ;
		ProductAttributesSet productAttributesSet;
		Map<String,Set<String>> map;
		for(SymptomRootcauseMapping mapping : symptomRootcauseList) {
			map = resultDataMap.get(mapping.getSymptoms());
			list = new ArrayList<>();
			for(Entry<String, Set<String>> set : map.entrySet()) {
				productAttributesSet = new ProductAttributesSet();
				productAttributesSet.setAttribute_name(set.getKey());
				productAttributesSet.setAttribute_data(new ArrayList<>(set.getValue()));
				list.add(productAttributesSet);
			}
			mapping.setProductAttributes(list);
		}
	}
	private void updateList(Map<String, String> resolutionAndDescriptionMap,SymptomRootcauseMapping symptomRootcause) {
		try {
			JSONObject resolutionJson = new JSONObject(resolutionAndDescriptionMap.get("resolutionResult"));
			JSONObject descriptionJson = new JSONObject(resolutionAndDescriptionMap.get("descriptionResult"));
			JSONObject resolutionAggregations = (JSONObject) resolutionJson.get(ProductTriageConstants.AGGREGATIONS);
			JSONObject resolution = (JSONObject) resolutionAggregations.get("my-agg-name");
			JSONArray resolutionBuckets = (JSONArray) resolution.get(ProductTriageConstants.BUCKETS);
			int resolutionSize = resolutionBuckets.length();
			while(resolutionSize > 0) {
				resolutionSize--;
				JSONObject json = (JSONObject) resolutionBuckets.get(resolutionSize);
				symptomRootcause.addResolution(String.valueOf(json.get(ProductTriageConstants.KEY)));
			}
			
			JSONObject descriptionAggregations = (JSONObject) descriptionJson.get(ProductTriageConstants.AGGREGATIONS);
			JSONObject description = (JSONObject) descriptionAggregations.get("my-agg-name");
			JSONArray descriptionBuckets = (JSONArray) description.get(ProductTriageConstants.BUCKETS);
			int descriptionSize = descriptionBuckets.length();
			while(descriptionSize > 0) {
				descriptionSize--;
				JSONObject json = (JSONObject) descriptionBuckets.get(descriptionSize);
				symptomRootcause.addDescription(String.valueOf(json.get(ProductTriageConstants.KEY)));
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}
	
	@Override
		public String getWizardFlowStatus(String project) throws IOException {		
			return productDetailsDao.getWizardFlowStatus(project);
		}

	@Override
	public String getRootcausedetailsSet(String project, String inpJson) {
		String rootcause=null;
		try {
			rootcause = productDetailsDao.getRootcausedetailsSet(project,inpJson);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return rootcause;
	}

	@Override
	public String getRootcausedetails(String rootcause, String project) {
		try {
			rootcause = productDetailsDao.getRootcausedetails(rootcause,project);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return rootcause;
	}
	@Override
	public String getSymptom(String project, String issue, String inpJson) {
		String symptoms=null;
		try {
			symptoms = productDetailsDao.getSymptom(project,issue,inpJson);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return symptoms;
	}

	@Override
	public String updatequestions(String project) throws IOException {
		return productDetailsDao.updatequestions(project);
	}
	
	
	
}

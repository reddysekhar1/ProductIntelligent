package com.intelliservice.agentanalytics.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intelliservice.agentanalytics.dao.GenericCollectionDao;
import com.intelliservice.agentanalytics.dao.InsightsDao;
import com.intelliservice.agentanalytics.dao.ProductDetailsDao;
import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;

@Service
public class InsightsServiceImpl implements InsightsService {
	private static final Logger log = LoggerFactory.getLogger(InsightsServiceImpl.class);
	@Autowired
	InsightsDao insightsDao;
	
	@Autowired
	ProductDetailsDao productDetailsDao;
	
	@Autowired
	GenericCollectionDao genericCollectionDao;

	@Override
	public List<Insights> getSimilarIssues(String issueDescription) {
		return insightsDao.getSimilarIssues(issueDescription);
	}
	/*@Override
	public String getSimilarIssues(String issueDescription, String product, String model) {
		JSONObject jsonObj = new JSONObject();
		List<String> symptomsList = new ArrayList<>();		
		try {
			List<Insights> similarIssuesList = insightsDao.getSimilarIssues(issueDescription,product,model);			
			similarIssuesList.forEach(insight -> symptomsList.addAll(insight.getSymptoms()));			
			LinkedHashSet<String> symptomsHashSet = new LinkedHashSet<>(symptomsList);        
			ArrayList<String> symtomsListWithoutDuplicates = new ArrayList<>(symptomsHashSet);
			
			jsonObj.put(ProductTriageConstants.RESPONSE_DATA, similarIssuesList);
			jsonObj.put(ProductTriageConstants.SYMPTOMS, symtomsListWithoutDuplicates);			
				
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
	return jsonObj.toString();
	}*/
	
	@Override
	public String getSimilarIssues(String issueDescription, String inputJson,String project) {
		JSONObject inputObj = new JSONObject(inputJson);
		List<String> symptomsList = new ArrayList<>();
		JSONObject jsonObj = new JSONObject();
		try {			
			if(inputObj.keySet().isEmpty())
				inputJson = ProductTriageConstants.EMPTY_JSON;		
			JSONArray similarIssuesArray = insightsDao.getSimilarIssues(issueDescription, inputJson, project);	
					
			/*
			 * for (int i=0; i < similarIssuesArray.length(); i++) { JSONArray arr1 =
			 * similarIssuesArray.getJSONObject(i).getJSONArray(ProductTriageConstants.
			 * SYMPTOMS); List<String> symtomsArr = IntStream.range(0, arr1.length())
			 * .mapToObj(arr1::get) .map(Object::toString) .collect(Collectors.toList());
			 * symptomsList.addAll(symtomsArr); } Collection<Map<String, String>> data =
			 * mapQuestionToSymptoms(symptomsList,project);
			 */			
			Collection<Map<String, String>> data = new ArrayList<>();
			jsonObj.put(ProductTriageConstants.RESPONSE_DATA, similarIssuesArray);
			jsonObj.put(ProductTriageConstants.SYMPTOMS, data);
			
		} catch (IOException | ParseException e) {
			log.error(e.getMessage());
		}
		
		return jsonObj.toString();
	}
	

	@Override
	public List<ProductAttributes> getProductAttribute(String project) {
		return insightsDao.getProductAttribute(project);
	}
	
	public JSONArray getParsedJSON(JSONObject resultJson) {
		JSONArray responseList = new JSONArray();
		resultJson.getJSONObject(ProductTriageConstants.HITS).getJSONArray(ProductTriageConstants.HITS).forEach(resultItem -> {
			JSONObject jsonObj = (JSONObject) resultItem;
			JSONObject sourceJSON = jsonObj.getJSONObject(ProductTriageConstants.SOURCE);	
			sourceJSON.put(ProductTriageConstants.ID, jsonObj.getString("_"+ProductTriageConstants.ID));
			sourceJSON.put(ProductTriageConstants.SCORE, jsonObj.getDouble("_"+ProductTriageConstants.SCORE));
			responseList.put(sourceJSON);
		});		
		
		return responseList;
		
	}
	
	public JSONArray getClusters(JSONArray clusters,JSONArray responseList) {		
		for (int cluster= 0; cluster < clusters.length(); cluster++) {
			JSONArray docsArray = new JSONArray();
			JSONArray documents = clusters.getJSONObject(cluster).getJSONArray(ProductTriageConstants.DOCUMENT);		
			documents.forEach(docItem -> {
				responseList.forEach(jsonObj -> {
					JSONObject resultItem = (JSONObject) jsonObj;
					if (resultItem.getString(ProductTriageConstants.ID).trim().equalsIgnoreCase(docItem.toString().trim()))
						docsArray.put(resultItem);
					});
			});			
			clusters.getJSONObject(cluster).put(ProductTriageConstants.DOCUMENT, docsArray);
		}		
	 return clusters;
	}

	@Override
	public JSONArray getIssueClusters(String issueDescription,String json,String project) {		
		JSONObject inputObj = new JSONObject(json);
		JSONArray responseList = new JSONArray();
		JSONArray clusters= new JSONArray();
		JSONArray clustersResult= new JSONArray();
		try {
			
			if(inputObj.keySet().isEmpty())
				json = ProductTriageConstants.EMPTY_JSON;			
			String data = insightsDao.getIssueClusters(issueDescription,json,project);
			if (data != null) {
				JSONObject resultJson = new JSONObject(data);				
				responseList = resultJson.has(ProductTriageConstants.HITS) ? getParsedJSON(resultJson) : responseList;
				clusters = resultJson.has(ProductTriageConstants.CLUSTERS) ? getClusters(resultJson.getJSONArray(ProductTriageConstants.CLUSTERS),responseList) : clusters;		
				for (int result = 0; result < clusters.length(); result++) {
					JSONObject src = clusters.getJSONObject(result);
					String str=src.getString("label");
					String[] split = str.split( " " );
					if(split.length!=1)
					{
						clustersResult.put(src);
					}
				}
			}			
	
		} catch(Exception e) {
			json = ProductTriageConstants.EMPTY_JSON;
			log.error(e.getMessage());
	    }
		return clustersResult;
	}
	
	
	@Override
	public String getRecommendedsolutions(String issueDescription, String json) {
		try {
			JSONObject inputObj = new JSONObject(json);
			if(inputObj.keySet().isEmpty())
				json = ProductTriageConstants.EMPTY_JSON;
		} catch(Exception e) {
				json = ProductTriageConstants.EMPTY_JSON;
			log.error(e.getMessage());
		}	
	
	return insightsDao.getRecommendedsolutions(issueDescription,json);
	}
	/**
	 * @author ajitkumar.sahoo
	 * @param symptomsList
	 * This method will fetch the data from symptomrootcausemapping collection and map the symptoms with question.
	 * @return 
	 */
	private Collection<Map<String, String>> mapQuestionToSymptoms(List<String> symptomsList,String project) {
		Map<String,Map<String,String>> symptomQuestionMap = new LinkedHashMap<>();
		Set<String> hashSet = new LinkedHashSet<>();
		Map<String, Long> symptomsCounts = new LinkedHashMap<>();
		if(symptomsList != null && !symptomsList.isEmpty()) {
			symptomsCounts = symptomsList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			hashSet = new LinkedHashSet<>(symptomsList);
		}
		//Get all Symptom root cause Mapping data
		List<SymptomRootcauseMapping> symptomRootcauseList = productDetailsDao.getAllSymptomRootcauseMapping(project);
		for(String symptomsKey: hashSet) {
			for(SymptomRootcauseMapping mapping : symptomRootcauseList) {
				if(symptomsKey.equalsIgnoreCase(mapping.getSymptoms())) {
					symptomQuestionMap.computeIfAbsent(symptomsKey, k -> updateMap(symptomsKey,mapping));
				}
			}
		}
		return updateScoreOnSymptoms(symptomsCounts,symptomQuestionMap);
	}
	
	
	/*
	 *This function will sort the Json Array by score.
	 */
	private List<JSONObject> sortJsonArray(JSONArray arr) {
		List<JSONObject> list = new ArrayList<>();
		arr.forEach(obj -> list.add(new JSONObject(obj.toString())));	
		list.sort((JSONObject obj1, JSONObject obj2) -> obj1.getDouble(ProductTriageConstants.SCORE) - (obj2.getDouble(ProductTriageConstants.SCORE)) > 0 ? 1:0);
		return list;
	}

	/*
	 *  This function will update the score field to percentage.
	 */
	private void updateScoreToPercentage(List<JSONObject> jsonList) {
		Double maxScore = jsonList.get(0).getDouble(ProductTriageConstants.SCORE);
		for(JSONObject obj : jsonList) {
			Double score = obj.getDouble(ProductTriageConstants.SCORE);
			Double per = (score/maxScore) * 100;
			obj.put(ProductTriageConstants.SCORE, per);
			
		}
	}
	/*
	 * This function will check the Threshold. if difference between two objetc more that Threshold number then remove from list
	 * 
	 */
	private List<JSONObject> checkThresholdCount(List<JSONObject> jsonList) {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.THRESHOLD);
		List<JSONObject> returnList = new ArrayList<>();
		Double maxScore = jsonList.get(0).getDouble("score");
		for(JSONObject obj : jsonList) {
			Double score = obj.getDouble("score");
			if(maxScore - score <= Integer.parseInt(triageConfiguration.getValue().trim())) {
				returnList.add(obj);
			}else {
				break;
			}
			maxScore = score;
			
		}
		return returnList;
	}
	/*
	 * This function will update the symptomQuestionMap MAP
	 */
	public Map<String,String> updateMap(String symptomsKey, SymptomRootcauseMapping mapping) {
		Map<String, String> dataMap = new LinkedHashMap<>();
		dataMap.put(ProductTriageConstants.SYMPTOMS, symptomsKey);
		dataMap.put(ProductTriageConstants.LEADINGQUESTION, mapping.getLeadingquestion());
		dataMap.put(ProductTriageConstants.SCORE, "0");
		return dataMap;
	}
	/*
	 * This function will update the score on Symptoms list 
	 */
	private Collection<Map<String, String>> updateScoreOnSymptoms(Map<String, Long> symptomsCounts, Map<String, Map<String, String>> symptomQuestionMap) {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.SYMPTOMS_THRESHOLD);
		List<Map<String, String>> returnMapList = new ArrayList<>();
		int count = 0;
		for(Map<String, String> map : symptomQuestionMap.values() ) {
			map.put(ProductTriageConstants.SCORE, String.valueOf(symptomsCounts.get(map.get(ProductTriageConstants.SYMPTOMS))));
		}
		// Create a list from elements of HashMap for sorting
        List<Map.Entry<String, Map<String, String>>> list =new LinkedList<>(symptomQuestionMap.entrySet());
        list.sort((Entry<String, Map<String, String>> o1, Entry<String, Map<String, String>> o2)
        		->Integer.parseInt(String.valueOf(o2.getValue().get(ProductTriageConstants.SCORE))) 
        		- (Integer.parseInt(String.valueOf(o1.getValue().get(ProductTriageConstants.SCORE)))));
		for(Map.Entry<String, Map<String, String>> map : list) {
			
			int current = Integer.parseInt(map.getValue().get(ProductTriageConstants.SCORE));
			if(count == 0) {
				count = current;
				returnMapList.add(map.getValue());
			}else {
				if((count - current) <= Integer.parseInt(triageConfiguration.getValue().trim()))
					returnMapList.add(map.getValue());
				else
					break;
			}
		}
		return returnMapList;
	}

}

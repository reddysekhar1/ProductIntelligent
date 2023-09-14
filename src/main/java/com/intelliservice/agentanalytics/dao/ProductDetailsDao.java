package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.intelliservice.agentanalytics.exception.AgentAnalyticsException;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.RootcauseDetails;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
import com.intelliservice.agentanalytics.model.Symptoms;
import com.intelliservice.agentanalytics.model.SymptomsDetails;

public interface ProductDetailsDao {
	public String getSymtomsRootcauseList(String project,String json);
	
	public String getRootcauseSymtomsList(String project,String json);
	
	public ProductAttributes saveProductDetails(ProductAttributes productDetails);

	public List<ProductAttributes> getProductDetails();

	public String symptomRootcauseMapping(String collectionId,String project) throws IOException;

	public List<SymptomRootcauseMapping> saveSymptomRootcauseMapping(List<SymptomRootcauseMapping> symptomRootcauseList);

	public void deleteAllSymptomRootcauseMapping(String project);
	
	public List<SymptomRootcauseMapping> getAllSymptomRootcauseMapping(String project);
	
	public String updateSymptoms(Symptoms symptomsModal);

	public String deleteSymptoms(String inputJson);

	public String createSymptoms(Insights insightsModal);

	public String createSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping);

	public String updateSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping);

	public String deleteSymptomsrootcause(String inputJson);

	public String updateStatusAndScore(SymptomRootcauseMapping symptomRootcauseMapping);
	
	public String createAttributes(String project);
	
	public String updateRootcause(SymptomRootcauseMapping symptomRootcauseMapping);
	
	public String deleteRootcause(String inputJson);
	
	public String populateRecommendedSolutions(String rootcauseList,String project);

	public String addSymptoms(SymptomsDetails symptomsDetailsModal);
	
	public String getSimilarSymptomsSearch(String symptoms,String project)throws AgentAnalyticsException, IOException;

	public String getSimilarRootCauseSearch(String rootcause,String project) throws AgentAnalyticsException, IOException;

	public String addRootcause(RootcauseDetails rootcauseDetailsModal);
	
	public String symptomsUnmap(String inputJson);

	String rootcauseUnmap(String inputJson);
	
	public String getSymptomsdetails(String symptoms,String project)throws AgentAnalyticsException, IOException;

	String getSymptomsdetailsSet(String project, String json) throws AgentAnalyticsException, IOException;

	public String getProductattributes(SourceMap sourceMap, String project,String collectionId);

	public Map<String,String> getResolutionAndDescription(SymptomRootcauseMapping symptomRootcauseMapping, String project,String collectionId);
	
	public void monitoringLog(String apiurl,String status,String project,Timestamp timestamp);

	public String getWizardFlowStatus(String project) throws IOException;

	public String getRootcausedetailsSet(String project, String inpJson) throws AgentAnalyticsException, IOException;

	public String getRootcausedetails(String rootcause, String project) throws AgentAnalyticsException, IOException;
	
	public String getSymptom(String project, String issue, String inpJson) throws AgentAnalyticsException, IOException;
	
	public String updatequestions(String project) throws IOException;
	





	
}

package com.intelliservice.agentanalytics.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intelliservice.agentanalytics.model.Insights;
import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.model.RootcauseDetails;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
import com.intelliservice.agentanalytics.model.Symptoms;
import com.intelliservice.agentanalytics.utils.DataNotFoundException;
import com.intelliservice.agentanalytics.model.SymptomsDetails;


public interface ProductDetailsService {
	public String getSymptomsList(String project,String json);
	public String getRootcauseList(String project,String json);
	public ProductAttributes saveProductDetails(ProductAttributes productDetails);
	public List<ProductAttributes> getProductDetails();
	public List<Map<String, Set<String>>> getProductTriage(String collectionName,String inputJson, String rootaggs,String project) throws IOException;
	public List<SourceMap> saveSourcesMap(List<SourceMap> sourceMap) throws DataNotFoundException;
	public List<SourceMap> getSourcesMap(String project);
	public String collectionReindex(String sourceCollection, String destinationCollection,String project) throws IOException;
	public List<SymptomRootcauseMapping> symptomRootcauseMapping(String collectionId,String project) throws IOException;
	String updateSymptoms(Symptoms symptomsModal); 

	String deleteSymptoms(String json);

	String createSymptoms(Insights insightsModal);
	
	public String createSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping);
	
	public String updateSymptomsrootcause(SymptomRootcauseMapping symptomRootcauseMapping);
	
	public String deleteSymptomsrootcause(String json);
	
	public String updateStatusAndScore(SymptomRootcauseMapping symptomRootcauseMapping);
	
	public String createAttributes(String project);
	
	public String updateRootcause(SymptomRootcauseMapping symptomRootcauseMapping);

	public String deleteRootcause(String json);
	
	public String populateRecommendedSolutions(String inputRootcause,String project) throws JsonProcessingException;
	
	public String addSymptoms(SymptomsDetails symptomsDetailsModal);
	
	public String getSimilarSymptomsSearch(String symptoms,String project);
	
	public String getSimilarRootCauseSearch(String rootcause,String project);
	
	public String addRootcause(RootcauseDetails rootcauseDetailsModal);
	
	public String saveTriageSummary(String inputJson) throws IOException;
	
	public String symptomsUnmap(String json);
	
	public String rootcauseUnmap(String json);
	
	public String getSymptomsdetails(String symptoms,String project);
	
	public String getSymptomsdetailsSet(String project,String json);

	public String getWizardFlowStatus(String project) throws IOException;
	
	public String getRootcausedetailsSet(String project, String inpJson);
	
	public String getRootcausedetails(String rootcause, String project);
	
	public String getSymptom(String project, String issue, String inpJson);
	
	public String updatequestions(String project) throws IOException;

	
}

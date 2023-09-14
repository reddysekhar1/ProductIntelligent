package com.intelliservice.agentanalytics.dao;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.utils.ESQueries;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;
import com.jayway.jsonpath.JsonPath;



@Service
public class FieldValuesDaoImpl implements FieldValuesDao {
	private static final Logger log = LoggerFactory.getLogger(FieldValuesDaoImpl.class);
	
	@Autowired
	RestClient restClient; 
	
	@Autowired
	ESClient esClient;
	
	@Autowired
	GenericCollectionDao genericCollectionDao;

	@Override
	public List<String> getFieldValues(String collectionName, String fieldValue,String project) {

        List<String> list = new ArrayList<>();
        TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.FIELDSVALUES);
        try {
        	String result = esClient.performRequest(ProductTriageConstants.POST, "/" + collectionName + "/_search/", String.format(triageConfiguration.getValue(), fieldValue,project));
        	list = JsonPath.parse(result).read("$.aggregations..key");
		} catch (Exception e) {
			log.error("Error occur in getFieldValues", e);
		}
             
		return list;
	}
}

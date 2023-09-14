package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.NoSuchIndexException;
import org.springframework.stereotype.Service;

import com.intelliservice.agentanalytics.config.ESClient;
import com.intelliservice.agentanalytics.entity.SourceMapEntity;
import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.model.TriageConfiguration;
import com.intelliservice.agentanalytics.repository.SourceMapRepositiry;
import com.intelliservice.agentanalytics.utils.DataNotFoundException;
import com.intelliservice.agentanalytics.utils.ProductTriageConstants;
@Service
public class SourceMapDaoImpl implements SourceMapDao {
	private static final Logger log = LoggerFactory.getLogger(SourceMapDaoImpl.class);

	@Autowired
	SourceMapRepositiry sourceMapRepositiry;
	
	@Autowired
	ESClient esClient;
	
	@Autowired
	GenericCollectionDao genericCollectionDao;
	
	//Project code fix
	@Override
	public List<SourceMap> saveSourcesMap(List<SourceMap> sourceMap) throws DataNotFoundException {
		List<SourceMapEntity> entities   = null;
		List<SourceMap> models = null;
		Iterable<SourceMapEntity> forDelete   = null;
		try {
			entities = sourceMap.stream().map(obj -> mapToEntity(obj)).collect(Collectors.toList());
			String project  = sourceMap.get(0).getProject();
			forDelete = getSourcesMapEntity(project);
			sourceMapRepositiry.deleteAll(forDelete);
			entities = (List<SourceMapEntity>) sourceMapRepositiry.saveAll(entities);
			models = entities.stream().map(obj -> mapToModel(obj)).collect(Collectors.toList());
		} catch (IllegalArgumentException  e) {
			log.error("Error happen due to save or delete", e);
			throw new DataNotFoundException("Input source map is null");
		}catch (NoSuchIndexException e) {
			entities = (List<SourceMapEntity>) sourceMapRepositiry.saveAll(entities);
		}
		return models;
	}
	
private SourceMapEntity mapToEntity(SourceMap sourceMap) {
		SourceMapEntity sourceMapEntity = new SourceMapEntity();
			sourceMapEntity.setSourceFields(sourceMap.getSourceFields());
			sourceMapEntity.setSourceName(sourceMap.getSourceName());	
			sourceMapEntity.setProject(sourceMap.getProject());	
		return sourceMapEntity;		
	}
	
	private SourceMap mapToModel(SourceMapEntity sourceMapEntity) {
		SourceMap sourceMap = new SourceMap();
			sourceMap.setSourceFields(sourceMapEntity.getSourceFields());
			sourceMap.setSourceName(sourceMapEntity.getSourceName());
			sourceMap.setProject(sourceMapEntity.getProject());
		
		return sourceMap;				
	}


	@Override
	public List<SourceMap> getSourcesMap(String project){
		Iterable<SourceMapEntity> entityIter = sourceMapRepositiry.findByProject(project);
		List<SourceMap> models   = new ArrayList<>();
		
		entityIter.forEach(entity->models.add(mapToModel((SourceMapEntity) entity)));
		return models;
	}

	
	public Iterable<SourceMapEntity> getSourcesMapEntity(String project){
		return sourceMapRepositiry.findByProject(project);
		
	}

	/*
	 * This method will copy or reindex the data from Source collection to destinationCollection
	 * project fix
	 */
	@Override
	public String collectionReindex(String sourceCollection, String destinationCollection, String project) throws IOException {
		TriageConfiguration triageConfiguration = genericCollectionDao.getConfigurationData(ProductTriageConstants.REINDEXQUERY);
		String inputJson = String.format(triageConfiguration.getValue(), sourceCollection,destinationCollection,project);
		/*
		String inputJson1 = "{\"source\": {\"index\": \""+sourceCollection+"\"}, \"dest\": {\"index\": \""+destinationCollection+"\"}, \"script\": {"
				+ "    \"source\": \"ctx._id=null;ctx._source['project'] = '"+project+"'\","
				+ "    \"lang\": \"painless\""
				+ "  }}"; */
		String delJson = "{ \"query\": { \"match\": {\"project.keyword\": \""+project+"\"} } }";
		String requestUrl = "/"+ProductTriageConstants.PRODUCT_TRIAGE_COLL;
	    esClient.performRequest(ProductTriageConstants.POST, requestUrl+ProductTriageConstants.DELETEBYQUERY, delJson);
		return esClient.performRequest(ProductTriageConstants.POST, ProductTriageConstants.REINDEX_PATH, inputJson);
	}

}

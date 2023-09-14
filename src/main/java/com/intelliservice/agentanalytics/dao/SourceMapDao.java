package com.intelliservice.agentanalytics.dao;

import java.io.IOException;
import java.util.List;


import com.intelliservice.agentanalytics.model.SourceMap;
import com.intelliservice.agentanalytics.utils.DataNotFoundException;


public interface SourceMapDao {
	public List<SourceMap> saveSourcesMap(List<SourceMap> sourceMap) throws DataNotFoundException;

	public List<SourceMap> getSourcesMap(String project);

	public String collectionReindex(String sourceCollection, String destinationCollection, String project) throws IOException;
}

package com.intelliservice.agentanalytics.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.intelliservice.agentanalytics.entity.SourceMapEntity;

@Repository
public interface SourceMapRepositiry extends ElasticsearchRepository<SourceMapEntity, String> {
	@Query(" {     \"multi_match\" : {       \"query\":    \"?0\",       \"fields\": [ \"project\" ]     }   } ")
	List<SourceMapEntity> findByProject(String project);

}

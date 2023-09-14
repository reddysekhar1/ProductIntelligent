package com.intelliservice.agentanalytics.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.intelliservice.agentanalytics.entity.SymptomRootcauseMapEntity;

public interface SymptomRootcauseMappingRepository extends ElasticsearchRepository<SymptomRootcauseMapEntity, Integer> {
	
	List<SymptomRootcauseMapEntity> findAll();
	@Query(" {     \"multi_match\" : {       \"query\":    \"?0\",       \"fields\": [ \"project\" ]     }   } ")
	List<SymptomRootcauseMapEntity> findByProject(String project, Pageable pageable);
	long deleteByProject(String project);


}

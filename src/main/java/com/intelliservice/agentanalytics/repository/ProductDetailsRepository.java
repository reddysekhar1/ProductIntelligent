package com.intelliservice.agentanalytics.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.intelliservice.agentanalytics.entity.ProductAttributesEntity;

@Repository
public interface ProductDetailsRepository extends ElasticsearchRepository<ProductAttributesEntity, String> {
	
	@Query(" {     \"multi_match\" : {       \"query\":    \"?0\",       \"fields\": [ \"attribute_name\" ]     }   } ")
	List<ProductAttributesEntity> findByAttributeName(String attributeNmae);
	List<ProductAttributesEntity> findAll();
	@Query(" {     \"multi_match\" : {       \"query\":    \"?0\",       \"fields\": [ \"project\" ]     }   } ")
	List<ProductAttributesEntity> findByProject(String project);
	
	
}

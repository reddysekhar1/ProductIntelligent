package com.intelliservice.agentanalytics.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.web.PageableDefault;

import com.intelliservice.agentanalytics.entity.InsightsEntity;

public interface InsightsRepository extends ElasticsearchRepository<InsightsEntity, String>{
	//@Query("{\"bool\": {\"must\": [{\"match\": {\"connectorName\": \"?0\"}}]}}")
		//@Query("{     \"more_like_this\" : {       \"fields\" : [\"title\", \"description\"],       \"like\" : \"?0\",       \"min_term_freq\" : 1,       \"max_query_terms\" : 12     }   } ")
		@Query(" {     \"multi_match\" : {       \"query\":    \"?0\",       \"fields\": [ \"title\", \"description\" ]     }   } ")
		List<InsightsEntity> getSimilarIssues(String searchText);
		//@Query("{     \"bool\": {       \"should\": [         {           \"more_like_this\": {             \"like\": \"RIGHT BURNERS NOT WORKING\",             \"min_term_freq\": 2,             \"min_doc_freq\": 1,             \"max_query_terms\": 10,             \"min_word_length\": 2,             \"fields\": [               \"resolution\",                \"description^10\"             ]           }         },         {           \"multi_match\": {             \"fields\": [               \"resolution\",               \"description^10\"             ],             \"query\": \"RIGHT BURNERS NOT WORKING\",             \"fuzziness\": \"0\",             \"cutoff_frequency\": 0.01,             \"prefix_length\": 2,             \"slop\": 2           }         },         {           \"query_string\": {             \"query\": \"\\\"RIGHT BURNERS NOT WORKING\\\"\",             \"phrase_slop\": 30,             \"boost\": 10           }         }       ],       \"minimum_should_match\": 1,       \"filter\": [         { \"term\":  { \"product.keyword\": \"Prod_A\" }},         { \"term\": { \"model.keyword\": \"M1\"}}       ]     }   }")
		@Query("{     \"bool\": {       \"should\": [         {           \"more_like_this\": {             \"like\": \"?0\",             \"min_term_freq\": 2,             \"min_doc_freq\": 1,             \"max_query_terms\": 10,             \"min_word_length\": 2,             \"fields\": [               \"resolution\",                \"description^10\"             ]           }         },         {           \"multi_match\": {             \"fields\": [               \"resolution\",               \"description^10\"             ],             \"query\": \"?0\",             \"fuzziness\": \"0\",             \"cutoff_frequency\": 0.01,             \"prefix_length\": 2,             \"slop\": 2           }         },         {           \"query_string\": {             \"query\": \"?0\",             \"phrase_slop\": 30,             \"boost\": 10           }         }       ],       \"minimum_should_match\": 1,       \"filter\": [         { \"term\":  { \"product.keyword\": \"?1\" }},         { \"term\": { \"model.keyword\": \"?2\"}}       ]     }   },\"size\":1")
		Page<InsightsEntity> getSimilarIssues(String searchText,String product,String model,@PageableDefault(page = 0, value = 50)Pageable pageable);

}

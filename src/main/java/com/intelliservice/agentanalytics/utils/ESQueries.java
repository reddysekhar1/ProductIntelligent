package com.intelliservice.agentanalytics.utils;

public class ESQueries {

	private ESQueries() {
	}

	public static final String FIELDSVALUES = "{\"size\": 0,\"aggs\": {\"my-agg-name\":{\"terms\": {\"field\":\"%s.keyword\"}}}}";
	public static final String SIMILARISSUE = "{\"query\": {\"bool\": {\"must\": {\"terms\": {  \"symptoms.keyword\": %1$s}},\"should\": [{\"more_like_this\": {\"like\": \" %2$s\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"fields\": %3$s }},{\"multi_match\": {\"fields\": %3$s,\"query\": \" %2$s\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\" %2$s\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\": %4$s}},\"size\": 50 } ";
	public static final String RECOMMENDED_SOLUTION  = "{\"query\":{\"terms\":{\"symptoms.keyword\": %s}}}";

	public static final String INSIGHTQUERY_IF_PART1 = "{\"query\": {\"bool\": {\"must\": {\"terms\": {  \"symptoms.keyword\": ";
	public static final String INSIGHTQUERY_IF_PART2 = "}},\\\"should\\\": [{\\\"more_like_this\\\": {\\\"like\\\": \\\"";
	public static final String INSIGHTQUERY_IF_PART3 = "\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"fields\": ";
	public static final String INSIGHTQUERY_IF_PART4 = "}},{\"multi_match\": {\"fields\": ";
	public static final String INSIGHTQUERY_IF_PART5 = ",\"query\": \"";
	public static final String INSIGHTQUERY_IF_PART6 = "\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\"";
	public static final String INSIGHTQUERY_IF_PART7 = "\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\":";
	public static final String INSIGHTQUERY_IF_PART8 = "}},\"size\": 50 } ";
	
	public static final String INSIGHTQUERY_ELSE_PART1 = "{\"query\": {\"bool\": {\"should\": [{\"more_like_this\": {\"like\": \"";
	public static final String INSIGHTQUERY_ELSE_PART2="\",\"min_term_freq\": 2,\"min_doc_freq\": 1,\"max_query_terms\": 10,\"min_word_length\": 2,\"fields\": ";
	public static final String INSIGHTQUERY_ELSE_PART3="}},{\"multi_match\": {\"fields\": ";
	public static final String INSIGHTQUERY_ELSE_PART4=",\"query\": \"";
	public static final String INSIGHTQUERY_ELSE_PART5="\",\"fuzziness\": \"0\",\"cutoff_frequency\": 0.01,\"prefix_length\": 2,\"slop\": 2}},{\"query_string\": {\"query\":\"";
	public static final String INSIGHTQUERY_ELSE_PART6="\",\"phrase_slop\": 30,\"boost\": 10}}],\"minimum_should_match\": 1,\"filter\":";
	public static final String INSIGHTQUERY_ELSE_PART7="}},\"size\": 50 } ";
    public static final String INSIGHT_ISSUE_CLUSTER_IF_PART1 = "{\"query\": {\"bool\": {\"must\": [{\"query_string\": {\"query\": \"";
	public static final String INSIGHT_ISSUE_CLUSTER_IF_PART2 = "\"}},{\"terms\": {  \"symptoms.keyword\": ";
	public static final String INSIGHT_ISSUE_CLUSTER_IF_PART3 = "}}],\"filter\":";
	public static final String INSIGHT_ISSUE_CLUSTER_IF_PART4 = "}},\"size\": 100,\"query_hint\": \"true\",\"field_mapping\": {\"title\": [\"_source.";
	public static final String INSIGHT_ISSUE_CLUSTER_IF_PART5 = "\"],\"resolution\": [\"_source.";
	public static final String INSIGHT_ISSUE_CLUSTER_IF_PART6 = "\"]} }";
	
	public static final String INSIGHT_ISSUE_CLUSTER_ELSE_PART1 = "{\"query\": {\"bool\": {\"must\": [{\"query_string\": {\"query\": \"";
	public static final String INSIGHT_ISSUE_CLUSTER_ELSE_PART2 = "\"}}],\"filter\":";
	public static final String INSIGHT_ISSUE_CLUSTER_ELSE_PART3 = "}},\"size\": 100,\"query_hint\": \"true\",\"field_mapping\": {\"title\": [\"_source.";
	public static final String INSIGHT_ISSUE_CLUSTER_ELSE_PART4 = "\"],\"resolution\": [\"_source.";
	public static final String INSIGHT_ISSUE_CLUSTER_ELSE_PART5 = "\"]} }";
	
	
	public static final String INSIGHT_RECOMMENDED_SOLN_IF_PART1 = "{\"size\": 100,\"query\": {\"bool\": {\"must\": [{\"query_string\": {\"query\": \"";
	public static final String INSIGHT_RECOMMENDED_SOLN_IF_PART2 = "\"}},{\"terms\": {\"symptoms.keyword\":";
	public static final String INSIGHT_RECOMMENDED_SOLN_IF_PART3 = "}}],\"filter\": ";
	public static final String INSIGHT_RECOMMENDED_SOLN_IF_PART4 = "}},\"aggregations\": {\"significant_types\": {\"significant_terms\": {\"field\": \"resolution.keyword\"}}} }";
	
	public static final String INSIGHT_RECOMMENDED_SOLN_ELSE_PART1 = "{\"size\": 100,\"query\": {\"bool\": {\"must\": [{\"query_string\": {\"query\": \"";
	public static final String INSIGHT_RECOMMENDED_SOLN_ELSE_PART2 = "\"}}],\"filter\": ";
	public static final String INSIGHT_RECOMMENDED_SOLN_ELSE_PART3 = "}},\"aggregations\": {\"significant_types\": {\"significant_terms\": {\"field\": \"resolution.keyword\"}}} }";
	
	public static final String UPDATESYMPTOMS_PART1 ="{ \"script\": { \"source\": \"if (ctx._source.symptoms.contains(params.existing_symp)) { ctx._source.symptoms.add(params.new_symp); ctx._source.symptoms.remove(ctx._source.symptoms.indexOf(params.existing_symp)) }\", \"lang\": \"painless\", \"params\": { \"existing_symp\": \"";
	public static final String UPDATESYMPTOMS_PART2 ="\",\"new_symp\": \"";
	public static final String UPDATESYMPTOMS_PART3 ="\" } }, \"query\": { \"term\": { \"symptoms.keyword\": \"";
	public static final String UPDATESYMPTOMS_PART4="\" } } }";
	
	public static final String DELETESYMPTOMS_PART1="{ \"query\": { \"term\": { \"symptoms.keyword\": \"";
	public static final String DELETESYMPTOMS_PART2= "\" } } }";
	
	public static final String UPDATESYMPTOMSROOTCAUSE_PART1="{ \"script\": { \"source\": \"if (ctx._source.symptoms == params.symptoms) { ctx._source.symptom_title=params.symptom_title ;ctx._source.leadingquestion=params.leadingquestion }\",  \"lang\": \"painless\", \"params\": { \"symptoms\": \"";
	public static final String UPDATESYMPTOMSROOTCAUSE_PART2="\",\"symptom_title\": \"";
	public static final String UPDATESYMPTOMSROOTCAUSE_PART3="\",\"leadingquestion\":\"";
	public static final String UPDATESYMPTOMSROOTCAUSE_PART4="\" } }, \"query\": { \"term\": { \"symptoms.keyword\": \"";
	public static final String UPDATESYMPTOMSROOTCAUSE_PART5="\" } } }";
	
	public static final String DELETESYMPTOMSROOTCAUSE_PART1="{ \"query\":{\"terms\":{\"symptoms.keyword\":[\"";
	public static final String DELETESYMPTOMSROOTCAUSE_PART2="\"]}} }";
	
	public static final String DELETESYMPTOMSROOTCAUSE_PART11="{ \"script\": { \"source\": \"if (ctx._source.symptoms.contains(params.tag)) { ctx._source.symptoms.remove(ctx._source.symptoms.indexOf(params.tag)) }\", \"lang\": \"painless\", \"params\": { \"tag\": \"";
	public static final String DELETESYMPTOMSROOTCAUSE_PART12="\" } } }";
	
	public static final String UPDATESTATUSANDSCORE_PART1="{ \"script\": { \"source\": \"if (ctx._source.symptoms == params.symptoms && ctx._source.rootcause == params.rootcause ) { ctx._source.relevency=params.relevency;ctx._source.status=params.status }\", \"lang\": \"painless\", \"params\": { \"relevency\": \"";
	public static final String UPDATESTATUSANDSCORE_PART2= "\",\"status\":\"" ;
	public static final String UPDATESTATUSANDSCORE_PART3= "\",\"symptoms\": \"" ;
	public static final String UPDATESTATUSANDSCORE_PART4="\",\"rootcause\": \"";
	public static final String UPDATESTATUSANDSCORE_PART5="\" } }, \"query\": { \"bool\": { \"filter\": [ { \"term\": { \"symptoms.keyword\": \"";
	public static final String UPDATESTATUSANDSCORE_PART6="\" }}, { \"term\": { \"rootcause.keyword\": \"";
	public static final String UPDATESTATUSANDSCORE_PART7= "\"}} ] } } }";
	
	public static final String UPDATEROOTCAUSE_PART1="{ \"script\": { \"source\": \"if (ctx._source.rootcause == params.rootcause) { ctx._source.rootcause_title=params.rootcause_title}\",  \"lang\": \"painless\", \"params\": { \"rootcause\": \"";
	public static final String UPDATEROOTCAUSE_PART2="\",\"rootcause_title\": \"";
	public static final String UPDATEROOTCAUSE_PART3="\" } }, \"query\": { \"term\": { \"rootcause.keyword\": \"";
	public static final String UPDATEROOTCAUSE_PART4="\" } } }";
	
	public static final String DELETEROOTCAUSE_PART1="{ \"query\":{\"terms\":{\"rootcause.keyword\":[\"";
	public static final String DELETEROOTCAUSE_PART2="\"]}} }";
	
	public static final String DELETEROOTCAUSE_PART11="{ \"script\": { \"source\": \"if (ctx._source.rootcause.contains(params.tag)) { ctx._source.rootcause.remove(ctx._source.rootcause.indexOf(params.tag)) }\", \"lang\": \"painless\", \"params\": { \"tag\": \"";		
	public static final String DELETEROOTCAUSE_PART12="\" } } }";
}
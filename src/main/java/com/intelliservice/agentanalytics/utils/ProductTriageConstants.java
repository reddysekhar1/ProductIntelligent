package com.intelliservice.agentanalytics.utils;

public class ProductTriageConstants {
	private ProductTriageConstants() {}
	
	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	public static final String STATUS_CODE_OK = "200";
	public static final String STATUS_CODE_CREATED = "201";
	public static final String STATUS_CODE_BAD_REQUEST = "400";
	public static final String STATUS_CODE_SERVER_ERROR = "500";
	public static final String PROJECT_SAVED = "Project saved successfully";
	public static final String CATEGORY_SAVED = "Category saved successfully";
	public static final String CATEGORY_EXIST = "Category is already exist";
	
	
	public static final String RESPONSE_DATA = "responsedata";
	public static final String SYMPTOMS = "symptoms";
	public static final String ROOTCAUSE = "rootcause";
	public static final String ROOTCAUSEDATA = "rootcausedata";
	public static final String SOURCENAME = "source_name";
	public static final String SOURCEFIELD = "source_fields";
	public static final String PRODUCTATTRIBUTES = "productattributes";
	
	//Fields
	public static final String PRODUCT = "product";
	public static final String MODEL = "model";
	public static final String PRODUCT_ATTRIBUTES_COLL="productdetails";//TODO
	public static final String PRODUCT_TRIAGE_COLL = "producttriage";//TODO	
	public static final String MONITORINGLOG_COLL ="monitoringlogflow";
	
	public static final String SEARCH_WITH_CLUSTERS = "/_search_with_clusters";
	public static final String DELETE_BY_QUERY = "_delete_by_query";
	public static final String SOURCEMAP_COLLECTION = "sourcemap";//TODO
	public static final String SYMPTOM_ROOTCAUSE_COLLECTION = "symptomrootcausemapping";//TODO
	public static final String SYMPTOM_ROOTCAUSE_AND_TRIAGE_COLLECTION = "symptomrootcausemapping,producttriage";
	public static final String QUESTION = "_question";
	public static final String TEN = "10";
	public static final String TITLE = "_title";
	public static final String OPEN = "open";
	public static final String TRIAGE_CONFIGURATION_COLL= "triageconfiguration";
	public static final String PRODUCTTRIAGE_UTTERANCES_QUESTIONS_COLLECTION = "producttriage_utterances_questions";//TODO
	
	//Constant String use in application
	public static final String AGGREGATIONS  = "aggregations";
	public static final String BUCKETS = "buckets";
	public static final String KEY = "key";
	public static final String LEADINGQUESTION = "leadingquestion";
	public static final String ROOTCAUSETITLE = "rootcause_title";
	//sonar qube issue fixed
	public static final String UPDATEBYQUERY = "/_update_by_query";
	//sonar qube issue fixed
	public static final String DELETEBYQUERY = "/_delete_by_query";
	public static final String DOC = "/_doc";
	public static final String UTF8 = "UTF-8";
	public static final String SEARCH = "/_search";
	public static final String SCORE = "score";
	public static final String DEFAULTREVIEW = "Yet to Review";
	public static final String DOC_COUNT = "doc_count";
	public static final String RELEVANCY= "relevancy";
	
	//insightsdaoimpl
	public static final String YES = "yes";
	public static final String NO = "no";
	public static final String SKIP = "skip";
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String UTF = "UTF-8";
	public static final String ERROR = "error";
	public static final String REASON = "reason";
	public static final String PRODATTRIBUTES = "productattributes";	
	public static final String SOURCE_FIELDS = "source_fields";
	public static final String SOURCE_NAME = "source_name";
	public static final String KEYWORD = ".keyword";
	public static final String TERM = "term";
	public static final String DEFAULT = "DEFAULT";	
	
	public static final String HITS = "hits";
	public static final String CLUSTERS = "clusters";
	public static final String SOURCE = "_source";	
	public static final String ID = "id";	
	public static final String DOCUMENT = "documents";
	public static final String EMPTY_JSON = "{}";
	public static final String REINDEX_PATH = "_reindex";
	public static final String PROJECT = "project";
	public static final String ATTRIBUTENAME = "attribute_name";
	public static final String ATTRIBUTEDATA = "attribute_data";
	
	
		//Configuration Constants
	public static final String THRESHOLD = "threshold";
	public static final String SYMPTOMS_THRESHOLD = "symptomsthreshold";
	public static final String ROOTCAUSE_THRESHOLD = "rootcausethreshold";
	public static final String RESULT_SIZE = "resultsize";
	public static final String FIELDSVALUES = "filedsValueQuery";
	public static final String RECOMMENDED_SOLUTION = "recommendedSolutionQuery";
	public static final String SIMILARISSUEQUERY = "similarIssueQuery";
	public static final String SIMILARISSUEQUERY_YES_NO = "similarIssueQueryYesNo";
	public static final String SYMPTOMROOTCAUSEMAPPING= "symptomRootcauseMappingQuery";
	public static final String UPDATESYMPTOMSQUERY = "updateSymptomsQuery";
	public static final String UPDATESYMPTOMSROOTCAUSEQUERY = "updateSymptomsrootcauseQuery";
	public static final String DELETESYMPTOMSROOTCAUSE_MAPPING_COLL = "deleteSymptomsrootcausemappingCollection";
	public static final String DELETESYMPTOMSROOTCAUSE_PRODUCT_TRIAGE_COLL = "deleteSymptomsrootcauseproducttriageCollection";
	public static final String UPDATESTATUSANDSCOREQUERY = "updateStatusAndScoreQuery";
	public static final String ISSUECLUSTERSQUERY = "issueClustersQuery";
	public static final String ISSUECLUSTERSQUERY_YES_NO = "issueClustersQueryYesNo";
	public static final String REINDEXQUERY = "reindexQuery";
	public static final String UPDATEROOTCAUSEQUERY  = "updateRootcauseQuery";
	public static final String DELETEROOTCAUSEQUERY  = "deleteRootcauseQuery";
	public static final String DELETEROOTCAUSEQUERY_PRODUCT_TRIAGE_COLL  = "deleteRootcauseproducttriageQuery";
	public static final String SIMILARROOTCAUSESEARCHQUERY= "similarRootCauseSearchQuery";
	public static final String SIMILARSYMPTOMSSEARCHQUERY= "similarSymptomsSearchQuery";
	public static final String GETSOURCEMAP = "getsourcemap";
	public static final String GETSYMPTOMS = "getsymptomQuery";
	public static final String GETSYMPTOMSWITHOUTFILTER= "getsymptomQueryWithoutfilter";
	
	
	
}


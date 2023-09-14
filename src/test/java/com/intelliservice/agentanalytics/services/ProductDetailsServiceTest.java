package com.intelliservice.agentanalytics.services;
  
  import static org.junit.Assert.assertTrue; import static
  org.junit.jupiter.api.Assertions.assertEquals;
  
  import java.util.ArrayList; import java.util.List; import java.util.Map;
  import java.util.Set;
  //import org.junit.Test;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 
import org.junit.runner.RunWith; 
import org.mockito.InjectMocks; 
import org.mockito.Mock; 
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations; 
import org.springframework.test.context.junit4.SpringRunner;
  
  import com.intelliservice.agentanalytics.dao.ProductDetailsDao; import
  com.intelliservice.agentanalytics.dao.ProductTriageDao; import
  com.intelliservice.agentanalytics.dao.SourceMapDao; import
  com.intelliservice.agentanalytics.model.ProductAttributes; import
  com.intelliservice.agentanalytics.model.SourceMap; import
  com.intelliservice.agentanalytics.model.SymptomRootcauseMapping;
  
  @RunWith(SpringRunner.class) public class ProductDetailsServiceTest {
  
  @InjectMocks ProductDetailsServiceImpl productDetailsService;
  
  @Mock ProductDetailsDao productDetailsDao;
  
  @Mock ProductTriageDao productTriageDao;
  
  @Mock SourceMapDao sourceMapDao;
  
  private final String project = "HCL";
  
  private final String json="{\"issuedetails\":\"ice is not making freezer\",\"product\":\"refrigrator\",\"model\":\"side by side door\"}";
  
  @BeforeEach public void config() { MockitoAnnotations.initMocks(this); }
  
  @Test 
  public void saveProductDetailsPass() 
  { 
  ProductAttributes productAttributes = getProductAttributesMockObject();
  Mockito.when(productDetailsDao.saveProductDetails(productAttributes)).thenReturn(productAttributes); 
  ProductAttributes returnObj =productDetailsService.saveProductDetails(productAttributes);
  assertEquals(returnObj.getAttributeName(), "name"); 
  }
  
  @org.junit.Test(expected = Exception.class)
  public void saveProductDetailsFail() {
  ProductAttributes productAttributes = getProductAttributesMockObject();
  Mockito.when(productDetailsDao.saveProductDetails(productAttributes)).thenThrow(Exception.class); 
  ProductAttributes returnObj =productDetailsService.saveProductDetails(productAttributes);
  assertEquals(returnObj.getAttributeName(), "name"); 
  }
  
  @Test 
  public void getProductDetailsPass() 
  {
  List<ProductAttributes> list =getProductAttributesListMockObject();
  Mockito.when(productDetailsDao.getProductDetails()).thenReturn(list);
  List<ProductAttributes> resultList =productDetailsService.getProductDetails(); 
  assertEquals(list.size(),resultList.size());
  } 
  
  @org.junit.Test(expected = Exception.class) 
  public void getProductDetailsFail() 
  {
  Mockito.when(productDetailsDao.getProductDetails()).thenThrow(Exception.class );
  List<ProductAttributes> resultList =productDetailsService.getProductDetails();
  
  }
  
  @Test 
  public void getProductTriagesymptomsPass() throws Exception {
  List<SymptomRootcauseMapping> srcList = getSymptomRootcauseListMockObject();
  List<SourceMap> smList = getSourceMapListMockObject();
  Mockito.when(productDetailsDao.getAllSymptomRootcauseMapping(project)).thenReturn(srcList);
  Mockito.when(sourceMapDao.getSourcesMap(project)).thenReturn(smList);
  Mockito.when(productTriageDao.getProductTriage("producttriage",json, smList.get(0),"symptoms",project)).thenReturn(getSymptomsAggsString());
  List<Map<String, Set<String>>> result =productDetailsService.getProductTriage("producttriage",json,"symptoms",project);
  assertTrue(result.size() > 0);
  
  }
  
  @Test 
  public void getProductTriageRcPass() throws Exception 
  {
  List<SymptomRootcauseMapping> srcList = getSymptomRootcauseListMockObject();
  List<SourceMap> smList = getSourceMapListMockObject();
  Mockito.when(productDetailsDao.getAllSymptomRootcauseMapping(project)).thenReturn(srcList);
  Mockito.when(sourceMapDao.getSourcesMap(project)).thenReturn(smList);
  Mockito.when(productTriageDao.getProductTriage("producttriage",json,smList.get(0),"rootcause",project)).thenReturn(getRcAggsString());
  List<Map<String, Set<String>>> result =productDetailsService.getProductTriage("producttriage",json,"rootcause",project);
  assertTrue(result.size() > 0);
  
  }
  
  @org.junit.Test(expected = AssertionError.class) 
  public void getProductTriageFail() throws Exception 
  { 
  List<SymptomRootcauseMapping> srcList =getSymptomRootcauseListMockObject(); 
  List<SourceMap> smList =getSourceMapListMockObject();
  Mockito.when(productDetailsDao.getAllSymptomRootcauseMapping(project)).thenReturn(srcList);
  Mockito.when(sourceMapDao.getSourcesMap(project)).thenReturn(smList);
  Mockito.when(productTriageDao.getProductTriage("producttriage",json,smList.get(0),"rootcause",project)).thenReturn("abc");
  List<Map<String, Set<String>>> result =productDetailsService.getProductTriage("producttriage","rootcause",project,json);
  assertTrue(result.size() > 0);
  
  }
  
  @Test
  public void saveSourcesMapPass() throws Exception 
  { 
  List<SourceMap> smList = getSourceMapListMockObject();
  Mockito.when(sourceMapDao.saveSourcesMap(smList)).thenReturn(smList);
  List<SourceMap> resultSm = productDetailsService.saveSourcesMap(smList);
  assertTrue(resultSm.size() == smList.size()); 
  }
  
  @org.junit.Test(expected = NullPointerException.class) 
  public void saveSourcesMapFail() throws Exception 
  { 
  List<SourceMap> smList =getSourceMapListMockObject();
  Mockito.when(sourceMapDao.saveSourcesMap(smList)).thenReturn(null);
  List<SourceMap> resultSm = productDetailsService.saveSourcesMap(smList);
  assertTrue(resultSm.size() == smList.size());
  
  }
  
  @Test public void getSourcesMapPass() throws Exception 
  { 
  List<SourceMap> smList = getSourceMapListMockObject();
  Mockito.when(sourceMapDao.getSourcesMap(project)).thenReturn(smList);
  List<SourceMap> resultSm = productDetailsService.getSourcesMap(project);
  assertTrue(resultSm.size() == smList.size()); 
  }
  
  @org.junit.Test(expected = NullPointerException.class) 
  public void getSourcesMapFail() throws Exception 
  { 
  List<SourceMap> smList =getSourceMapListMockObject();
  Mockito.when(sourceMapDao.getSourcesMap(project)).thenReturn(null);
  List<SourceMap> resultSm = productDetailsService.getSourcesMap(project);
  assertTrue(resultSm.size() == smList.size()); 
  }
  
  @Test public void collectionReindexPass() throws Exception{
  Mockito.when(sourceMapDao.collectionReindex("ticketdata","ticketdatanew", project)).thenReturn(getReindexMockString()); 
  String str =productDetailsService.collectionReindex("ticketdata","ticketdatanew",project); 
  assertEquals(str.contains("Created"), true);
  
  } 
  @org.junit.Test(expected = Exception.class) 
  public void collectionReindexFail()throws Exception
  {
  Mockito.when(sourceMapDao.collectionReindex("ticketdata","ticketdatanew",project)).thenReturn("abc");
  String str =productDetailsService.collectionReindex("ticketdata","ticketdatanew",project); 
  assertEquals(str.contains("created"), true);
  
  }
  
  @Test 
  public void getSymptomsListPass() throws Exception
  {
  Mockito.when(productDetailsService.getSymptomsList(project,json)).thenReturn( getSymptomRootcause()); 
  String str =productDetailsService.getSymptomsList(project,json); 
  assertTrue(str.length() > 0);
  }
  
  @Test 
  public void getRootcauseListPass() throws Exception
  {
  Mockito.when(productDetailsService.getRootcauseList(project,json)).thenReturn(getRootcauseSymptoms()); 
  String str =productDetailsService.getRootcauseList(project,json); 
  assertTrue(str.length() > 0);
  }
  
  @org.junit.Test(expected = NullPointerException.class) 
  public void getSymptomsListFail() throws Exception
  {
  Mockito.when(productDetailsService.getSymptomsList(project,json)).thenReturn(null); 
  String str = productDetailsService.getSymptomsList(project,json);
  assertTrue(str.length() > 0); 
  } 
  @org.junit.Test(expected = NullPointerException.class) 
  public void getRootcauseListFail() throws Exception
  {
  Mockito.when(productDetailsService.getRootcauseList(project,json)).thenReturn(null); 
  String str = productDetailsService.getRootcauseList(project,json);
  assertTrue(str.length() > 0); 
  }
  
  //Below to this we have function for mock the object 
  public String getReindexMockString() 
  { 
	  String str ="{\"took\":65,\"timed_out\":false,\"total\":25,\"updated\":0,\"created\":0,\"deleted\":0,\"batches\":1,\"version_conflicts\":0,\"noops\":0,\"retries\":{\"bulk\":0,\"search\":0},\"throttled_millis\":0,\"requests_per_second\":-1.0,\"throttled_until_millis\":0,\"failures\":[{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"yxonU3sB7fqfCwV5Q5sP\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"zBonU3sB7fqfCwV5_5u7\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"zRooU3sB7fqfCwV5JJsN\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"zhooU3sB7fqfCwV5VJvh\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"zxooU3sB7fqfCwV5eJvV\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"0BpLU3sB7fqfCwV5yZui\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"0RpMU3sB7fqfCwV5AZva\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"0xpNU3sB7fqfCwV5DpvQ\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"1BpNU3sB7fqfCwV5LZvp\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"1RpNU3sB7fqfCwV5jpu8\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"1hpOU3sB7fqfCwV5L5uV\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"1xpOU3sB7fqfCwV5w5vP\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"2BpOU3sB7fqfCwV55JuB\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"2RpPU3sB7fqfCwV5BZvl\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"2hpPU3sB7fqfCwV5WpuO\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"2xpPU3sB7fqfCwV5cpvX\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"3BpPU3sB7fqfCwV5mpv5\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"3RpPU3sB7fqfCwV54JsA\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"3hpQU3sB7fqfCwV5O5uZ\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"3xpQU3sB7fqfCwV5Zpt2\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"4BpRU3sB7fqfCwV5zZv8\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"4RpSU3sB7fqfCwV5BJtc\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"4hpSU3sB7fqfCwV5j5uL\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"tQNFc3sBhGuMABFyKQPx\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400},{\"index\":\"ticketdataNew\",\"type\":\"_doc\",\"id\":\"tgNFc3sBhGuMABFyLgMJ\",\"cause\":{\"type\":\"invalid_index_name_exception\",\"reason\":\"Invalid index name [ticketdataNew], must be lowercase\",\"index_uuid\":\"_na_\",\"index\":\"ticketdataNew\"},\"status\":400}]}";
	  return str; 
	  }
  
  public String getSymptomsAggsString() { 
	  String str ="{\"took\":8,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":25,\"relation\":\"eq\"},\"max_score\":null,\"hits\":[]},\"aggregations\":{\"symptoms\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":31,\"buckets\":[{\"key\":\"cooking issues\",\"doc_count\":13,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":13,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":9,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":9}]}},{\"key\":\"M2\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":4}]}}]}}]}},{\"key\":\"issues oven\",\"doc_count\":11,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":11,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":7,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":7}]}},{\"key\":\"M2\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":4}]}}]}}]}},{\"key\":\"error code\",\"doc_count\":7,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":7,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":4}]}},{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}}]}}]}},{\"key\":\"issues eoc\",\"doc_count\":7,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":7,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":4}]}},{\"key\":\"M2\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":3}]}}]}}]}},{\"key\":\"codes eoc\",\"doc_count\":6,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":6,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":4}]}},{\"key\":\"M1\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":2}]}}]}}]}},{\"key\":\"eoc error\",\"doc_count\":6,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":6,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":4}]}},{\"key\":\"M1\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":2}]}}]}}]}},{\"key\":\"issues fault\",\"doc_count\":6,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":6,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":4,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":4}]}},{\"key\":\"M1\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":2}]}}]}}]}},{\"key\":\"issues operations\",\"doc_count\":5,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":5,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}},{\"key\":\"M2\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":2}]}}]}}]}},{\"key\":\"doesn bake\",\"doc_count\":4,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":4,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}},{\"key\":\"M2\",\"doc_count\":1,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":1}]}}]}}]}},{\"key\":\"oven doesn\",\"doc_count\":4,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":4,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}},{\"key\":\"M2\",\"doc_count\":1,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":1}]}}]}}]}}]}}}"; 
	  return str;
	  }
  
  public String getRcAggsString() { 
	  String str ="{\"took\":2,\"timed_out\":false,\"_shards\":{\"total\":1,\"successful\":1,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":{\"value\":25,\"relation\":\"eq\"},\"max_score\":null,\"hits\":[]},\"aggregations\":{\"rootcause\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":42,\"buckets\":[{\"key\":\"0_cust_found_tested_faulty\",\"doc_count\":10,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":10,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":5}]}},{\"key\":\"M2\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":5}]}}]}}]}},{\"key\":\"1_working_board_sensor_operational\",\"doc_count\":10,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":10,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":5}]}},{\"key\":\"M2\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":5}]}}]}}]}},{\"key\":\"2_clock_checked_display_tech\",\"doc_count\":10,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":10,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":5}]}},{\"key\":\"M2\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":5}]}}]}}]}},{\"key\":\"3_clock_whack_ck_dim\",\"doc_count\":10,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":10,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":5}]}},{\"key\":\"M2\",\"doc_count\":5,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":5}]}}]}}]}},{\"key\":\"0_defective_faulty_failed_check\",\"doc_count\":3,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":3,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":2}]}},{\"key\":\"M1\",\"doc_count\":1,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":1}]}}]}}]}},{\"key\":\"0_sparking_bad_ordrng_lighting\",\"doc_count\":3,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":3,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}}]}}]}},{\"key\":\"1_burner_screws_burners_foot\",\"doc_count\":3,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":3,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}}]}}]}},{\"key\":\"1_circuit_tests_run_test\",\"doc_count\":3,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":3,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":2}]}},{\"key\":\"M1\",\"doc_count\":1,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":1}]}}]}}]}},{\"key\":\"2_test_operation_check_console\",\"doc_count\":3,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":3,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M1\",\"doc_count\":3,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":3}]}}]}}]}},{\"key\":\"2_ven_heating_light_ver\",\"doc_count\":3,\"product\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"Prod_A\",\"doc_count\":3,\"model\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"M2\",\"doc_count\":2,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2020\",\"doc_count\":2}]}},{\"key\":\"M1\",\"doc_count\":1,\"yom\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"2019\",\"doc_count\":1}]}}]}}]}}]}}}";
  return str; 
  }
  
  public String deleteSymptomsRootcauseString() { 
	  String str="{\"took\":2048,\"timed_out\":false,\"total\":10,\"deleted\":10,\"batches\":1,\"version_conflicts\":0,\"noops\":0,\"retries\":{\"bulk\":0,\"search\":0},\"throttled_millis\":0,\"requests_per_second\":-1.0,\"throttled_until_millis\":0,\"failures\":[]}";
  return str; 
  }
  
    public String getRootcauseSymptoms() 
     { 
	  String str="[{\"rootcause\":\"2_clock_defective_tests_assembly\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"1_technician_checked_verified_faulty\",\"rootcausedata\":[{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"rootcause\",\"rootcausedata\":[{\"symptoms\":\"ovel\",\"relevancy\":\"100\",\"leadingquestion\":\"leadingquestion\",\"status\":\"open\"},{\"symptoms\":\"ovel1\",\"relevancy\":\"100\",\"leadingquestion\":\"leadingquestion\",\"status\":\"open\"},{\"symptoms\":\"ovel11\",\"relevancy\":\"10\",\"leadingquestion\":\"leadingquestion1234\",\"status\":\"open\"},{\"symptoms\":\"ovel112\",\"relevancy\":\"20\",\"leadingquestion\":\"leadingquestion1234\",\"status\":\"open\"}]},{\"rootcause\":\"3_switch_light_broken_replaced\",\"rootcausedata\":[{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"0_defective_faulty_failed_check\",\"rootcausedata\":[{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"}]},{\"rootcause\":\"0_no_power_display_prescreened\",\"rootcausedata\":[{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"0_glides_liner_griddle_grates\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"1_gasket_seal_reinstalled_sealing\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"0_light_power_switch_beeping\",\"rootcausedata\":[{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"2_dented_jamesray_damagednot_phone\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"}]},{\"rootcause\":\"0_smell_monoxide_leak_gas\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"rootcause123\",\"rootcausedata\":[{\"symptoms\":\"ovel1\",\"relevancy\":\"100\",\"leadingquestion\":\"leadingquestion123\",\"status\":\"open\"}]},{\"rootcause\":\"0_leaking_cracked_pressure_leak\",\"rootcausedata\":[{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"}]},{\"rootcause\":\"3_knob_knobs_broken_stem\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"3_clock_whack_ck_dim\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"1_temp_oven_connected_soluti\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"}]},{\"rootcause\":\"0_lock_display_displayed_console\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"1_working_board_sensor_operational\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"2_broil_broiler_bake_heatingreferred\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"0_checked_hinges_liner_parts\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"1_oven_temps_orifices_orifice\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"1_ven_ac_heating_complaint\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"},{\"symptoms\":\"cooking issues\"}]},{\"rootcause\":\"1_circuit_tests_run_test\",\"rootcausedata\":[{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"}]},{\"rootcause\":\"0_lighting_light_burners_wire\",\"rootcausedata\":[{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"}]},{\"rootcause\":\"0_hole_gas_ignition_smell\",\"rootcausedata\":[{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"}]},{\"rootcause\":\"rootcause12\",\"rootcausedata\":[{\"symptoms\":\"ovel1\",\"relevancy\":\"100\",\"leadingquestion\":\"leadingquestion12\",\"status\":\"open\"}]},{\"rootcause\":\"1_oven_door_replaced_faulty\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"}]},{\"rootcause\":\"1_flame_flames_residue_levels\",\"rootcausedata\":[{\"symptoms\":\"wont light\"},{\"symptoms\":\"oven light\"},{\"symptoms\":\"interior light\"}]},{\"rootcause\":\"3_elec_installed_cord_buzzing\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"}]},{\"rootcause\":\"0_clock_faulty_cleared_blk\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"}]},{\"rootcause\":\"0_cust_found_tested_faulty\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"rootcause1234\",\"rootcausedata\":[{\"symptoms\":\"ovel1\",\"relevancy\":\"10\",\"leadingquestion\":\"leadingquestion1234\",\"status\":\"open\"}]},{\"rootcause\":\"2_oven_clock_timer_temperature\",\"rootcausedata\":[{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"2_clock_checked_display_tech\",\"rootcausedata\":[{\"symptoms\":\"issues fault\"},{\"symptoms\":\"eoc error\"},{\"symptoms\":\"codes eoc\"},{\"symptoms\":\"error code new\"}]},{\"rootcause\":\"2_hinge_reinstalled_reset_popped\",\"rootcausedata\":[{\"symptoms\":\"closing properly\"},{\"symptoms\":\"cooking issues\"}]}]"; 
	  return str; 
	  } 
  
    public String getSymptomRootcause() 
      { 
    	String str="[{\"symptoms\":\"ovel1\",\"rootcausedata\":[{\"rootcausetitle\":\"rootcause_title\",\"rootcause\":\"rootcause\",\"relevancy\":\"100\",\"status\":\"open\"},{\"rootcausetitle\":\"rootcause_title12\",\"rootcause\":\"rootcause12\",\"relevancy\":\"100\",\"status\":\"open\"},{\"rootcausetitle\":\"rootcause_title123\",\"rootcause\":\"rootcause123\",\"relevancy\":\"100\",\"status\":\"open\"},{\"rootcausetitle\":\"rootcause_title1234\",\"rootcause\":\"rootcause1234\",\"relevancy\":\"10\",\"status\":\"open\"}]},{\"symptoms\":\"cooking issues\",\"rootcausedata\":[{\"rootcause\":\"0_light_power_switch_beeping\"},{\"rootcause\":\"0_glides_liner_griddle_grates\"},{\"rootcause\":\"3_switch_light_broken_replaced\"},{\"rootcause\":\"2_hinge_reinstalled_reset_popped\"},{\"rootcause\":\"1_gasket_seal_reinstalled_sealing\"},{\"rootcause\":\"0_checked_hinges_liner_parts\"},{\"rootcause\":\"3_knob_knobs_broken_stem\"},{\"rootcause\":\"2_broil_broiler_bake_heatingreferred\"},{\"rootcause\":\"1_ven_ac_heating_complaint\"},{\"rootcause\":\"0_smell_monoxide_leak_gas\"}]},{\"symptoms\":\"closing properly\",\"rootcausedata\":[{\"rootcause\":\"3_knob_knobs_broken_stem\"},{\"rootcause\":\"2_hinge_reinstalled_reset_popped\"},{\"rootcause\":\"2_broil_broiler_bake_heatingreferred\"},{\"rootcause\":\"1_ven_ac_heating_complaint\"},{\"rootcause\":\"1_gasket_seal_reinstalled_sealing\"},{\"rootcause\":\"0_smell_monoxide_leak_gas\"},{\"rootcause\":\"0_checked_hinges_liner_parts\"},{\"rootcause\":\"2_dented_jamesray_damagednot_phone\"},{\"rootcause\":\"1_oven_door_replaced_faulty\"},{\"rootcause\":\"0_glides_liner_griddle_grates\"}]},{\"symptoms\":\"oven light\",\"rootcausedata\":[{\"rootcause\":\"1_flame_flames_residue_levels\"},{\"rootcause\":\"1_circuit_tests_run_test\"},{\"rootcause\":\"0_lighting_light_burners_wire\"},{\"rootcause\":\"0_leaking_cracked_pressure_leak\"},{\"rootcause\":\"0_hole_gas_ignition_smell\"},{\"rootcause\":\"0_defective_faulty_failed_check\"},{\"rootcause\":\"3_knob_knobs_broken_stem\"},{\"rootcause\":\"2_broil_broiler_bake_heatingreferred\"},{\"rootcause\":\"1_ven_ac_heating_complaint\"},{\"rootcause\":\"0_smell_monoxide_leak_gas\"}]},{\"symptoms\":\"ovel112\",\"rootcausedata\":[{\"rootcausetitle\":\"rootcause\",\"rootcause\":\"rootcause\",\"relevancy\":\"20\",\"status\":\"open\"}]},{\"symptoms\":\"ovel11\",\"rootcausedata\":[{\"rootcausetitle\":\"rootcause\",\"rootcause\":\"rootcause\",\"relevancy\":\"10\",\"status\":\"open\"}]},{\"symptoms\":\"wont light\",\"rootcausedata\":[{\"rootcause\":\"1_flame_flames_residue_levels\"},{\"rootcause\":\"1_circuit_tests_run_test\"},{\"rootcause\":\"0_lighting_light_burners_wire\"},{\"rootcause\":\"0_leaking_cracked_pressure_leak\"},{\"rootcause\":\"0_hole_gas_ignition_smell\"},{\"rootcause\":\"0_defective_faulty_failed_check\"},{\"rootcause\":\"3_knob_knobs_broken_stem\"},{\"rootcause\":\"2_broil_broiler_bake_heatingreferred\"},{\"rootcause\":\"1_ven_ac_heating_complaint\"},{\"rootcause\":\"0_smell_monoxide_leak_gas\"}]},{\"symptoms\":\"error code new\",\"rootcausedata\":[{\"rootcause\":\"2_oven_clock_timer_temperature\"},{\"rootcause\":\"2_clock_defective_tests_assembly\"},{\"rootcause\":\"1_technician_checked_verified_faulty\"},{\"rootcause\":\"1_oven_temps_orifices_orifice\"},{\"rootcause\":\"0_no_power_display_prescreened\"},{\"rootcause\":\"0_lock_display_displayed_console\"},{\"rootcause\":\"3_clock_whack_ck_dim\"},{\"rootcause\":\"2_clock_checked_display_tech\"},{\"rootcause\":\"1_working_board_sensor_operational\"},{\"rootcause\":\"0_cust_found_tested_faulty\"}]},{\"symptoms\":\"codes eoc\",\"rootcausedata\":[{\"rootcause\":\"1_temp_oven_connected_soluti\"},{\"rootcause\":\"0_clock_faulty_cleared_blk\"},{\"rootcause\":\"3_elec_installed_cord_buzzing\"},{\"rootcause\":\"2_clock_defective_tests_assembly\"},{\"rootcause\":\"1_oven_temps_orifices_orifice\"},{\"rootcause\":\"0_lock_display_displayed_console\"},{\"rootcause\":\"3_clock_whack_ck_dim\"},{\"rootcause\":\"2_clock_checked_display_tech\"},{\"rootcause\":\"1_working_board_sensor_operational\"},{\"rootcause\":\"0_cust_found_tested_faulty\"}]},{\"symptoms\":\"ovel\",\"rootcausedata\":[{\"rootcausetitle\":\"rootcause_title\",\"rootcause\":\"rootcause\",\"relevancy\":\"100\",\"status\":\"open\"}]},{\"symptoms\":\"eoc error\",\"rootcausedata\":[{\"rootcause\":\"1_temp_oven_connected_soluti\"},{\"rootcause\":\"0_clock_faulty_cleared_blk\"},{\"rootcause\":\"3_elec_installed_cord_buzzing\"},{\"rootcause\":\"2_clock_defective_tests_assembly\"},{\"rootcause\":\"1_oven_temps_orifices_orifice\"},{\"rootcause\":\"0_lock_display_displayed_console\"},{\"rootcause\":\"3_clock_whack_ck_dim\"},{\"rootcause\":\"2_clock_checked_display_tech\"},{\"rootcause\":\"1_working_board_sensor_operational\"},{\"rootcause\":\"0_cust_found_tested_faulty\"}]},{\"symptoms\":\"issues fault\",\"rootcausedata\":[{\"rootcause\":\"1_temp_oven_connected_soluti\"},{\"rootcause\":\"0_clock_faulty_cleared_blk\"},{\"rootcause\":\"3_elec_installed_cord_buzzing\"},{\"rootcause\":\"2_clock_defective_tests_assembly\"},{\"rootcause\":\"1_oven_temps_orifices_orifice\"},{\"rootcause\":\"0_lock_display_displayed_console\"},{\"rootcause\":\"3_clock_whack_ck_dim\"},{\"rootcause\":\"2_clock_checked_display_tech\"},{\"rootcause\":\"1_working_board_sensor_operational\"},{\"rootcause\":\"0_cust_found_tested_faulty\"}]},{\"symptoms\":\"interior light\",\"rootcausedata\":[{\"rootcause\":\"1_flame_flames_residue_levels\"},{\"rootcause\":\"1_circuit_tests_run_test\"},{\"rootcause\":\"0_lighting_light_burners_wire\"},{\"rootcause\":\"0_leaking_cracked_pressure_leak\"},{\"rootcause\":\"0_hole_gas_ignition_smell\"},{\"rootcause\":\"0_defective_faulty_failed_check\"},{\"rootcause\":\"3_knob_knobs_broken_stem\"},{\"rootcause\":\"2_broil_broiler_bake_heatingreferred\"},{\"rootcause\":\"1_ven_ac_heating_complaint\"},{\"rootcause\":\"0_smell_monoxide_leak_gas\"}]}]";
    	return str; 
    	} 
    
  public List<SourceMap> getSourceMapListMockObject(){
  List<SourceMap> smList = new ArrayList<>(); 
  List<String> dataList = new ArrayList<>(); 
  dataList.add("product"); 
  dataList.add("model");
  dataList.add("yom"); 
  SourceMap sm = new SourceMap();
  sm.setSourceName("productArrt"); 
  sm.setSourceFields(dataList); 
  for(int i = 0;i< 10; i++) 
  { 
	  smList.add(sm); 
  }
  
  return smList;
  
  }
  
  public List<SymptomRootcauseMapping> getSymptomRootcauseListMockObject() 
  {
  List<SymptomRootcauseMapping> srcList = new ArrayList<>();
  SymptomRootcauseMapping src = new SymptomRootcauseMapping();
  src.setRootcause("3_valve_safety_coil_burner");
  src.setSymptoms("oven doesn"); 
  src.setLeadingquestion("oven doesn_Question");
  for(int i = 0; i< 10; i++) 
  { 
  srcList.add(src); 
  }
  return srcList; 
  }
  
  
  public List<ProductAttributes> getProductAttributesListMockObject() 
  {
  List<ProductAttributes> productAttributesList = new ArrayList<>(); 
  for(int i= 0; i < 5; i++) 
  {
  productAttributesList.add(getProductAttributesMockObject()); 
  } 
  return productAttributesList; 
  }
  
  
  public ProductAttributes getProductAttributesMockObject() 
  {
  ProductAttributes productAttributes = new ProductAttributes();
  productAttributes.setAttributeData(new ArrayList<>());
  productAttributes.setAttributeName("name"); 
  return productAttributes; 
  }
  
  
  @Test 
  public void CreateSymptomsRootcausepass() throws Exception
  {
  SymptomRootcauseMapping symptomRootcauseMapping=CreateSymptomRootcauseListMockObject(); 
  String src="succes";
  Mockito.when(productDetailsDao.createSymptomsrootcause(symptomRootcauseMapping)).thenReturn(src); 
  String str =productDetailsService.createSymptomsrootcause(symptomRootcauseMapping);
  assertEquals(str.contains("succes"), true); 
  }
  
  @Test 
  public void addRootcause() throws Exception
  {
  SymptomRootcauseMapping symptomRootcauseMapping=CreateSymptomRootcauseListMockObject(); 
  String src="succes";
  Mockito.when(productDetailsDao.createSymptomsrootcause(symptomRootcauseMapping)).thenReturn(src); 
  String str =productDetailsService.createSymptomsrootcause(symptomRootcauseMapping);
  assertEquals(str.contains("succes"), true); 
  }
  
  
  @org.junit.Test(expected = Exception.class) 
  public void createSymptomsRootcausefail() throws Exception
  { 
  SymptomRootcauseMapping symptomRootcauseMapping=CreateSymptomRootcauseListMockObject(); //String src="succes";
  Mockito.when(productDetailsDao.createSymptomsrootcause(symptomRootcauseMapping)).thenThrow(Exception.class); 
  String str =
  productDetailsService.createSymptomsrootcause(symptomRootcauseMapping);
  assertEquals(str.contains("succes"), true); 
  }
  
  
  @Test public void deleteSymptomsRootcausepass() throws Exception
  { 
  String symptoms="door closing"; 
  String src="succes";
  Mockito.when(productDetailsDao.deleteSymptomsrootcause(symptoms)).thenReturn(deleteSymptomsRootcauseString()); 
  String str =productDetailsService.deleteSymptomsrootcause(symptoms);
  assertEquals(str.contains("delete"), true); 
  }
  
  @org.junit.Test(expected = Exception.class) 
  public void updateSymptomsRootcausefail()throws Exception
  { 
  SymptomRootcauseMapping symptomRootcauseMapping=CreateSymptomRootcauseListMockObject(); //String src="succes";
  Mockito.when(productDetailsDao.updateSymptomsrootcause(symptomRootcauseMapping)).thenThrow(Exception.class); 
  String str = productDetailsService.createSymptomsrootcause(symptomRootcauseMapping);
  assertEquals(str.contains("succes"), true); }
  
  
  public SymptomRootcauseMapping CreateSymptomRootcauseListMockObject() {
  SymptomRootcauseMapping src = new SymptomRootcauseMapping();
  src.setRelevancy("70"); src.setStatus("open");
  src.setSymptoms("door closing_new");
  src.setRootcause("3_valve_safety_coil_burner");
  src.setRootcause("3_knob_knobs_broken_stem_new");
  src.setSymptoms("oven doesn"); src.setLeadingquestion("oven doesn_Question");
  return src; }
  
  
  
  }
 
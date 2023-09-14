package com.intelliservice.agentanalytics.services;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

import com.intelliservice.agentanalytics.dao.FieldValuesDao;
import com.intelliservice.agentanalytics.dao.GenericCollectionDao;

@RunWith(SpringRunner.class)
public class GenericCollectionServiceTest {
	@InjectMocks
	GenericCollectionServiceImpl genericCollectionService;
	
	@Mock
	GenericCollectionDao genericCollectionDao;
	
	@Mock
	FieldValuesDao fieldValuesDao;
	
	@BeforeEach
	public void config() {
		MockitoAnnotations.initMocks(this);
	}
	@Test
	public void getCollectionsPass() throws Exception {
		List<String> smList  = getAllCollections();
		Mockito.when(genericCollectionDao.getCollections()).thenReturn(smList);
		List<String> resultSm = genericCollectionService.getCollections();
		assertTrue(resultSm.size() == smList.size());
	}
	
	//@Test(expected = NullPointerException.class)
	public void getCollectionsFail() throws Exception {
		List<String> smList  = getAllCollections();
		Mockito.when(genericCollectionDao.getCollections()).thenReturn(null);
		List<String> resultSm = genericCollectionService.getCollections();
		assertTrue(resultSm.size() == smList.size());
	}
	@Test
	public void getFieldsPass() throws Exception {
		List<String> smList  = getAllFields();
		String sm="sourcemap";
		Mockito.when(genericCollectionDao.getFields(sm)).thenReturn(smList);
		List<String> resultSm = genericCollectionService.getFields(sm);
		assertTrue(resultSm.size() == smList.size());
	}
	
	//@Test(expected = NullPointerException.class)
	public void getFieldsFail() throws Exception {
		List<String> smList  = getAllFields();
		String sm="sourcemap";
		Mockito.when(genericCollectionDao.getFields(sm)).thenReturn(null);
		List<String> resultSm = genericCollectionService.getFields(sm);
		assertTrue(resultSm.size() == smList.size());
	}
	
	@Test
	public void getFieldValuesPass() throws Exception {
		List<String> smList  = getAllFields();
		String sm="sourcemap";
		String sm1="source";
		String sm3="project";
		Mockito.when(fieldValuesDao.getFieldValues(sm,sm1,sm3)).thenReturn(smList);
		List<String> resultSm = genericCollectionService.getFieldValues(sm,sm1,sm3);
		assertTrue(resultSm.size() == smList.size());
	}
	
	
	/*
	 * @Test(expected = NullPointerException.class) public void getFieldValuesFail()
	 * throws Exception { List<String> smList = getAllFields(); String
	 * sm="sourcemap"; String sm1="source";
	 * Mockito.when(genericCollectionDao.getFieldValues(sm,sm1)).thenReturn(null);
	 * List<String> resultSm = genericCollectionService.getFieldValues(sm,sm1);
	 * assertTrue(resultSm.size() == smList.size());
	 * 
	 * }
	 */
	 
	
	
	public List<String> getAllCollections()
	{
		
		List<String> dataList = new ArrayList<>();
		dataList.add("projectdata_collection");
		dataList.add("sourcemap");
		dataList.add("botsdata_collection");
		
		
		return dataList;
	}
	
	public List<String> getAllFields()
	{
		
		List<String> dataList = new ArrayList<>();
		dataList.add("source");
		dataList.add("test");
		dataList.add("map");
		
		
		return dataList;
	}
	
	
}

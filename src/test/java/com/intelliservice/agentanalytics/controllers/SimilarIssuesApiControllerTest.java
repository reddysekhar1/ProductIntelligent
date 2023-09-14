package com.intelliservice.agentanalytics.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.intelliservice.agentanalytics.model.ProductAttributes;
import com.intelliservice.agentanalytics.services.InsightsService;

public class SimilarIssuesApiControllerTest {
	@InjectMocks
	InsightsApiController insightsApiController;
	
	@Mock
	InsightsService insightsService;
	
	MockMvc mockMvc; 
	@BeforeEach
	public void config() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(insightsApiController).build();
	}
	
	//@Test
	public void getSimilarTickets() throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("model", "M1");
		obj.put("product", "Prod_A");
		String response = new String();
        when(insightsService.getSimilarIssues("burner",obj.toString(),"HCL")).thenReturn(response);

		mockMvc.perform(get("/producttriage/insights/similarissues/burner").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
	}
	
	
	//@Test
	public void getProductAttributes() throws Exception{
		List<ProductAttributes> list= new ArrayList<>();
		when(insightsService.getProductAttribute("HCL")).thenReturn(list);
		mockMvc.perform(get("/producttriageapi/insights/productattributes").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
}

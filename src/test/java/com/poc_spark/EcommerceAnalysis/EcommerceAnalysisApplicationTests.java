package com.poc_spark.EcommerceAnalysis;

import com.poc_spark.EcommerceAnalysis.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class EcommerceAnalysisApplicationTests {


	@MockitoBean
	private CategoryService categoryService;


	@Test
	void contextLoads() throws Exception {}

}
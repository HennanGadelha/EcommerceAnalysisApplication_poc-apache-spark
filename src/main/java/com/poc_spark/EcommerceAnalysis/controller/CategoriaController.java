package com.poc_spark.EcommerceAnalysis.controller;

import java.util.List;

import com.poc_spark.EcommerceAnalysis.dto.CategorySummary;
import com.poc_spark.EcommerceAnalysis.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class CategoriaController {

    private final CategoryService categoryService;

    public CategoriaController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public List<CategorySummary> listarCategoriasMaisRentaveis() {
        return categoryService.listMostProfitableCategories();
    }
}

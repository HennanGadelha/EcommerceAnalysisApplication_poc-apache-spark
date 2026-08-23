package com.poc_spark.EcommerceAnalysis.controller;

import com.poc_spark.EcommerceAnalysis.dto.MonthlySalesSummary;
import com.poc_spark.EcommerceAnalysis.dto.ProductRevenueSummary;
import com.poc_spark.EcommerceAnalysis.dto.ProductSalesSummary;
import com.poc_spark.EcommerceAnalysis.dto.RegionRevenueSummary;
import com.poc_spark.EcommerceAnalysis.service.SalesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping("/monthly")
    public List<MonthlySalesSummary> getMonthlySales() {
        return salesService.getMonthlySales();
    }

    @GetMapping("/products/top")
    public List<ProductSalesSummary> getBestSellingProducts() {
        return salesService.getBestSellingProducts();
    }

    @GetMapping("/products/revenue")
    public List<ProductRevenueSummary> getProductsByRevenue() {
        return salesService.getProductsByRevenue();
    }

    @GetMapping("/regions/revenue")
    public List<RegionRevenueSummary> getRevenueByRegion() {
        return salesService.getRevenueByRegion();
    }
}
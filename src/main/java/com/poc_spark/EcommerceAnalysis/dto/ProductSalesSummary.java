package com.poc_spark.EcommerceAnalysis.dto;

public record ProductSalesSummary(
        String product,
        Long quantitySold
) {
}
package com.poc_spark.EcommerceAnalysis.dto;

import java.math.BigDecimal;

public record ProductRevenueSummary(
        String product,
        BigDecimal totalRevenue
) {
}
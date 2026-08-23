package com.poc_spark.EcommerceAnalysis.dto;

import java.math.BigDecimal;

public record CategorySummary(String category, BigDecimal totalRevenue) {
}


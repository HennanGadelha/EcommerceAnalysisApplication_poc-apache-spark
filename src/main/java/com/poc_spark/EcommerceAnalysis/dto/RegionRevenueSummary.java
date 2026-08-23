package com.poc_spark.EcommerceAnalysis.dto;

import java.math.BigDecimal;

public record RegionRevenueSummary(
        String region,
        BigDecimal totalRevenue
) {
}
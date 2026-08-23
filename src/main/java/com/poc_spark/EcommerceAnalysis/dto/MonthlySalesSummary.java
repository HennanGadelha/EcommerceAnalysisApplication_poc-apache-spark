package com.poc_spark.EcommerceAnalysis.dto;

import java.math.BigDecimal;

public record MonthlySalesSummary(String month,
                                  BigDecimal totalSales) {
}

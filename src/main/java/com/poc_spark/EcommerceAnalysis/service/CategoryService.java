package com.poc_spark.EcommerceAnalysis.service;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.sum;

import com.poc_spark.EcommerceAnalysis.SalesSparkDataSource;
import com.poc_spark.EcommerceAnalysis.dto.CategorySummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final SalesSparkDataSource salesSparkDataSource;

    public CategoryService(SalesSparkDataSource salesSparkDataSource) {
        this.salesSparkDataSource = salesSparkDataSource;
    }

    public List<CategorySummary> listMostProfitableCategories() {

        Dataset<Row> categories = salesSparkDataSource
                .getSales()
                .filter(col("categoria").isNotNull())
                .filter(col("status_pedido").equalTo("Entregue"))
                .withColumn(
                        "total_value",
                        col("preco").multiply(col("quantidade"))
                )
                .groupBy("categoria")
                .agg(
                        sum("total_value")
                                .alias("total_revenue")
                )
                .orderBy(
                        col("total_revenue").desc()
                );

        return categories.collectAsList()
                .stream()
                .map(row -> new CategorySummary(
                        row.getString(0),
                        new BigDecimal(row.get(1).toString())
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}
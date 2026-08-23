package com.poc_spark.EcommerceAnalysis;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

@Component
public class SalesSparkDataSource {

    private final SparkSession spark;
    private Dataset<Row> sales;

    public SalesSparkDataSource(SparkSession spark) {
        this.spark = spark;
        loadData();
    }

    private void loadData() {

        try {
            sales = spark.read()
                    .option("header", true)
                    .option("inferSchema", true)
                    .csv("/data/ecommerce.csv");

            sales.cache();
            sales.count();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error loading sales data",
                    e
            );
        }
    }

    public Dataset<Row> getSales() {
        return sales;
    }

}

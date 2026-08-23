package com.poc_spark.EcommerceAnalysis;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.*;

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
            ClassPathResource resource =
                    new ClassPathResource("vendas_2024.csv");

            File tempFile = File.createTempFile(
                    "vendas_2024",
                    ".csv"
            );

            try (InputStream inputStream = resource.getInputStream();
                 OutputStream outputStream = new FileOutputStream(tempFile)) {

                inputStream.transferTo(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sales = spark.read()
                    .option("header", true)
                    .option("inferSchema", true)
                    .csv(tempFile.getAbsolutePath());

            sales.cache();
            sales.count();

            tempFile.delete();

        } catch (IOException e) {
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

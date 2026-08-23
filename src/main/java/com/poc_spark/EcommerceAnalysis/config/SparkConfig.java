package com.poc_spark.EcommerceAnalysis.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Bean(destroyMethod = "stop")
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName("EcommerceAnalysis")
                .master("spark://spark-master:7077")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
    }
}
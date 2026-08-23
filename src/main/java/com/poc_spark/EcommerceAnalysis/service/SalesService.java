package com.poc_spark.EcommerceAnalysis.service;

import com.poc_spark.EcommerceAnalysis.SalesSparkDataSource;
import com.poc_spark.EcommerceAnalysis.dto.MonthlySalesSummary;
import com.poc_spark.EcommerceAnalysis.dto.ProductRevenueSummary;
import com.poc_spark.EcommerceAnalysis.dto.ProductSalesSummary;
import com.poc_spark.EcommerceAnalysis.dto.RegionRevenueSummary;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.apache.spark.sql.functions.*;

@Service
public class SalesService {
    private final SalesSparkDataSource salesSparkDataSource;

    public SalesService(SalesSparkDataSource salesSparkDataSource) {
        this.salesSparkDataSource = salesSparkDataSource;
    }

    public List<MonthlySalesSummary> getMonthlySales() {

        Dataset<Row> monthlySales = salesSparkDataSource.getSales()
                .filter(col("status_pedido").equalTo("Entregue"))
                .withColumn("month", date_format(col("data_venda"), "yyyy-MM"))
                .withColumn("total_value", col("preco").multiply(col("quantidade")))
                .groupBy("month").agg(sum("total_value").alias("total_sales")).orderBy("month");

        return monthlySales.collectAsList()
                .stream()
                .map(row -> new MonthlySalesSummary(
                        row.getString(0),
                        new BigDecimal(row.get(1).toString())
                                .setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();

    }

    public List<ProductSalesSummary>  getBestSellingProducts(){

        Dataset<Row> products = salesSparkDataSource.getSales()
                .filter(col("status_pedido").equalTo("Entregue"))
                .filter(col("nome_produto").isNotNull()).groupBy("nome_produto")
                .agg(sum("quantidade").alias("quantity_sold")).orderBy(col("quantity_sold").desc());

        return products.collectAsList()
                .stream()
                .map(row -> new ProductSalesSummary(
                        row.getString(0),
                        ((Number) row.get(1)).longValue()
                ))
                .toList();
    }

    public List<ProductRevenueSummary> getProductsByRevenue() {

        Dataset<Row> products = salesSparkDataSource
                .getSales()
                .filter(col("status_pedido").equalTo("Entregue"))
                .filter(col("nome_produto").isNotNull())
                .withColumn(
                        "total_value",
                        col("preco").multiply(col("quantidade"))
                )
                .groupBy("nome_produto")
                .agg(
                        sum("total_value")
                                .alias("total_revenue")
                )
                .orderBy(
                        col("total_revenue").desc()
                );

        return products.collectAsList()
                .stream()
                .map(row -> new ProductRevenueSummary(
                        row.getAs("nome_produto"),
                        new BigDecimal(
                                row.getAs("total_revenue").toString()
                        ).setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }

    public List<RegionRevenueSummary> getRevenueByRegion() {

        Dataset<Row> regions = salesSparkDataSource
                .getSales()
                .filter(col("status_pedido").equalTo("Entregue"))
                .filter(col("regiao").isNotNull())
                .withColumn(
                        "total_value",
                        col("preco").multiply(col("quantidade"))
                )
                .groupBy("regiao")
                .agg(
                        sum("total_value")
                                .alias("total_revenue")
                )
                .orderBy(
                        col("total_revenue").desc()
                );

        return regions.collectAsList()
                .stream()
                .map(row -> new RegionRevenueSummary(
                        row.getAs("regiao"),
                        new BigDecimal(
                                row.getAs("total_revenue").toString()
                        ).setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();
    }
}

# EcommerceAnalysis (POC Apache Spark + Spring Boot)

Este repositório é uma prova de conceito que integra Apache Spark (spark-sql) em uma aplicação Spring Boot para análise de dados de e-commerce.

Sumário rápido:
- O Spark é usado para processamento e agregação em larga escala (DataFrame/SQL).
- A aplicação inicializa uma SparkSession como bean Spring e pré-carrega os dados de vendas no startup.

## Arquitetura e classes principais (visão de alto nível)

- EcommerceAnalysisApplication (src/main/java/...): classe padrão Spring Boot que sobe o contexto da aplicação.

- config/SparkConfig.java
  - Cria um bean SparkSession com @Bean(destroyMethod = "stop").
  - appName("EcommerceAnalysis") e master("spark://spark-master:7077").
  - `spark.ui.enabled` está configurado como `false` aqui (ideal para alguns deployments em cluster). Para desenvolvimento local troque `master` para `local[*]` ou remova a configuração `spark.ui.enabled` para expor a UI.
  - O bean garante que exista apenas uma SparkSession compartilhada pela aplicação e que ela seja parada corretamente quando o contexto Spring for desligado.

- SalesSparkDataSource.java
  - Recebe a SparkSession via injeção de dependência e faz `loadData()` no construtor.
  - loadData(): lê `/data/ecommerce.csv` com header e `inferSchema`, faz `sales.cache()` e em seguida `sales.count()`.
    - `cache()` + `count()` tem o objetivo de materializar o dataset em memória no startup (evita overhead de criação repetida e acelera consultas subsequentes).
    - Ponto importante: carregar e cachear na inicialização reduz latência das primeiras requisições, porém aumenta o tempo/memória de startup.

  - Dados de teste (observação):
    - Por padrão o caminho lido é `/data/ecommerce.csv`. Para testes locais coloque um CSV com este nome nesse diretório ou ajuste o caminho no código.
    - Há uma massa de testes com cerca de 1.000.000 (um milhão) de registros compactada no diretório `data` do projeto: `data/ecommerce.rar`. Extraia esse arquivo para obter `data/ecommerce.csv` e usar como dataset de teste.
    - Atenção: esse CSV grande consome memória ao ser cacheado; em máquinas com pouca memória ajuste a estratégia (ex.: não cachear todo o dataset, usar amostragem ou particionar em arquivos menores).

- service/SalesService.java
  - Camada que aplica transformações Spark (DataFrame API) sobre o Dataset carregado e retorna DTOs para a API.
  - Três métodos exemplares explicados abaixo.

## Por que iniciar a SparkSession e carregar dados no startup?
- Compartilhamento: SparkSession é caro de criar; como bean Spring, é instanciada uma vez e compartilhada entre componentes.
- Latência: pré-carregar e cachear os dados (count() para materializar) reduz latência das primeiras consultas.
- Operacional: o `destroyMethod = "stop"` garante desligamento limpo do Spark quando a aplicação fecha.

Trade-offs:
- Prós: respostas mais rápidas após startup, evita recriar sessões repetidamente.
- Contras: maior uso de memória e startup mais lento. Em ambientes com muitos serviços ou memória limitada, pode ser preferível criar sessions sob demanda.

## Como executar (resumo rápido)
Pré-requisitos: Java 17, Gradle wrapper (incluso), Docker (opcional).

Local (desenvolvimento):
- Editar `SparkConfig.master` para `local[*]` se quiser rodar Spark localmente sem cluster.
- Windows: `gradlew.bat bootRun`  
- Linux/macOS: `./gradlew bootRun`

Build e jar:
- `./gradlew build` -> gera `build/libs/*.jar`
- `java -jar build/libs/<artifact>.jar`

Docker:
- `./gradlew build`
- `docker build -t ecommerce-analysis .`
- `docker run --rm -p 8080:8080 -p 4040:4040 ecommerce-analysis`

Observação: o projeto por padrão aponta para um master `spark://spark-master:7077` (espera um cluster Spark). Rodando local ou em Docker single-node, ganhos de paralelismo serão limitados.

## Explicação de métodos do SalesService (exemplos selecionados)

1) getMonthlySales()
- O que faz:
  - `sales.filter(status_pedido == "Entregue")` — considera apenas pedidos entregues.
  - `withColumn("month", date_format(data_venda, "yyyy-MM"))` — agrupa por mês.
  - calcula `total_value = preco * quantidade` e faz `groupBy(month).agg(sum(total_value).alias("total_sales"))`.
  - Ordena por mês e converte cada linha em MonthlySalesSummary (DTO) com BigDecimal arredondado.
- Por que é útil:
  - KPI de receita mensal para dashboards e análise de tendência.

2) getBestSellingProducts()
- O que faz:
  - Filtra `status_pedido == "Entregue"` e `nome_produto` não nulo.
  - Agrupa por `nome_produto` somando `quantidade` vendida e ordena descendentemente.
  - Converte o resultado para ProductSalesSummary (produto + quantidade vendida).
- Por que é útil:
  - Identifica produtos com maior volume de vendas — importante para estoque e promoção.

3) getProductsByRevenue()
- O que faz:
  - Filtra pedidos entregues e produtos não nulos.
  - Cria coluna `total_value = preco * quantidade`.
  - Agrupa por `nome_produto` e soma `total_value` como `total_revenue`, ordenando do maior para o menor.
  - Converte cada linha em ProductRevenueSummary com BigDecimal arredondado.
- Por que é útil:
  - Mostra quais produtos geram mais receita, diferente do rank por unidades vendidas (útil para margem/price strategy).

Observação geral sobre os métodos:
- Todos usam a API DataFrame do Spark, retornam listas coletadas em memória (`collectAsList()`), o que é aceitável para conjuntos moderados; para datasets muito grandes recomenda-se paginação, agregações parcimoniosas ou retornar amostras.

## Spark UI e Debug
- A Spark UI normalmente aparece em 4040 quando há um contexto local ativo, exceto quando `spark.ui.enabled` está false (como no config atual). Para desenvolvimento, habilite a UI removendo essa configuração ou definindo `.config("spark.ui.enabled", "true")` e/ou usando `local[*]` como master.

## Produção (Kubernetes) — ponto rápido
- Em produção prefira:
  - Spark on Kubernetes (Spark Operator ou spark-submit com master k8s)
  - Dados em armazenamento distribuído (S3/HDFS)
  - Configurar executors/cores/memória (ex.: 4 executors x 2 cores x 4Gi)
  - Monitoramento via Prometheus/Grafana e centralização de logs

## Testes
- `./gradlew test`

---
Se quiser, adiciono:
- exemplos de manifests Kubernetes (Deployment/Service) para rodar a API + Spark Operator;
- um docker-compose com um pequeno Spark master/worker para desenvolvimento local;
- alterações para tornar o master configurável por propriedade/variável de ambiente (recomendado).
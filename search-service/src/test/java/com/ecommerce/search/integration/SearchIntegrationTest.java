package com.ecommerce.search.integration;

import com.ecommerce.search.dto.SearchRequest;
import com.ecommerce.search.dto.SearchResponse;
import com.ecommerce.search.event.ProductEvent;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" })
@Testcontainers
@DirtiesContext
class SearchIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    private static final ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withExposedPorts(9200);

    static {
        elasticsearchContainer.start();
    }

    @DynamicPropertySource
    static void elasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.elasticsearch.cluster-nodes", () -> elasticsearchContainer.getHost() + ":" + elasticsearchContainer.getMappedPort(9200));
    }

    @BeforeEach
    void setUp() throws IOException {
        // Ensure the Elasticsearch index is created
        elasticsearchClient.indices().create(new org.elasticsearch.client.indices.CreateIndexRequest("products"), org.elasticsearch.client.RequestOptions.DEFAULT);
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the Elasticsearch index
        elasticsearchClient.indices().delete(new org.elasticsearch.client.indices.DeleteIndexRequest("products"), org.elasticsearch.client.RequestOptions.DEFAULT);
    }

    @Test
    void givenProductEvent_whenPublishedToKafka_thenProductIsIndexed() {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductId(1L);
        productEvent.setName("Laptop");
        productEvent.setDescription("High-end laptop");
        productEvent.setPrice(999.99);
        productEvent.setStock(10);
        productEvent.setAction("CREATED");

        kafkaTemplate.send("product-topic", String.valueOf(productEvent.getProductId()), productEvent);

        // Wait for the product to be indexed in Elasticsearch
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setQuery("Laptop");
            ResponseEntity<SearchResponse[]> response = restTemplate.postForEntity("/api/search", searchRequest, SearchResponse[].class);
            return response.getBody() != null && response.getBody().length > 0;
        });

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setQuery("Laptop");
        ResponseEntity<SearchResponse[]> response = restTemplate.postForEntity("/api/search", searchRequest, SearchResponse[].class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("Laptop", response.getBody()[0].getName());
    }
}
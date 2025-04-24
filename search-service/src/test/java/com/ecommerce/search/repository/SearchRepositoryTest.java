package com.ecommerce.search.repository;

import com.ecommerce.search.dto.SearchResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class SearchRepositoryTest {

    @Autowired
    private SearchRepository searchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

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
    void setUp() {
        // Ensure the Elasticsearch index is created
        if (!elasticsearchOperations.indexOps(SearchResponse.class).exists()) {
            elasticsearchOperations.indexOps(SearchResponse.class).create();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up the Elasticsearch index
        if (elasticsearchOperations.indexOps(SearchResponse.class).exists()) {
            elasticsearchOperations.indexOps(SearchResponse.class).delete();
        }
    }

    @Test
    void saveProduct_ReturnsSavedProduct() {
        SearchResponse product = new SearchResponse();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-end laptop");
        product.setPrice(999.99);
        product.setStock(10);

        SearchResponse savedProduct = searchRepository.save(product);

        assertNotNull(savedProduct);
        assertEquals("Laptop", savedProduct.getName());
    }

    @Test
    void findById_ReturnsProduct() {
        SearchResponse product = new SearchResponse();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("High-end laptop");
        product.setPrice(999.99);
        product.setStock(10);
        searchRepository.save(product);

        SearchResponse foundProduct = searchRepository.findById(1L).orElse(null);

        assertNotNull(foundProduct);
        assertEquals("Laptop", foundProduct.getName());
    }
}
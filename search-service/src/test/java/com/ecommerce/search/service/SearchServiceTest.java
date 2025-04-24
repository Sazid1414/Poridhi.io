package com.ecommerce.search.service;

import com.ecommerce.search.dto.SearchRequest;
import com.ecommerce.search.dto.SearchResponse;
import com.ecommerce.search.exception.SearchNotFoundException;
import com.ecommerce.search.repository.SearchRepository;
import com.ecommerce.search.service.impl.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private SearchHits<SearchResponse> searchHits;

    @InjectMocks
    private SearchServiceImpl searchService;

    private SearchRequest searchRequest;
    private SearchResponse searchResponse;

    @BeforeEach
    void setUp() {
        searchRequest = new SearchRequest();
        searchRequest.setQuery("Laptop");

        searchResponse = new SearchResponse();
        searchResponse.setId(1L);
        searchResponse.setName("Laptop");
        searchResponse.setDescription("High-end laptop");
        searchResponse.setPrice(999.99);
        searchResponse.setStock(10);
    }

    @Test
    void searchProducts_ReturnsResults() {
        SearchHit<SearchResponse> searchHit = new SearchHit<>(null, null, null, 1.0f, null, null, searchResponse);
        when(searchHits.stream()).thenReturn(Collections.singletonList(searchHit).stream());
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(SearchResponse.class))).thenReturn(searchHits);

        List<SearchResponse> results = searchService.searchProducts(searchRequest);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Laptop", results.get(0).getName());
        verify(elasticsearchOperations, times(1)).search(any(CriteriaQuery.class), eq(SearchResponse.class));
    }

    @Test
    void searchProducts_NoResults_ThrowsException() {
        when(searchHits.stream()).thenReturn(Collections.<SearchHit<SearchResponse>>emptyList().stream());
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(SearchResponse.class))).thenReturn(searchHits);

        assertThrows(SearchNotFoundException.class, () -> searchService.searchProducts(searchRequest));
        verify(elasticsearchOperations, times(1)).search(any(CriteriaQuery.class), eq(SearchResponse.class));
    }
}
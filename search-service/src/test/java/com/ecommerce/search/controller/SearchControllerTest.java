package com.ecommerce.search.controller;

import com.ecommerce.search.dto.SearchRequest;
import com.ecommerce.search.dto.SearchResponse;
import com.ecommerce.search.service.SearchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void searchProducts_ReturnsSearchResults() throws Exception {
        List<SearchResponse> results = Arrays.asList(searchResponse);
        when(searchService.searchProducts(any(SearchRequest.class))).thenReturn(results);

        mockMvc.perform(post("/api/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].description").value("High-end laptop"));

        verify(searchService, times(1)).searchProducts(any(SearchRequest.class));
    }
}
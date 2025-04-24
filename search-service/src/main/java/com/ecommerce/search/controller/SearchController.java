package com.ecommerce.search.controller;

import com.ecommerce.search.dto.SearchRequest;
import com.ecommerce.search.dto.SearchResponse;
import com.ecommerce.search.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<List<SearchResponse>> searchProducts(@RequestBody SearchRequest searchRequest) {
        List<SearchResponse> results = searchService.searchProducts(searchRequest);
        return ResponseEntity.ok(results);
    }
}
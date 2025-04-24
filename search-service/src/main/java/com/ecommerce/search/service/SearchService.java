package com.ecommerce.search.service;

import com.ecommerce.search.dto.SearchRequest;
import com.ecommerce.search.dto.SearchResponse;

import java.util.List;

public interface SearchService {

    List<SearchResponse> searchProducts(SearchRequest searchRequest);
}
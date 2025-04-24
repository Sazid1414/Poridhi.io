package com.ecommerce.search.repository;

import com.ecommerce.search.dto.SearchResponse;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchRepository extends ElasticsearchRepository<SearchResponse, Long> {
}
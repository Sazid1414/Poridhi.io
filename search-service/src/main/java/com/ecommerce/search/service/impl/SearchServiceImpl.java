package com.ecommerce.search.service.impl;

import com.ecommerce.search.dto.SearchRequest;
import com.ecommerce.search.dto.SearchResponse;
import com.ecommerce.search.exception.SearchNotFoundException;
import com.ecommerce.search.repository.SearchRepository;
import com.ecommerce.search.service.SearchService;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public SearchServiceImpl(SearchRepository searchRepository, ElasticsearchOperations elasticsearchOperations) {
        this.searchRepository = searchRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public List<SearchResponse> searchProducts(SearchRequest searchRequest) {
        String query = searchRequest.getQuery();
        Criteria criteria = new Criteria("name").matches(query)
                .or(new Criteria("description").matches(query));
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        SearchHits<SearchResponse> searchHits = elasticsearchOperations.search(criteriaQuery, SearchResponse.class);
        List<SearchResponse> results = searchHits.stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            throw new SearchNotFoundException(query);
        }

        return results;
    }
}
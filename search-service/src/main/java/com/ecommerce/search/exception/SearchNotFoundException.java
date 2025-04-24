package com.ecommerce.search.exception;

public class SearchNotFoundException extends RuntimeException {

    public SearchNotFoundException(String query) {
        super("No results found for query: " + query);
    }
}
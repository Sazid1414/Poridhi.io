package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.event.ProductEvent;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductServiceImpl(ProductRepository productRepository, KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public ProductResponse saveProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        Product savedProduct = productRepository.save(product);

        // Publish product event to Kafka
        ProductEvent productEvent = new ProductEvent(savedProduct.getId(), savedProduct.getName(),
                savedProduct.getDescription(), savedProduct.getPrice(), savedProduct.getStock(), "CREATED");
        kafkaTemplate.send("product-topic", String.valueOf(savedProduct.getId()), productEvent);

        return mapToProductResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        Product updatedProduct = productRepository.save(product);

        // Publish product event to Kafka
        ProductEvent productEvent = new ProductEvent(updatedProduct.getId(), updatedProduct.getName(),
                updatedProduct.getDescription(), updatedProduct.getPrice(), updatedProduct.getStock(), "UPDATED");
        kafkaTemplate.send("product-topic", String.valueOf(updatedProduct.getId()), productEvent);

        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.deleteById(id);

        // Publish product deletion event to Kafka
        ProductEvent productEvent = new ProductEvent(id, product.getName(), product.getDescription(),
                product.getPrice(), product.getStock(), "DELETED");
        kafkaTemplate.send("product-topic", String.valueOf(id), productEvent);
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        return response;
    }
}
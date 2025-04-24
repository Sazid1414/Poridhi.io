package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.event.ProductEvent;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Laptop", "High-end laptop", 999.99, 10);
        productRequest = new ProductRequest("Laptop", "High-end laptop", 999.99, 10);
    }

    @Test
    void saveProduct_ReturnsSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(kafkaTemplate.send(anyString(), anyString(), any(ProductEvent.class))).thenReturn(null);

        ProductResponse response = productService.saveProduct(productRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
        verify(kafkaTemplate, times(1)).send(eq("product-topic"), anyString(), any(ProductEvent.class));
    }

    @Test
    void getAllProducts_ReturnsProductList() {
        List<Product> products = Arrays.asList(
                new Product(1L, "Laptop", "High-end laptop", 999.99, 10),
                new Product(2L, "Phone", "Smartphone", 599.99, 20)
        );
        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("Laptop", responses.get(0).getName());
        assertEquals("Phone", responses.get(1).getName());
    }

    @Test
    void getProductById_ReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Laptop", response.getName());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void updateProduct_ReturnsUpdatedProduct() {
        ProductRequest updateRequest = new ProductRequest("Laptop Pro", "Upgraded laptop", 1299.99, 5);
        Product updatedProduct = new Product(1L, "Laptop Pro", "Upgraded laptop", 1299.99, 5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(kafkaTemplate.send(anyString(), anyString(), any(ProductEvent.class))).thenReturn(null);

        ProductResponse response = productService.updateProduct(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Laptop Pro", response.getName());
        assertEquals(1299.99, response.getPrice());
        verify(kafkaTemplate, times(1)).send(eq("product-topic"), anyString(), any(ProductEvent.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);
        when(kafkaTemplate.send(anyString(), anyString(), any(ProductEvent.class))).thenReturn(null);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
        verify(kafkaTemplate, times(1)).send(eq("product-topic"), anyString(), any(ProductEvent.class));
    }

    @Test
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
    }
}
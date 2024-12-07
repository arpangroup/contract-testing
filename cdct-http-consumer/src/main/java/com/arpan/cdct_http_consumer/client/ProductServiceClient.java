package com.arpan.cdct_http_consumer.client;

import com.arpan.cdct_http_consumer.exceptions.CustomException;
import com.arpan.cdct_http_consumer.model.DetailProductResponse;
import com.arpan.cdct_http_consumer.model.ProductCreateRequest;
import com.arpan.cdct_http_consumer.model.SimpleProductResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${serviceClients.products.baseUrl:http://localhost:8080/api/products}")
    @Setter
    private String baseUrl;

    public List<SimpleProductResponse> getProducts() { // RestClientException: HttpClientErrorException
        try {
            SimpleProductResponse[] productList = restTemplate.getForObject(baseUrl, SimpleProductResponse[].class);
            return Arrays.asList(productList);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Products not found: {}", e.getMessage());
            return Collections.emptyList();
        } catch (RestClientException e) {
            // Handle other errors (e.g., 500, 400)
            log.error("Error occurred: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch products", e);
        }
    }

    public @Nullable DetailProductResponse getProductById(@Nonnull String productId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + productId, DetailProductResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Product not found with ID {}: {}", productId + ": ", e.getMessage());
            return null;
        } catch (RestClientException e) {
            // Handle other errors (e.g., 500, 400)
            log.error("Error occurred while fetching product by ID {}: {}",productId, e.getMessage());
            throw new CustomException("Failed to fetch product", e);
        }

    }

    public @Nullable SimpleProductResponse createNewProduct(@Nonnull ProductCreateRequest productCreateRequest) {
        try {
            ResponseEntity<SimpleProductResponse> response = restTemplate.postForEntity(baseUrl, productCreateRequest, SimpleProductResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            // Handle specific error responses like 400 Bad Request
            log.error("Error creating product: {}", e.getMessage());
            return null;
        } catch (RestClientException e) {
            // Handle other errors
            log.error("Error occurred while creating product: {}", e.getMessage());
            throw new CustomException("Failed to create product", e);
        }
    }
}

package com.arpan.cdct_http_consumer.client;

import com.arpan.cdct_http_consumer.exceptions.CustomException;
import com.arpan.cdct_http_consumer.model.DetailProductResponse;
import com.arpan.cdct_http_consumer.model.ProductCreateRequest;
import com.arpan.cdct_http_consumer.model.SimpleProductResponse;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {
    private final RestTemplate restTemplate;

    //@Value("${serviceClients.products.baseUrl:http://localhost:8080/api/products}")
    private String baseUrl = "/api/products";

    public List<SimpleProductResponse> getAllProducts() { // RestClientException: HttpClientErrorException
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

    public DetailProductResponse getProductById(String productId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + productId, DetailProductResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Product not found with ID {}: {}", productId + ": ", e.getMessage());
            //return null;
            throw e;
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
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    public HttpEntity<Object> createEntityWithHeaders(Object request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 20xA1vQ2k3y");
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(request, headers);
    }
}

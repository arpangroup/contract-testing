package com.arpan.cdct_http_consumer.client;

import com.arpan.cdct_http_consumer.model.DetailProductResponse;
import com.arpan.cdct_http_consumer.model.ProductCreateRequest;
import com.arpan.cdct_http_consumer.model.SimpleProductResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${serviceClients.products.baseUrl:http://localhost:8080/api/products}")
    @Setter
    private String baseUrl;

    public List<SimpleProductResponse> getProducts() {
        SimpleProductResponse[] productList = restTemplate.getForObject(baseUrl, SimpleProductResponse[].class);
        return Arrays.asList(productList);
    }

    public DetailProductResponse getProductById(String productId) {
        return restTemplate.getForObject(baseUrl + "/" + productId, DetailProductResponse.class);
    }

    public SimpleProductResponse createNewProduct(ProductCreateRequest productCreateRequest) {
        return restTemplate.postForObject(baseUrl, productCreateRequest, SimpleProductResponse.class);
    }
}

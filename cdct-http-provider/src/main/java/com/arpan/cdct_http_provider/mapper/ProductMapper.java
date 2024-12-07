package com.arpan.cdct_http_provider.mapper;

import com.arpan.cdct_http_provider.model.Product;
import com.arpan.cdct_http_provider.model.ProductCreateRequest;
import com.arpan.cdct_http_provider.model.SimpleProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public SimpleProductResponse mapTo(Product product) {
        return new SimpleProductResponse(product.getProductId(), product.getProductName(), product.getPrice());
    }

    public Product mapTo(ProductCreateRequest request) {
        return new Product(null, request.getProductName(), request.getPrice(), null, null, false);
    }
}

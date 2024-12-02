package com.arpan.cdct_http_consumer.client;

import com.arpan.cdct_http_consumer.model.SimpleProductResponse;
import lombok.Data;

import java.util.List;

@Data
public class ProductServiceResponse {
    private List<SimpleProductResponse> products;
}

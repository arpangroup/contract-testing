package com.arpan.cdct_http_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DetailProductResponse extends SimpleProductResponse {
    private String type;
    private String version;

    public DetailProductResponse(String productId, String productName, int price) {
        super(productId, productName, price);
    }

    public DetailProductResponse(String productId, String productName, int price, String type, String version) {
        super(productId, productName, price);
        this.type = type;
        this.version = version;
    }
}

package com.arpan.cdct_http_provider.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SimpleProductResponse {
    private String productId;
    private String productName;
    private int price;

    protected void setProductId(String productId) {
        this.productId = productId;
    }
}

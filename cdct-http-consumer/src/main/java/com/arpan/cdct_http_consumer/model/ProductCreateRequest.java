package com.arpan.cdct_http_consumer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ProductCreateRequest {
    private String productName;
    private int price;
}

package com.arpan.cdct_http_consumer.model;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class SimpleProductResponse {
    private String productId;
    private String productName;
    private int price;
}

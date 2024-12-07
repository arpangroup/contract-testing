package com.arpan.cdct_http_consumer.model;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@ToString
public class DetailProductResponse extends SimpleProductResponse {
    private String type;
    private String version;
    private String createdAt;
    @Setter
    private String updatedAt;
    @Setter
    private boolean active;

    public DetailProductResponse(String productId, String productName, int price, String type, String version) {
        super(productId, productName, price);
        this.type = type;
        this.version = version;
        this.createdAt = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.updatedAt = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.active = false;
    }
}

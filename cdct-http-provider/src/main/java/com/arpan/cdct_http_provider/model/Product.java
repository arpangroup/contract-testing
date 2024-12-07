package com.arpan.cdct_http_provider.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Getter
@ToString
public class Product extends SimpleProductResponse {
    private String type;
    private String version;
    private String createdAt;
    @Setter
    private String updatedAt;
    @Setter
    private boolean active;

    public Product(String productId, String productName, int price, String type, String version, boolean active) {
        super(productId, productName, price);
        this.type = type == null ? "ELECTRONICS" : type;
        this.version = version == null ? "1.0" : version;
        this.createdAt = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.updatedAt = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.active = active;
    }

    public void setProductId(String productId) {
        super.setProductId(productId);
    }
}

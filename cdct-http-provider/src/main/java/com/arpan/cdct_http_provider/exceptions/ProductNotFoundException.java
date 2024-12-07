package com.arpan.cdct_http_provider.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ProductNotFoundException  extends RuntimeException {
    public ProductNotFoundException(String id) {
        super("Could not find product " + id);
    }
}
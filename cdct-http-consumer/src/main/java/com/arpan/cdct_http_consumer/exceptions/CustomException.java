package com.arpan.cdct_http_consumer.exceptions;

public class CustomException extends RuntimeException {
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}

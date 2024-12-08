package com.arpan.cdct_http_provider.controller;

import com.arpan.cdct_http_provider.exceptions.BadRequestException;
import com.arpan.cdct_http_provider.exceptions.ProductNotFoundException;
import com.arpan.cdct_http_provider.mapper.ProductMapper;
import com.arpan.cdct_http_provider.model.Product;
import com.arpan.cdct_http_provider.model.ProductCreateRequest;
import com.arpan.cdct_http_provider.model.SimpleProductResponse;
import com.arpan.cdct_http_provider.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @GetMapping()
    public ResponseEntity<List<SimpleProductResponse>> getAllProducts() {
       List<SimpleProductResponse> products = repository.findAll().stream().map(mapper::mapTo).toList();
       return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public Product getProductDetails(@PathVariable String productId) {
        if (!StringUtils.hasText(productId)) throw new BadRequestException();
        return repository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<Product> createNewProduct(@RequestBody ProductCreateRequest productCreateRequest) {
        Product productResponse = repository.save(mapper.mapTo(productCreateRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }
}

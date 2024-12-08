package com.arpan.cdct_http_provider.repository;

import com.arpan.cdct_http_provider.exceptions.BadRequestException;
import com.arpan.cdct_http_provider.exceptions.ProductNotFoundException;
import com.arpan.cdct_http_provider.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductRepository {
    private List<Product> products = new ArrayList<>();

    @PostConstruct
    public void init() {
        products.addAll(Arrays.asList(
                new Product("P101", "Product1", 500, null, null, false),
                new Product("P102", "Product2", 600, null, null, false),
                new Product("P103", "Product3", 700, null, null, false),
                new Product("P104", "Product4", 800, null, null, false),
                new Product("P105", "Product5", 900, null, null, false)
        ));
    }


    public List<Product> findAll() {
        return this.products;
    }

    public Optional<Product> findById(final String productId) {
        return this.products.stream().filter(p -> p.getProductId().equals(productId)).findFirst();
    }

    public Product save(Product product) {
        try {
           if (product.getProductId() == null) {
               product.setProductId("P10" + (this.products.size() + 1));
           }
            this.products.add(product);
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    public void deleteAll() {
        this.products = new ArrayList<>();
    }

    public void deleteById(String productId) {
        Optional<Product> product = products.stream().filter(p -> p.getProductId().equals(productId)).findFirst();
        product.ifPresent(value -> this.products.remove(value));
    }

    public void saveAll(List<Product> products) {
        this.products.addAll(products);
    }
}

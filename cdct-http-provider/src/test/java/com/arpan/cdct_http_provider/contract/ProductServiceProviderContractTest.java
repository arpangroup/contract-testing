package com.arpan.cdct_http_provider.contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.IgnoreMissingStateChange;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpan.cdct_http_provider.model.Product;
import com.arpan.cdct_http_provider.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.ByteBuffer;
import java.util.*;

import static com.arpan.cdct_http_provider.contract.ProductServiceProviderContractTest.PROVIDER_NAME_PRODUCT_SERVICE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider(PROVIDER_NAME_PRODUCT_SERVICE)
@PactFolder("src/test/resources/pacts")
@IgnoreMissingStateChange
@Slf4j
public class ProductServiceProviderContractTest {
    static final String CONSUMER_NAME__WEB_BROWSER = "WebBrowserConsumer";
    static final String PROVIDER_NAME_PRODUCT_SERVICE = "ProductServiceProvider";

    @LocalServerPort
    private int port;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context, HttpRequest request) {
        // WARNING: Do not modify anything else on the request, because you could invalidate the contract
        if (request.containsHeader("Authorization")) {
            request.setHeader("Authorization", "Bearer " + generateToken());
        }
        context.verifyInteraction();
    }

    @State(value = "products exists", action = StateChangeAction.SETUP)
    void productsExists() {
        log.info("Executing state: products exists");
        productRepository.deleteAll();
        productRepository.saveAll(Arrays.asList(
                new Product("P101", "Product1", 500, null, null, false),
                new Product("P201", "Product2", 600, null, null, false),
                new Product("P301", "Product3", 700, null, null, false),
                new Product("P401", "Product4", 800, null, null, false)
        ));
    }

    @State(value = "no product exists", action = StateChangeAction.SETUP)
    void noProductsExist() {
        productRepository.deleteAll();
    }

    @State(value = "product with ID P101 exists", action = StateChangeAction.SETUP)
    void productExists(Map<String, Object> params) {
        String productId = (String) params.get("id");
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            productRepository.save(new Product(productId, "Product", 500, null, null, false));
        }
    }

    @State(value = "product with ID P101 does not exists", action = StateChangeAction.SETUP)
    void productNotExist(Map<String, Object> params) {
        String productId = (String) params.get("id");
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            productRepository.deleteById(productId);
        }
    }

    private static String generateToken() {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(System.currentTimeMillis());
        return Base64.getEncoder().encodeToString(buffer.array());
    }
}

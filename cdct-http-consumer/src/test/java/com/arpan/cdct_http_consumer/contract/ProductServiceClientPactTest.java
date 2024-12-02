package com.arpan.cdct_http_consumer.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpan.cdct_http_consumer.service.ProductServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PactConsumerTest // Step1
//@ExtendWith(PactConsumerTestExt.class) // Step1-alternative
//@PactTestFor(providerName = "inventory-provider", pactVersion = V4) // Step3-class level alternative when there is only one consumer test
public class ProductServiceClientPactTest {
    @Autowired
    private ProductServiceClient productServiceClient;

    @Pact(consumer = "WebBrowserConsumer")
    public V4Pact allProducts(PactDslWithProvider builder) {
        // Define the expected response body using PactDslJsonBody
        /*PactDslJsonBody jsonBody = new PactDslJsonBody()
            .stringType("studentName", "John Doe")
            .stringType("studentId", "S12345")
            .integerType("age", 20);*/

        // Build the Pact interaction
        return builder
                .given("products exists") // State
                    .uponReceiving("get all products")
                    .path("/products")
                .willRespondWith()
                    .status(200)
                    .body(new PactDslJsonArray() //PactDslJsonBody not used here, as the API is directly returning list of products
                        .minArrayLike("products", 1, 2)
                        .integerType("id", 9L)
                        .stringType("name", "Gem Visa")
                        .stringType("type", "CREDIT_CARD")
                        .closeObject()
                        .closeArray()
                )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "allProducts", pactVersion = PactSpecVersion.V3)
    void testAllProducts(MockServer mockServer) {
        productServiceClient.setBaseUrl(mockServer.getUrl());
        List<Product> products = productServiceClient.fetchProducts().getProducts();
        assertThat(products, hasSize(2));
        assertThat(products.get(0), is(equalTo(new Product(9L, "Gem Visa", "CREDIT_CARD", null, null))));
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact singleProduct(PactDslWithProvider builder) {
        return builder
                .given("product with ID 10 exists", "id", 10)
                .uponReceiving("get product with ID 10")
                .path("/products/10")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody()
                                .integerType("id", 10L)
                                .stringType("name", "28 Degrees")
                                .stringType("type", "CREDIT_CARD")
                                .stringType("code", "CC_001")
                                .stringType("version", "v1")
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "singleProduct", pactVersion = PactSpecVersion.V3)
    void testSingleProduct(MockServer mockServer) {
        productServiceClient.setBaseUrl(mockServer.getUrl());
        Product product = productServiceClient.getProductById(10L);
        assertThat(product, is(equalTo(new Product(10L, "28 Degrees", "CREDIT_CARD", "v1", "CC_001"))));
    }
}

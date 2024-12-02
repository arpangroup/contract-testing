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
import com.arpan.cdct_http_consumer.client.ProductServiceClient;
import com.arpan.cdct_http_consumer.model.DetailProductResponse;
import com.arpan.cdct_http_consumer.model.SimpleProductResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@PactConsumerTest // Step1
//@ExtendWith(PactConsumerTestExt.class) // Step1-alternative
@PactTestFor(providerName = "ProductServiceProvider")
//@PactTestFor(providerName = "inventory-provider", pactVersion = V4) // Step3-class level alternative when there is only one consumer test
public class ProductServiceClientPactTest {
    @Autowired
    private ProductServiceClient productServiceClient;

    @Pact(consumer = "WebBrowserConsumer") // Step2
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
                    .method("GET")
                    .path("/api/products")
                    //.headers("Accept", "application/json")
                    //.headers(Map.of("Content-Type", "application/json"))
                .willRespondWith()
                    .status(200)
                    /*.body(new PactDslJsonBody() //PactDslJsonArray
                        .minArrayLike("products", 1, 2)
                            .integerType("productId", "P101")
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                            .closeObject()
                        .closeArray()
                    )*/
                    .body(new PactDslJsonArray()
                        .minArrayLike(2) // Ensures at least 2 objects in the array
                            .stringType("productId", "P101")
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                        .closeArray()
                    )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "allProducts", pactVersion = PactSpecVersion.V4) // Step3: either on Test class, or on the Test method
    void testAllProducts(MockServer mockServer) {
        // Step1.1: or define expectedJson like:
        List<SimpleProductResponse> expectedProducts = List.of(
                new SimpleProductResponse("P101", "Product1", 5000),
                new SimpleProductResponse("P102", "Product2", 6000)
        );

        // Step2: define the actualJson response
        // In a consumer test, you need to use the mock server URL (provided by Pact)
        // instead of the actual external endpoint URL like (http://myapi.com/api/products/P123)
        // The purpose of a Pact consumer test is to validate the contract
        // using a simulated server rather than making real API
        /*productServiceClient.setBaseUrl(mockServer.getUrl());
        List<SimpleProductResponse> products = productServiceClient.fetchProducts();
        assertThat(products, hasSize(2));
        assertThat(products.get(0), is(equalTo(new Product(9L, "Gem Visa", "CREDIT_CARD", null, null))));*/
        ResponseEntity<SimpleProductResponse[]> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products/P101", SimpleProductResponse[].class);
        List<SimpleProductResponse> actualProduct = Arrays.asList(productResponse.getBody());

        // Step3: Validate the response
        assertThat(productResponse.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(productResponse.getStatusCode().value()).isEqualTo(200);

        // validate Headers
        assertThat(productResponse.getHeaders().getContentType().toString()).contains("application/json");

        // validate ResponseBody
        assertThat(actualProduct).usingRecursiveComparison().isEqualTo(expectedProducts);
    }

    @Pact(consumer = "WebBrowserConsumer")
    public V4Pact singleProduct(PactDslWithProvider builder) {
        return builder
                .given("product with ID P101 exists", "id", "P101")
                .uponReceiving("get product with ID P101")
                    .method("GET")
                    .path("/api/products/P101")
                .willRespondWith()
                    .status(200)
                    .body(
                        new PactDslJsonBody()
                            .stringType("productId", "P101")
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                            .stringType("productType", "DEFAULT")
                            .stringType("version", "V-01")
                            .booleanType("active", true)
                    )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "singleProduct", pactVersion = PactSpecVersion.V4)
    void testSingleProduct(MockServer mockServer) {
        productServiceClient.setBaseUrl(mockServer.getUrl());
        DetailProductResponse product = productServiceClient.getProductById("P101");
        //assertThat(product, is(equalTo(new Product(10L, "28 Degrees", "CREDIT_CARD", "v1", "CC_001"))));
    }

    @Pact(consumer = "WebBrowserConsumer")
    public V4Pact createProduct(PactDslWithProvider builder) {
        return builder
                .given("create new product exists")
                .uponReceiving("create new product with productName and price")
                    .method("POST")
                    .path("/api/products")
                    //.matchHeader(CONTENT_TYPE, APPLICATION_JSON, APPLICATION_JSON_CHARSET_UTF_8)
                    .body(new PactDslJsonBody()
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                    )
                .willRespondWith()
                    .status(200)
                    .headers(Map.of("Content-Type", "application/json"))
                    .body(new PactDslJsonBody()
                        .stringType("productId", "P101")
                        .stringType("productName", "Product1")
                        .numberType("price", 500)
                    )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createProduct", pactVersion = PactSpecVersion.V4)
    void testCreateProduct(MockServer mockServer) {
        // Simulating the POST request & Validate the response
        ResponseEntity<SimpleProductResponse> postResponse = new RestTemplate().postForEntity(mockServer.getUrl() + "/api/products", productCreateRequest, SimpleProductResponse.class);
        assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
        assertThat(postResponse.getBody()).usingRecursiveComparison().isEqualTo(expectedProductCreateResponse);
    }
}

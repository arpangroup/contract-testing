package com.arpan.cdct_http_consumer.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpan.cdct_http_consumer.client.ProductServiceClient;
import com.arpan.cdct_http_consumer.exceptions.CustomException;
import com.arpan.cdct_http_consumer.model.DetailProductResponse;
import com.arpan.cdct_http_consumer.model.ProductCreateRequest;
import com.arpan.cdct_http_consumer.model.SimpleProductResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static au.com.dius.pact.core.model.PactSpecVersion.V4;
import static com.arpan.cdct_http_consumer.contract.ProductServiceClientContractTest.PROVIDER_NAME_PRODUCT_SERVICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@PactConsumerTest /* Step1 or, Step1-alternative ====> @ExtendWith(PactConsumerTestExt.class) */
@PactTestFor(providerName = PROVIDER_NAME_PRODUCT_SERVICE,pactVersion = V4) /* Step3-class level alternative when there is only one consumer test */
public class ProductServiceClientContractTest {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceClientContractTest.class);
    static final String CONSUMER_NAME__WEB_BROWSER = "WebBrowserConsumer";
    static final String PROVIDER_NAME_PRODUCT_SERVICE = "ProductServiceProvider";
    static final Map<String, String> HEADERS = Map.of("Content-Type", "application/json");
    private final String REGEX_BEARER_TOKEN = "Bearer (19|20)[a-zA-Z0-9]+";
    private final String REGEX_PRODUCT_ID = "^P\\d+$";
    private final String SAMPLE_BEARER_TOKEN = "Bearer 20xA1vQ2k3y";

    @Pact(consumer = CONSUMER_NAME__WEB_BROWSER)  // Step2
    public V4Pact getAllProducts(PactDslWithProvider builder) {
        // Define the expected response body using PactDslJsonBody
        PactDslJsonBody jsonBody = new PactDslJsonBody()
            .stringType("studentName", "John Doe")
            .stringType("studentId", "S12345")
            .integerType("age", 20);

        // Build the Pact interaction
        return builder
                .given("products exists") // State
                    .uponReceiving("get all products")
                    .method("GET")
                    .path("/api/products")
                    //.matchHeader("Authorization", REGEX_BEARER_TOKEN) // Regex to match "Bearer 19" or "20" followed by alphanumeric characters
                    //.headers("Accept", "application/json")
                    //.headers(Map.of("Content-Type", "application/json"))
                .willRespondWith()
                    .status(200)
                    .headers(HEADERS)
                    /*.body(new PactDslJsonBody() //PactDslJsonArray
                        .minArrayLike("products", 1, 2)  // name="products" size=1, numberExamples=2
                            .integerType("productId", "P101")
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                            .closeObject()
                        .closeArray()
                    )*/
                    /*.body(new PactDslJsonArray()
                        .minArrayLike(2) // Ensures at least 2 objects in the array
                            .stringMatcher("productId", "P101")
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                        .closeArray()
                    )*/
                    .body(LambdaDsl.newJsonArrayMinLike(2, array ->
                            array.object(object -> {
                                    object.stringType("productId", "P101");
                                    object.stringType("productName", "Product1");
                                    object.numberType("price", 500);
                            })
                    ).build())
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllProducts", pactVersion = V4) // Step3: either on Test class, or on the Test method
    void testGetAllProducts__whenProductsExists(MockServer mockServer) {
        System.out.println("Mock Provider Endpoint: " + mockServer.getUrl());
        // Step1.1: or define expectedJson like:
        List<SimpleProductResponse> expectedProducts = List.of(
                new SimpleProductResponse("P101", "Product1", 500),
                new SimpleProductResponse("P102", "Product2", 600)
        );

        // Step2: define the actualJson response & validate
        // In a consumer test, you need to use the mock server URL (provided by Pact)
        // instead of the actual external endpoint URL like (http://myapi.com/api/products/P123)
        // The purpose of a Pact consumer test is to validate the contract
        // using a simulated server rather than making real API
        /*ResponseEntity<SimpleProductResponse[]> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products", SimpleProductResponse[].class);
        List<SimpleProductResponse> actualProduct = Arrays.asList(productResponse.getBody());

        // Step3: Validate the response
         assertThat(productResponse.getStatusCode().is2xxSuccessful()).isTrue();
         assertThat(productResponse.getStatusCode().value()).isEqualTo(200);

        // validate Headers
         assertThat(productResponse.getHeaders().getContentType().toString()).contains("application/json");

        // validate ResponseBody
         assertThat(actualProduct).usingRecursiveComparison().isEqualTo(expectedProducts);*/

        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        List<SimpleProductResponse> actualProduct = new ProductServiceClient(restTemplate).getAllProducts();
        assertThat(actualProduct).hasSize(2);
        assertThat(actualProduct.getFirst()).isEqualTo(expectedProducts.getFirst());
    }

    @Pact(consumer = CONSUMER_NAME__WEB_BROWSER)
    public V4Pact noProductsExists(PactDslWithProvider builder) {
        return builder
                .given("no product exists")
                    .uponReceiving("get all products")
                    .method("GET")
                    .path("/api/products")
                    //.matchHeader("Authorization", REGEX_BEARER_TOKEN) // Regex to match "Bearer 19" or "20" followed by alphanumeric characters
                .willRespondWith()
                    .status(200)
                    .headers(HEADERS)
                    .body("[]")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "noProductsExists", pactVersion = V4)
    void testGetAllProducts__whenNoProductsExists(MockServer mockServer) {
        System.out.println("Mock Provider Endpoint: " + mockServer.getUrl());

        //ResponseEntity<SimpleProductResponse[]> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products", SimpleProductResponse[].class);
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        List<SimpleProductResponse> actualProduct = new ProductServiceClient(restTemplate).getAllProducts();

        assertEquals(Collections.emptyList(), actualProduct);
    }

    @Pact(consumer = CONSUMER_NAME__WEB_BROWSER)
    public V4Pact getProductDetailsById(PactDslWithProvider builder) {
        return builder
                .given("product with ID P101 exists", "id", "P101")
                .uponReceiving("get product with ID P101")
                    .method("GET")
                    .path("/api/products/P101")
                    //.matchHeader("Authorization", SAMPLE_BEARER_TOKEN)
                .willRespondWith()
                    .status(200)
                    .headers(HEADERS)
                    .body(
                        new PactDslJsonBody()
                                .stringMatcher("productId", REGEX_PRODUCT_ID, "P101") // Regex ensures it starts with 'P' followed by digits
                                .stringType("productName", "Product1")
                                .numberType("price", 500)
                                .stringMatcher("type", "ELECTRONICS|SPORTS|BEAUTY|FASHION", "ELECTRONICS") // Allowed values; use stringMatcher instead of stringType to match one of the allowed types
                                .stringMatcher("version", "\\d+\\.\\d", "1.0") // Numeric decimal with up to 1 digit after the decimal point
                                .stringMatcher("createdAt", "\\d{2}-\\d{2}-\\d{4}", "01-01-2025") // Ensures 'dd-MM-yyyy' format
                                .booleanType("active", true)
                    )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getProductDetailsById", pactVersion = V4)
    void testGetProductDetailsById__whenProductWithId_P101_Exists(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        DetailProductResponse expectedProduct = new ProductServiceClient(restTemplate).getProductById("P101");

        DetailProductResponse actualProduct = new DetailProductResponse("P101", "Product1", 500, "ELECTRONICS", "1.0", true);

        assertThat(actualProduct.getProductId()).isEqualTo(expectedProduct.getProductId());
        assertThat(actualProduct)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(expectedProduct);
    }

    @Pact(consumer = CONSUMER_NAME__WEB_BROWSER)
    public V4Pact productDetailsNotExist(PactDslWithProvider builder) {
        return builder
                .given("product with ID P101 does not exists", "id", "P101")
                    .uponReceiving("get product with ID P101")
                    .method("GET")
                    .path("/api/products/P101")
                    //.matchHeader("Authorization", SAMPLE_BEARER_TOKEN)
                .willRespondWith()
                    .status(HttpStatus.NOT_FOUND.value()) // 404
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "productDetailsNotExist", pactVersion = V4)
    void testGetProductDetailsById__whenProductWithId_P101_NotExists(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        ProductServiceClient productServiceClient = new ProductServiceClient(restTemplate);

        HttpClientErrorException e = assertThrows(
                HttpClientErrorException.class,
                () -> new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products/P101", DetailProductResponse.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode()); // 404
    }


    @Pact(consumer = CONSUMER_NAME__WEB_BROWSER)
    public V4Pact createProduct(PactDslWithProvider builder) {
        return builder
                .given("create new product exists")
                .uponReceiving("create new product with productName and price")
                    .method("POST")
                    .path("/api/products")
                    //.matchHeader("Authorization", "Bearer 20xA1vQ2k3y")
                    .matchHeader("Content-Type", "application/json")
                    .body(new PactDslJsonBody()
                            .stringType("productName", "Product1")
                            .numberType("price", 500)
                    )
                .willRespondWith()
                    .status(201) // HttpStatus.CREATED.value()
                    .headers(Map.of("Content-Type", "application/json"))
                    .body(new PactDslJsonBody()
                        .stringMatcher("productId", REGEX_PRODUCT_ID, "P12345") // Any productId starting with 'P'
                        .stringType("productName", "Product1")
                        .numberType("price", 500)
                    )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createProduct", pactVersion = V4)
    void testCreateProduct(MockServer mockServer) {
        SimpleProductResponse expectedProductCreateResponse = new SimpleProductResponse("P001", "Product1", 500);

        ProductCreateRequest productCreateRequest = new ProductCreateRequest("Product1", 500);
        //HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", "Bearer 20xA1vQ2k3y");
        //headers.set("Content-Type", "application/json");
        //HttpEntity<Object> requestEntity = new HttpEntity<>(productCreateRequest, headers);

        //ResponseEntity<SimpleProductResponse> actualProductCreateResponse = new RestTemplate().postForEntity(mockServer.getUrl() + "/api/products", productCreateRequest, SimpleProductResponse.class);
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        SimpleProductResponse actualProductCreateResponse = new ProductServiceClient(restTemplate).createNewProduct(productCreateRequest);


        // Validate the productId matches the expected pattern (starts with 'P')
        assert actualProductCreateResponse != null;
        assertThat(actualProductCreateResponse.getProductId()).matches(REGEX_PRODUCT_ID); // This checks that productId starts with 'P'

        assertThat(actualProductCreateResponse)
                .usingRecursiveComparison()
                .ignoringFields("productId")
                .isEqualTo(expectedProductCreateResponse);
    }


    /*@Pact(consumer = CONSUMER_NAME__WEB_BROWSER)
    public V4Pact allProductsNoAuthToken(PactDslWithProvider builder) {
        return builder
                .given("product exists")
                    .uponReceiving("get all products with no auth token")
                    .method("GET")
                    .path("/api/products")
                .willRespondWith()
                    .status(HttpStatus.UNAUTHORIZED.value()) // 401
                    .headers(HEADERS)
                    .body("{\"error:\": \"Unauthorized\"}")
                    .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "allProductsNoAuthToken", pactVersion = V4)
    void testGetAllProducts__whenNoAuth(MockServer mockServer) {
        System.out.println("Mock Provider Endpoint: " + mockServer.getUrl());
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();

        HttpClientErrorException e = assertThrows(
                HttpClientErrorException.class,
                //() -> new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products", SimpleProductResponse[].class)
                () -> new ProductServiceClient(restTemplate).getAllProducts()
        );
        assertEquals(HttpStatus.UNAUTHORIZED.value(), e.getStatusCode().value()); //401
    }*/

}

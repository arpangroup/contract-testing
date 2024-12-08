# Pact Maven + Springboot + JUnit5 workshop

## Introduction

**Workshop outline**:

- [step 1: **create consumer**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step1#step-1---simple-consumer-calling-provider): Create our consumer before the Provider API even exists
- [step 2: **unit test**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step2#step-2---client-tested-but-integration-fails): Write a unit test for our consumer
- [step 3: **pact test**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step3#step-3---pact-to-the-rescue): Write a Pact test for our consumer
- [step 4: **pact verification**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step4#step-4---verify-the-provider): Verify the consumer pact with the Provider API
- [step 5: **fix consumer**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step5#step-5---back-to-the-client-we-go): Fix the consumer's bad assumptions about the Provider
- [step 6: **pact test**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step6#step-6---consumer-updates-contract-for-missing-products): Write a pact test for `404` (missing User) in consumer
- [step 7: **provider states**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step7#step-7---adding-the-missing-states): Update API to handle `404` case
- [step 8: **pact test**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step8#step-8---authorization): Write a pact test for the `401` case
- [step 9: **pact test**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step9#step-9---implement-authorisation-on-the-provider): Update API to handle `401` case
- [step 10: **request filters**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step10#step-10---request-filters-on-the-provider): Fix the provider to support the `401` case
- [step 11: **pact broker**](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step11#step-11---using-a-pact-broker): Implement a broker workflow for integration with CI/CD

_NOTE: Each step is tied to, and must be run within, a git branch, allowing you to progress through each stage incrementally. For example, to move to step 2 run the following: `git checkout step2`_


## Requirements

- JDK 17 or above
- Maven 3+
- Docker for step 11

## Scenario

There are two components in scope for our workshop.

1. Product Catalog website (**Consumer**). It provides an interface to query the Product service for product information.
1. Product Service (**Provider**). Provides useful things about products, such as listing all products and getting the details of an individual product.

## Step 1 - Simple Consumer calling Provider
We need to first create an HTTP client to make the calls to our provider service:

Lets mock a providers API response, we are not consuming the whole response, instead we will consume a partial response
````bash
curl GET http://localhost:8080/api/products/P101 | python -m json.tool
````
````json
{
    "productId": "P101",
    "productName": "Product1",
    "price": 500,
    "productType": "DEFAULT",
    "version": "V-01",
    "active": true
}
````
Let's create a service which will call the actual endpoint.
````java
@Service
public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${serviceClients.products.baseUrl:http://localhost:8080/api/products}")
    private String baseUrl;

    public List<SimpleProductResponse> getProducts() {
        SimpleProductResponse[] productList = restTemplate.getForObject(baseUrl, SimpleProductResponse[].class);
        return Arrays.asList(productList);
    }

    public DetailProductResponse getProductById(String productId) {
        return restTemplate.getForObject(baseUrl + "/" + productId, DetailProductResponse.class);
    }
    
    public SImple getProductById(String productId) {
        return restTemplate.getForObject(baseUrl + "/" + productId, DetailProductResponse.class);
    }
}
````

we need to build the app and install the dependencies. Run the following in the consumer sub-directory:

````console
cdct-http-consumer ❯ ./mvnw clean install
````
We can run the app with 
````console
java -jar target/cdct-http-consumer-0.0.1-SNAPSHOT.jar --server.port=9090
or
mvn spring-boot:run
````
Accessing the URL for the app in the browser gives us a 500 error page as the downstream service is not running.
You will also see an exception in the Springboot console output.

*Move on to [step 2](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/tree/step2#step-2---client-tested-but-integration-fails)*



## Step 2 - Contract Test using Pact
Unit tests are written and executed in isolation of any other services. When we write tests for code that talk to other services, they are built on trust that the contracts are upheld. There is no way to validate that the consumer and provider can communicate correctly.

> An integration contract test is a test at the boundary of an external service verifying that it meets the contract expected by a consuming service — [Martin Fowler](https://martinfowler.com/bliki/IntegrationContractTest.html)

### Step 2.1. Pact Consumer Dependencies:
````xml
<!-- Pact Consumer Dependency -->
<dependency>
    <groupId>au.com.dius.pact.consumer</groupId>
    <artifactId>junit5</artifactId>
    <version>4.6.5</version>
    <scope>test</scope>
</dependency>
````


Let us add Pact to the project and write a consumer pact test for the GET /products/{id} endpoint.

Provider states is an important concept of Pact that we need to introduce. These states help define the state that the provider should be in for specific interactions. For the moment, we will initially be testing the following states:
- products exists
- no product exists
- product with ID P101 exists
- product with ID P101 does not exists
- create new product


The consumer can define the state of an interaction using the `given` property.

ProductServiceClientPactTest.java
````java
@PactConsumerTest /* Step1 or, Step1-alternative ====> @ExtendWith(PactConsumerTestExt.class) */
@PactTestFor(providerName = PROVIDER_NAME_PRODUCT_SERVICE,pactVersion = V4) /* Step3-class level alternative when there is only one consumer test */
public class ProductServiceClientContractTest {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceClientContractTest.class);
    private RestTemplate restTemplate;

    static final String CONSUMER_NAME__WEB_BROWSER = "WebBrowserConsumer";
    static final String PROVIDER_NAME_PRODUCT_SERVICE = "ProductServiceProvider";
    static final Map<String, String> HEADERS = Map.of("Content-Type", "application/json");
    private final String REGEX_BEARER_TOKEN = "Bearer (19|20)[a-zA-Z0-9]+";
    private final String REGEX_PRODUCT_ID = "^P\\d+$";
    private final String SAMPLE_BEARER_TOKEN = "Bearer 20xA1vQ2k3y";

    @BeforeEach
    public void setup(MockServer mockServer) {
        log.info("Mock Provider Endpoint: {}", mockServer.getUrl());
        restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
    }

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
            .willRespondWith()
                .status(200)
                .headers(HEADERS)
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
        // Step1.1: or define expectedJson like:
        List<SimpleProductResponse> expectedProducts = List.of(
                new SimpleProductResponse("P101", "Product1", 500),
                new SimpleProductResponse("P102", "Product2", 600)
        );

        // Step2: define the actualJson response & validate
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
            .willRespondWith()
                .status(200)
                .headers(HEADERS)
                .body("[]")
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "noProductsExists", pactVersion = V4)
    void testGetAllProducts__whenNoProductsExists(MockServer mockServer) {
        /*ResponseEntity<SimpleProductResponse[]> productResponse = new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products", SimpleProductResponse[].class);*/
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
            .willRespondWith()
                .status(200)
                .headers(HEADERS)
                .body(new PactDslJsonBody()
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
            .willRespondWith()
                .status(HttpStatus.NOT_FOUND.value()) // 404
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "productDetailsNotExist", pactVersion = V4)
    void testGetProductDetailsById__whenProductWithId_P101_NotExists(MockServer mockServer) {
        HttpClientErrorException e = assertThrows(
                HttpClientErrorException.class,
                () -> new RestTemplate().getForEntity(mockServer.getUrl() + "/api/products/P101", DetailProductResponse.class)
        );
        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode()); // 404
    }


    @Pact(consumer = CONSUMER_NAME__WEB_BROWSER)
    public V4Pact createProduct(PactDslWithProvider builder) {
        return builder
            .given("create new product")
                .uponReceiving("create new product with productName and price")
                .method("POST")
                .path("/api/products")
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
        /*ResponseEntity<SimpleProductResponse> actualProductCreateResponse = new RestTemplate().postForEntity(mockServer.getUrl() + "/api/products", productCreateRequest, SimpleProductResponse.class);*/
        SimpleProductResponse actualProductCreateResponse = new ProductServiceClient(restTemplate).createNewProduct(productCreateRequest);


        // Validate the productId matches the expected pattern (starts with 'P')
        assert actualProductCreateResponse != null;
        assertThat(actualProductCreateResponse.getProductId()).matches(REGEX_PRODUCT_ID); // This checks that productId starts with 'P'

        assertThat(actualProductCreateResponse)
                .usingRecursiveComparison()
                .ignoringFields("productId")
                .isEqualTo(expectedProductCreateResponse);
    }

}
````
These tests starts a mock server on a random port that acts as our provider service. To get this to work we update the URL in the ProductServiceClient to point to the mock server that Pact provides for the test.

Running this test will create a pact file which we can use to validate our assumptions on the provider side, and have conversation around.

````console
cdct-http-consumer ❯ ./mvnw verify

[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------

<<< Omitted >>>

[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.449 s
[INFO] Finished at: 2024-12-08T02:34:33+05:30
[INFO] ------------------------------------------------------------------------
````
A pact file should have been generated in `cdct-http-consumer/target/pacts/WebBrowserConsumer-ProductServiceProvider.json`

[View the generated WebBrowserConsumer-ProductServiceProvider.json](diagrams/WebBrowserConsumer-ProductServiceProvider.json)

*Move on to [step 3](https://github.com/arpangroup/contract-testing/tree/cdct-step3?tab=readme-ov-file#step-3---verify-the-provider)*


## Step 3 - Verify the provider
We will need to copy the Pact contract file that was produced from the consumer test into the Provider module. This will help us verify that the provider can meet the requirements as set out in the contract.

Copy the contract located in `cdct-http-consumer/target/pacts/ProductCatalogue-ProductService.json` to `cdct-http-provider/src/test/resources/pacts/WebBrowserConsumer-ProductServiceProvider.json`.

### Step 3.1. Pact Consumer Dependencies:
````xml
<!-- Pact Provider Dependency -->
<dependency>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>junit5</artifactId>
    <version>4.6.5</version>
    <scope>test</scope>
</dependency>
````


### Step 3.2. Provider Contract Test
Now let's make a start on writing a Pact test to validate the consumer contract:

In `cdct-http-provider/src/test/java/io/pact/workshop/product_service/ProductServiceProviderContractTest.java`:


````java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider(PROVIDER_NAME_PRODUCT_SERVICE)
@PactFolder("src/test/resources/pacts")
@IgnoreMissingStateChange
public class ProductServiceProviderContractTest {
    static final String CONSUMER_NAME__WEB_BROWSER = "WebBrowserConsumer";
    static final String PROVIDER_NAME_PRODUCT_SERVICE = "ProductServiceProvider";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}

````
This is a Springboot test that starts the app on a random port, and then injects the port into the test class. We then setup the test context using some annotations on the test class that tells the Pact framework who the provider is and where the pact files are. We also set the test target to point to the running app.

We now need to validate the pact generated by the consumer is valid by running the test, which should fail:

````console
cdct-http-provider ❯ ./mvnw verify

<<< Omitted >>>

Verifying a pact between WebBrowserConsumer and ProductServiceProvider
  [Using File src\test\resources\pacts\WebBrowserConsumer-ProductServiceProvider.json]
  Given product with ID P101 exists
  get product with ID P101

  Test Name: com.arpan.cdct_http_consumer.contract.ProductServiceClientContractTest.testGetProductDetailsById__whenProductWithId_P101_Exists(MockServer)

  Comments:

    returns a response which
      has status code 200 (OK)
      has a matching body (OK)
2024-12-08T11:21:51.356+05:30  WARN 25708 --- [cdct-http-provider] [           main] a.c.d.p.p.DefaultTestResultAccumulator   : Not all of the 3 were verified. The following were missing:
2024-12-08T11:21:51.356+05:30  WARN 25708 --- [cdct-http-provider] [           main] a.c.d.p.p.DefaultTestResultAccumulator   :     get all products
2024-12-08T11:21:51.357+05:30  WARN 25708 --- [cdct-http-provider] [           main] rificationStateChangeExtension$Companion : Did not find a test class method annotated with @State("product with ID P101 exists") 
for Interaction "get product with ID P101" 
with Consumer "WebBrowserConsumer"
2024-12-08T11:21:51.363+05:30  WARN 25708 --- [cdct-http-provider] [           main] rificationStateChangeExtension$Companion : Did not find a test class method annotated with @State("products exists") 
for Interaction "get all products" 
with Consumer "WebBrowserConsumer"

Verifying a pact between WebBrowserConsumer and ProductServiceProvider
  [Using File src\test\resources\pacts\WebBrowserConsumer-ProductServiceProvider.json]
  Given products exists
  get all products

  Test Name: com.arpan.cdct_http_consumer.contract.ProductServiceClientContractTest.testGetAllProducts__whenProductsExists(MockServer)

  Comments:

    returns a response which
      has status code 200 (OK)
      has a matching body (OK)
2024-12-08T11:21:51.414+05:30  WARN 25708 --- [cdct-http-provider] [           main] a.c.d.p.p.DefaultTestResultAccumulator   : Skipping publishing of verification results as it has been disabled (pact.verifier.publishResults is not 'true')
2024-12-08T11:21:51.414+05:30  WARN 25708 --- [cdct-http-provider] [           main] rificationStateChangeExtension$Companion : Did not find a test class method annotated with @State("products exists") 
for Interaction "get all products" 
with Consumer "WebBrowserConsumer"
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.321 s -- in com.arpan.cdct_http_provider.contract.ProductServiceProviderContractTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] 
````
Yay - green ✅!

*Move on to [step 4](https://github.com/arpangroup/contract-testing/tree/cdct-step4?tab=readme-ov-file#step-3---verify-the-provider)*

## Step 4 - Consumer updates contract for missing products
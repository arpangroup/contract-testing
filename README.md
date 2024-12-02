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


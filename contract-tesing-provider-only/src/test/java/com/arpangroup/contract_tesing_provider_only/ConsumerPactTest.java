package com.arpangroup.contract_tesing_provider_only;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpangroup.contract_tesing_provider_only.controller.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
public class ConsumerPactTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pact(provider = "ProviderService", consumer = "ConsumerService")
    public V4Pact createPact(PactDslWithProvider builder) throws IOException{
        File file = ResourceUtils.getFile("src/test/resources/UserResponse200.json");
        String content = new String(Files.readAllBytes(file.toPath()));

        // Define the expected response body
//        PactDslJsonBody responseBody = new PactDslJsonBody()
//                .booleanType("condition", true)
//                .stringType("name", "tom");

//        PactDslJsonBody responseBody = new PactDslJsonBody("");

        // Build the Pact interaction
        return builder
                .given("GET /api/getUserDetails")
                .uponReceiving("A GET request for user details")
                .method("GET")
                .path("/api/getUserDetails")
                .willRespondWith()
                .status(200)
                .body(content, "application/json")
                .toPact(V4Pact.class);
    }

    /*@Pact(provider = "ProviderService", consumer = "ConsumerService")
    public V4Pact createPact(PactDslWithProvider builder) throws IOException {
        File file = ResourceUtils.getFile("src/test/resources/UserResponse200.json");
        String content = new String(Files.readAllBytes(file.toPath()));

        PactDslJsonBody responseBody = new PactDslJsonBody()
                .booleanType("condition", true)
                .stringType("name", "tom");

        return builder
                .given("GET /api/getUserDetails")
                    .uponReceiving("GET REQUEST")
                    .path("/api/getUserDetails")
                    .method("GET")
                .willRespondWith()
                    .status(200)
//                    .headers(Map.of("Content-Type", "application/json"))
//                    .body(new PactDslJsonBody()
//                            .booleanType("condition", true)
//                            .stringType("name", "tom"))
                .body(responseBody)
                .toPact(V4Pact.class);

        //

    }*/

/*    @Pact(provider = "ProviderService", consumer = "ConsumerService")
    public V4Pact createPact(PactBuilder builder) {
        return builder
                .usingLegacyMessageDsl(false)
                .expectsToReceive("GET REQUEST")
                .withRequest(request -> request
                        .path("/api/pact")
                        .method("GET"))
                .willRespondWith(response -> response
                        .status(200)
                        .headers(Map.of("Content-Type", "application/json"))
                        .body(new PactDslJsonBody()
                                .booleanType("condition", true)
                                .stringType("name", "tom")))
                .toPact();
    }*/

    /*@Pact(provider = "ProviderService", consumer = "ConsumerService")
    public V4Pact createPact(PactBuilder builder) throws IOException {
        PactDslJsonBody responseBody = new PactDslJsonBody()
                .booleanType("condition", true)
                .stringType("name", "tom");


        return builder
                .usingLegacyDsl()
                .given("")
                    .uponReceiving("GET REQUEST")
                    .path("/api/getUserDetails")
                    .method("GET")
                .willRespondWith()
                    .status(200)
//                    .headers(Map.of("Content-Type", "application/json"))
//                    .body(new PactDslJsonBody()
//                            .booleanType("condition", true)
//                            .stringType("name", "tom"))
//                .body(responseBody)
                .body(
                        """
                           {
                            "condition": true,
                            "name": "tom"
                          }
                        """.trim().replace("\t", "")
                )
                .toPact(V4Pact.class);

        //

    }*/


    @Test
    @PactTestFor(providerName = "ProviderService")
    void testConsumer(MockServer mockServer) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Make GET request to the mock provider
        //ResponseEntity<UserResponse> response = restTemplate.getForEntity("http://localhost:9091/api/getUserDetails", UserResponse.class);
        //UserResponse actualResponse = response.getBody();

        // Validate the response
//        UserResponse expectedResponse = new UserResponse(true, "tom");
//        assertThat(response.getStatusCodeValue()).isEqualTo(200);
//        assertThat(response.getBody()).isEqualTo("{\"condition\":true,\"name\":\"tom\"}");
//        assertThat(actualResponse).isEqualTo(expectedResponse);

//        assertThat(actualResponse.isCondition()).isEqualTo(expectedResponse.isCondition());
//        assertThat(actualResponse.getName()).isEqualTo(expectedResponse.getName());

//        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);


        String mockServerUrl = mockServer.getUrl(); // Get the Pact mock server URL dynamically
//        mockServerUrl = "http://localhost:9091/";
        ResponseEntity<UserResponse> response = restTemplate.getForEntity(mockServerUrl + "/api/getUserDetails", UserResponse.class);


        // Expected response
        UserResponse expectedResponse = new UserResponse(true, "tom");

        // Validate the response
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    private String loadJsonFromFile(String filePath) throws IOException {
        // Read the JSON file as a string
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }
}

package com.arpangroup.contract_tesing_consumer.contract;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.arpangroup.contract_tesing_consumer.controller.DemoController;
import com.arpangroup.contract_tesing_consumer.dto.DemoDto;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * We’re passing the provider name and host on which the server mock (which is created from the contract) will be started.
 */
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProviderService", hostInterface="localhost", port = "8081")
public class ConsumerApiContractTest {

    @Pact(consumer = "ConsumerService")
    public V4Pact createPact(PactDslWithProvider builder) throws Exception {
        File file = ResourceUtils.getFile("src/test/java/com/arpangroup/contract_tesing_consumer/contract/response/DemoSuccessReponse200.json");
        String content = new String(Files.readAllBytes(file.toPath()));

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");


        return builder
                .given("test GET")
                    .uponReceiving("GET REQUEST")
                    .path("/api/pact")
                    //.matchHeader("Authorization", ".*", "Bearer abcdef")
                    .method("GET")
                /*.willRespondWith()
                    .status(200)
                    .headers(headers)
                    //.body("{\"condition\": true, \"name\": \"tom\"}"));
                    .body(content, "application/json")
                .toPact(V4Pact.class);*/
                .willRespondWith()
                    .status(200)
                    //.body("{\"condition\": true, \"name\": \"tom\"}"));
                    .body(content, "application/json")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(providerName = "ProviderService")
    void testConsumer(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplate();

        // Make the actual request to the mock server
        ResponseEntity<DemoDto> response = restTemplate.getForEntity(mockServer.getUrl() + "/api/pact", DemoDto.class);

        // Verify that the response matches the contract expectations
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

/*    @Test
    @PactTestFor(providerName = "ProviderService")
    void testConsumer(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/api/pact", String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        //assertThat(response.getBody()).contains("Sample Data");
        assertThat(response.getHeaders().get("Content-Type").contains("application/json")).isTrue();
        assertThat(response.getBody()).contains("condition", "true", "name", "tom");
    }*/

    /*@Test
    @PactTestFor(pactMethod = "createPact")
    public void givenGet_whenSendRequest_shouldReturn200WithProperHeaderAndBody(MockServer mockServer) {
        // when
        ResponseEntity<String> response = new RestTemplate().getForEntity(mockServer.getUrl() + "/pact", String.class);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getHeaders().get("Content-Type").contains("application/json")).isTrue();
        assertThat(response.getBody()).contains("condition", "true", "name", "tom");

        // and
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // when
        //String jsonBody = "{\"name\": \"Michael\"}";
        //ResponseEntity<String> postResponse = new RestTemplate().exchange(mockServer.getUrl() + "/pact", HttpMethod.POST, new HttpEntity<>(jsonBody, httpHeaders), String.class);

        // then
        //assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
    }*/
}

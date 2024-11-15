package com.arpangroup.contract_tesing_provider;


import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest
@Provider("ProviderService")
//@PactFolder("target/pacts")
//@PactFolder("contracts/pacts")
//@PactFolder("D:\\java-projects\\contract-testing\\contract-tesing-provider\\contracts\\pacts")
//@PactBroker(url = "http://localhost:9292")
//@PactBroker(url = "", authentication = @PactBrokerAuth(token = ""))
@PactFolder("src/test/resources/pacts")
public class ProviderPactTestOld {
//    @MockBean
//    private DemoController dataController;


    @LocalServerPort
    private int port;

    // Initialize Pact context before running tests
    @BeforeEach
    void setup(PactVerificationContext context) {
        // Set the target for Pact verification
        context.setTarget(new HttpTestTarget("localhost", port));

        // Mock the response for the `getPactData` method
//        DemoDto demoDto = new DemoDto(true, "tom");
//        when(dataController.getPactData()).thenReturn(ResponseEntity.ok(demoDto));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTest(PactVerificationContext context) {
        // Verify the interactions based on the Pact file
        context.verifyInteraction();
    }

    @State("default state")
    public void setupDefaultState() {
        // Set up the state (mock data, database setup, etc.)
        // Set up mock data or perform necessary preparations
        System.out.println("Setting up default state...");
    }

    @State("user exists")
    public void userExistsState() {
        System.out.println("Setting up user exists state...");
        // Add any necessary setup (e.g., populate database, mock services).
    }


}

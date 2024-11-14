package com.arpangroup.contract_tesing_provider.contract;


import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpangroup.contract_tesing_provider.controller.DemoController;
import com.arpangroup.contract_tesing_provider.controller.DemoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@WebMvcTest
@Provider("ProviderService")
//@PactFolder("target/pacts")
//@PactFolder("contracts/pacts")
@PactFolder("D:\\java-projects\\contract-tesing-consumer\\target\\pacts")
//@PactBroker(url = "", authentication = @PactBrokerAuth(token = ""))
public class ProviderPactTest {
    @MockBean
    private DemoController dataController;

    // Initialize Pact context before running tests
    @BeforeEach
    void before(PactVerificationContext context) {
        // Set the target for Pact verification
        context.setTarget(new HttpTestTarget("localhost", 8080));

        // Mock the response for the `getPactData` method
        DemoDto demoDto = new DemoDto(true, "tom");
        when(dataController.getPactData()).thenReturn(ResponseEntity.ok(demoDto));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTest(PactVerificationContext context) {
        // Verify the interactions based on the Pact file
        context.verifyInteraction();
    }



}

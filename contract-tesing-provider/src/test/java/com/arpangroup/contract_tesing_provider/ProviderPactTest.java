package com.arpangroup.contract_tesing_provider;


import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.arpangroup.contract_tesing_provider.controller.DemoController;
import com.arpangroup.contract_tesing_provider.controller.DemoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("provider-service")
@PactFolder("src/test/resources/pacts")
public class ProviderPactTest {


    @LocalServerPort
    private int port;

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
//    @PactVerification
    void pactVerificationTest(PactVerificationContext context) {
        context.verifyInteraction();
    }

    /**
     * State setup method for "default state".
     * This method will be called before the test if the contract specifies this state.
     */
    @State("default state")
    public void setupDefaultState() {
        System.out.println("Setting up default state...");
        // Mock data setup or API stubbing can be done here.
    }

    /**
     * State setup method for "user exists".
     */
    @State("user exists")
    public void userExistsState() {
        System.out.println("Setting up user exists state...");
        // Add any necessary setup (e.g., populate database, mock services).
    }
}

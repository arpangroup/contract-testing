# Pact Maven + Springboot + JUnit5 workshop

## Introduction

**Workshop outline**:

- [step 1: **create consumer**](https://github.com/arpangroup/contract-testing/tree/cdct-step1?tab=readme-ov-file#step-1---simple-consumer-calling-provider): Create our consumer before the Provider API even exists
- [step 2: **pact test**](https://github.com/arpangroup/contract-testing/tree/cdct-step2?tab=readme-ov-file#step-2---contract-test-using-pact): Write a Pact test for our consumer
- [step 3: **pact verification**](https://github.com/arpangroup/contract-testing/tree/cdct-step3?tab=readme-ov-file#step-3---verify-the-provider): Verify the provider
- [step 4: **pact test**](https://github.com/arpangroup/contract-testing/tree/cdct-step4?tab=readme-ov-file#step-4---consumer-updates-contract-for-missing-products): Write a pact test for `404` (missing User) in consumer
- [step 5: **provider states**](https://github.com/arpangroup/contract-testing/tree/cdct-step5?tab=readme-ov-file#step-5---adding-the-missing-states): Update Provider to handle `404` case
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

## Start

*Start at [step 1](https://github.com/arpangroup/contract-testing/tree/cdct-step1?tab=readme-ov-file#step-1---simple-consumer-calling-provider)*



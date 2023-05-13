package com.birdapp.BirdApp;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ContainerBase {

    @Container
    public static GenericContainer mongoDBContainer = new GenericContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void mongoProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
        registry.add("spring.data.mongodb.database", () -> "Bird");
    }

    @BeforeAll
    public static void beforeAll() {
        mongoDBContainer.start();
    }
}
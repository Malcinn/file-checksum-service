package com.company.filechecksumservice.infrastructure.r2dbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
public class R2DBCFileRepositoryTest {

    @Autowired
    R2DBCFileRepository repository;


    @Test
    void readsAllEntitiesCorrectly() {
        repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void readsEntitiesByNameCorrectly() {

        repository.findById(1L)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }
}

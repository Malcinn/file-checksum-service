package com.company.filechecksumservice.infrastructure.r2dbc;

import com.company.filechecksumservice.domain.File;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Testcontainers
@DataR2dbcTest
public class R2DBCFileRepositoryTest {

    @Autowired
    R2DBCFileRepository repository;

    @Container
    static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("file-checksum-file-repository")
                .withUsername("testUser")
                .withPassword("testPassword");
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        ConnectionFactoryOptions options = PostgreSQLR2DBCDatabaseContainer.getOptions(postgresContainer);
        registry.add("spring.r2dbc.url", () ->
                String.format("r2dbc:postgresql://%s:%s/%s",
                        options.getRequiredValue(ConnectionFactoryOptions.HOST),
                        options.getRequiredValue(ConnectionFactoryOptions.PORT),
                        options.getRequiredValue(ConnectionFactoryOptions.DATABASE)));
        registry.add("spring.r2dbc.username", postgresContainer::getUsername);
        registry.add("spring.r2dbc.password", postgresContainer::getPassword);

        registry.add("spring.liquibase.url", postgresContainer::getJdbcUrl);
        registry.add("spring.liquibase.user", postgresContainer::getUsername);
        registry.add("spring.liquibase.password", postgresContainer::getPassword);
    }

    @Test
    void fileRepositorySave_shouldCreateEntityWithPropertiesPopulated() {
        Mono<File> fileMono = repository.save(createTestFile());
        File file = fileMono.block();

        Assertions.assertNotNull(file);
        Assertions.assertNotNull(file.getId());
        Assertions.assertNotNull(file.getName());
        Assertions.assertNotNull(file.getSize());
        Assertions.assertNotNull(file.getChecksum());
        repository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void fileRepositoryFindById_ShouldReturnEntity() {
        Mono<File> fileMono = repository.save(createTestFile());
        File file = fileMono.block();

        Assertions.assertNotNull(file);
        Assertions.assertNotNull(file.getId());
        repository.findById(file.getId())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void fileRepositorySave_ShouldUpdateEntityPropertiesForAlreadyExistingEntity() {
        Mono<File> fileMono = repository.save(createTestFile());
        File file = fileMono.block();

        Assertions.assertNotNull(file);
        file.setName("test_file_new_name");
        File changed = repository.save(file).block();

        Assertions.assertNotNull(changed);
        Assertions.assertNotNull(file.getId());
        Assertions.assertEquals(file.getId(), changed.getId());
        Assertions.assertEquals("test_file_new_name", changed.getName());
        repository.findById(changed.getId())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void fileRepositoryDelete_shouldRemoveEntityFromDataSource() {
        Mono<File> fileMono = repository.save(createTestFile());
        File file = fileMono.block();

        Assertions.assertNotNull(file);
        Assertions.assertNotNull(file.getId());
        repository.delete(file).then().block();

        repository.findById(file.getId())
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();

    }

    private File createTestFile() {
        String fileContent = "Test file content";
        return new File(null, "test_file_name", (long) fileContent.getBytes().length, fileContent);
    }

}

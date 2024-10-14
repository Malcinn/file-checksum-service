package com.company.filechecksumservice;

import com.company.filechecksumservice.configuration.ApplicationConfiguration;
import com.company.filechecksumservice.infrastructure.r2dbc.R2DBCFileRepository;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.util.function.Consumer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(ApplicationConfiguration.class)
public class FileChecksumServiceIntegrationTest {

    private final static String FILE_API = "/api/file";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private R2DBCFileRepository repository;

    @Container
    static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("file-checksum-file-integration-repository")
                .withUsername("testUser")
                .withPassword("testPassword");
        postgresContainer.start();
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
    public void singleFileUploadFileShouldBeCreated() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("single_test.txt"));

        webTestClient.post().uri(FILE_API)
                .headers(apiConsumerCredentials())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(201);
                });

        StepVerifier.create(repository.findByNameContaining("single_"))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void multipleFileUploadFilesShouldBeCreated() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("multi_test.txt"));
        builder.part("files", new ClassPathResource("multi_Domain_Driven_Design_Quickly.pdf"));

        webTestClient.post().uri(FILE_API)
                .headers(apiConsumerCredentials())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(201);
                });

        StepVerifier.create(repository.findByNameContaining("multi_"))
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    private Consumer<HttpHeaders> apiConsumerCredentials() {
        return (httpHeaders) -> httpHeaders.setBasicAuth("user", "password");
    }
}

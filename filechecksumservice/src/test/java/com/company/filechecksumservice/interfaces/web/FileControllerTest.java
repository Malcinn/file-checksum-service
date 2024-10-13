package com.company.filechecksumservice.interfaces.web;

import com.company.filechecksumservice.interfaces.facade.FileFacade;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.function.Consumer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("tc")
public class FileControllerTest {

    private final static String FILE_API = "/api/file";

    @MockBean
    private FileFacade fileFacade;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void shouldReturn400WhenMissingFilesParam() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        webTestClient.post().uri(FILE_API)
                .headers(apiConsumerCredentials())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(400);
                });
    }

    @Test
    public void shouldReturn401WhenRequestIsNotAuthorized() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("test.txt"));

        webTestClient.post().uri(FILE_API)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(401);
                });
    }

    @Test
    public void shouldReturn201WhenSingleFileIsUploaded() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("test.txt"));

        webTestClient.post().uri(FILE_API)
                .headers(apiConsumerCredentials())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(201);
                });
    }

    @Test
    public void shouldReturn201WhenMultipleFilesAreUploaded() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("test.txt"));
        builder.part("files", new ClassPathResource("Domain_Driven_Design_Quickly.pdf"));

        webTestClient.post().uri(FILE_API)
                .headers(apiConsumerCredentials())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(201);
                });
    }

    @Test
    public void shouldReturn500WhenFileProcessingFailed() {
        Mockito.doThrow(new RuntimeException("Test runtime exception")).when(fileFacade).load(Mockito.any(FilePart.class));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", new ClassPathResource("test.txt"));

        webTestClient.post().uri(FILE_API)
                .headers(apiConsumerCredentials())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectAll(responseSpec -> {
                    responseSpec.expectStatus().isEqualTo(500);
                });
    }

    private Consumer<HttpHeaders> apiConsumerCredentials() {
        return (httpHeaders) -> httpHeaders.setBasicAuth("user", "password");
    }
}

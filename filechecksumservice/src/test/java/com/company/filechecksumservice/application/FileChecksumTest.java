package com.company.filechecksumservice.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HexFormat;

@ExtendWith(SpringExtension.class)
public class FileChecksumTest {

    @InjectMocks
    private FileChecksum underTests;

    @Test
    public void shouldReturnErrorIfFilePartIsEmpty() {
        StepVerifier.create(underTests.calculate(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void shouldReturnErrorIfMessageDigestAlgorithmDoesNotExist() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("test.txt");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("test_name");

        FileChecksum localUnderTests = new FileChecksum("NOT_EXISTING_ALGORITHM");
        StepVerifier.create(localUnderTests.calculate(filePart))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void shouldReturnChecksumIfFilePartIsComplete_smallSizedFile() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("test.txt");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("test_name");

        StepVerifier.create(underTests.calculate(filePart))
                .consumeNextWith(bytes -> {
                    Assertions.assertNotNull(bytes);
                    Assertions.assertEquals("056f5ca5dbd6a49717737a6e4ede4f78", HexFormat.of().formatHex(bytes));
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldReturnChecksumIfFilePartIsComplete_mediumSizedFile() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("Domain_Driven_Design_Quickly.pdf");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("Domain driven design quickly");

        StepVerifier.create(underTests.calculate(filePart))
                .consumeNextWith(bytes -> {
                    Assertions.assertNotNull(bytes);
                    Assertions.assertEquals("32a53356475e20d135c9c48d15123418", HexFormat.of().formatHex(bytes));
                })
                .expectComplete()
                .verify();
    }
}

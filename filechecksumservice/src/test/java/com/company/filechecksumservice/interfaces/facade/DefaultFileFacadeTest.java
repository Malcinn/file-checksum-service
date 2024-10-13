package com.company.filechecksumservice.interfaces.facade;

import com.company.filechecksumservice.application.Checksum;
import com.company.filechecksumservice.application.FileService;
import com.company.filechecksumservice.domain.File;
import com.company.filechecksumservice.infrastructure.file.FileStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class DefaultFileFacadeTest {

    @InjectMocks
    private DefaultFileFacade underTests;

    @Mock
    private Checksum checksum;

    @Mock
    private FileService fileService;

    @Mock
    private FileStorage fileStorage;


    @Test
    public void shouldReturnErrorIfFilePartIsEmpty() {
        StepVerifier.create(underTests.load(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void shouldReturnFileIfFilePartIsComplete() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("test.txt");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("test_name");
        Mockito.when(fileService.save(Mockito.any())).thenReturn(Mono.just(new File()));
        Mockito.when(fileStorage.store(Mockito.anyString(), Mockito.any())).thenReturn(Mono.empty());

        StepVerifier.create(underTests.load(filePart))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldReturnErrorIfFileServiceFinishWithError() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("test.txt");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("test_name");
        Mockito.when(fileService.save(Mockito.any())).thenReturn(Mono.error(IllegalArgumentException::new));

        StepVerifier.create(underTests.load(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}

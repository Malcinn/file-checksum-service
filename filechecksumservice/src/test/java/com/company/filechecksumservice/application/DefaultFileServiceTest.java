package com.company.filechecksumservice.application;

import com.company.filechecksumservice.application.Checksum;
import com.company.filechecksumservice.application.DefaultFileService;
import com.company.filechecksumservice.domain.File;
import com.company.filechecksumservice.domain.FileRepository;
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
public class DefaultFileServiceTest {

    @InjectMocks
    private DefaultFileService underTests;

    @Mock
    private Checksum checksum;

    @Mock
    private FileRepository fileRepository;

    @Test
    public void shouldReturnErrorIfFilePartIsEmpty() {
        StepVerifier.create(underTests.save(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void shouldReturnErrorIfRepositoryThrowError() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("test.txt");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("test_name");
        Mockito.when(checksum.calculate(filePart)).thenReturn(Mono.just("32a53356475e20d135c9c48d15123418".getBytes()));
        Mockito.when(fileRepository.store(Mockito.any(File.class))).thenThrow(new IllegalArgumentException());

        StepVerifier.create(underTests.save(filePart))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void shouldReturnFileIfFilePartIsComplete_smallSizedFile() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("test.txt");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("test_name");
        Mockito.when(checksum.calculate(filePart)).thenReturn(Mono.just("32a53356475e20d135c9c48d15123418".getBytes()));
        Mockito.when(fileRepository.store(Mockito.any(File.class))).thenReturn(Mono.just(new File()));

        StepVerifier.create(underTests.save(filePart))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void shouldReturnFileIfFilePartIsComplete_mediumSizedFile() {
        FilePart filePart = Mockito.mock(FilePart.class);
        Resource resource = new ClassPathResource("Domain_Driven_Design_Quickly.pdf");
        Flux<DataBuffer> fileContent = DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 1024);
        Mockito.when(filePart.content()).thenReturn(fileContent);
        Mockito.when(filePart.name()).thenReturn("Domain driven design quickly");
        Mockito.when(checksum.calculate(filePart)).thenReturn(Mono.just("32a53356475e20d135c9c48d15123418".getBytes()));
        Mockito.when(fileRepository.store(Mockito.any(File.class))).thenReturn(Mono.just(new File()));

        StepVerifier.create(underTests.save(filePart))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }
}

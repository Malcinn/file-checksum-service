package com.company.filechecksumservice.interfaces.facade;

import com.company.filechecksumservice.application.FileService;
import com.company.filechecksumservice.infrastructure.file.FileStorage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.Objects;

@AllArgsConstructor
public class DefaultFileFacade implements FileFacade {

    private final Logger LOGGER = LoggerFactory.getLogger(DefaultFileFacade.class);

    private final FileService fileService;

    private final FileStorage fileStorage;

    @Override
    public Mono<Void> load(FilePart part) {
        if (Objects.isNull(part)) {
            return Mono.error(() -> {
                LOGGER.error("filePart param is empty");
                return new IllegalArgumentException("filePart param is empty");
            });
        }
        return fileService.save(part)
                .then(part.content().map(DataBuffer::asInputStream)
                        .doOnNext(inputStream -> fileStorage.store("", inputStream)).then());
    }

}

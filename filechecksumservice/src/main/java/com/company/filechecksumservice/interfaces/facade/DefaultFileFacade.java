package com.company.filechecksumservice.interfaces.facade;

import com.company.filechecksumservice.application.FileService;
import com.company.filechecksumservice.infrastructure.file.FileStorage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class DefaultFileFacade implements FileFacade {

    private final FileService fileService;

    private final FileStorage fileStorage;

    @Override
    public Mono<Void> load(FilePart part) {
        return fileService.save(part)
                .then(part.content().map(DataBuffer::asInputStream)
                        .doOnNext(inputStream -> fileStorage.store("", inputStream)).then());
    }

}

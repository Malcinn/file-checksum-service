package com.company.filechecksumservice.interfaces.web;

import com.company.filechecksumservice.interfaces.facade.FileFacade;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private final FileFacade fileFacade;

    public FileController(FileFacade fileFacade) {
        this.fileFacade = fileFacade;
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('API_CONSUMER')")
    public Mono<Void> load(@RequestPart("files") Flux<FilePart> filePartFlux) {
        LOGGER.info("Load file executed");
        return filePartFlux
                .doOnNext(fileFacade::load)
                .doOnError(throwable -> LOGGER.error("Error occurred during loading file, message: {} ", throwable.getMessage()))
                .thenEmpty(Subscriber::onComplete);

    }


}

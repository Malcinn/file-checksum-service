package com.company.filechecksumservice.interfaces.web;

import com.company.filechecksumservice.interfaces.facade.FileFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    // do not forget to secure endpoints
    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<String>> load(@RequestPart("files") Flux<FilePart> filePartFlux) {
        return filePartFlux
                .doOnNext(fileFacade::load)
                .doOnError(throwable -> LOGGER.error("Error occurred during loading file, message: {} ", throwable.getMessage()))
                .then(Mono.just(
                        ResponseEntity.status(HttpStatusCode.valueOf(200))
                                .body(HttpStatus.OK.getReasonPhrase()))
                );
    }


}

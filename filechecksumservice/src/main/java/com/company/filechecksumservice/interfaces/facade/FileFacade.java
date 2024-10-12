package com.company.filechecksumservice.interfaces.facade;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileFacade {

    Mono<Void> load(FilePart part);
}

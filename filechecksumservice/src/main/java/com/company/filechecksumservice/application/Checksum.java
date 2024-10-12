package com.company.filechecksumservice.application;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface Checksum {

    Mono<byte[]> calculate(FilePart part);
}

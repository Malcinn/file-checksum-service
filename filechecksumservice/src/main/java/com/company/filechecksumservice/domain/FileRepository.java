package com.company.filechecksumservice.domain;

import reactor.core.publisher.Mono;

public interface FileRepository {

    Mono<File> store(File file);
}

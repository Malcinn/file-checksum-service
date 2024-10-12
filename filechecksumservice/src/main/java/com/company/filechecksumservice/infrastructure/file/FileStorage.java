package com.company.filechecksumservice.infrastructure.file;

import reactor.core.publisher.Mono;

import java.io.InputStream;

public interface FileStorage {

    Mono<Void> store(String location, InputStream content);
}

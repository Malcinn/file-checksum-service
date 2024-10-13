package com.company.filechecksumservice.infrastructure.file;

import reactor.core.publisher.Mono;

import java.io.InputStream;

public class DefaultFileStorage implements FileStorage {
    @Override
    public Mono<Void> store(String location, InputStream content) {
        return Mono.empty();
    }
}

package com.company.filechecksumservice.infrastructure.r2dbc;

import com.company.filechecksumservice.domain.File;
import com.company.filechecksumservice.domain.FileRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface R2DBCFileRepository extends FileRepository, R2dbcRepository<File, Long> {

    @Override
    default Mono<File> store(File file) {
        return save(file);
    }

    Flux<File> findByNameContaining(String name);
}

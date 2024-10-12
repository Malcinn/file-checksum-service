package com.company.filechecksumservice.application;

import com.company.filechecksumservice.domain.File;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface FileService {

    Mono<File> save(FilePart filePart);
}

package com.company.filechecksumservice.application;

import com.company.filechecksumservice.domain.File;
import com.company.filechecksumservice.domain.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.HexFormat;
import java.util.Objects;

public class DefaultFileService implements FileService {

    private final Logger LOGGER = LoggerFactory.getLogger(DefaultFileService.class);

    private final Checksum checksum;

    private final FileRepository fileRepository;

    public DefaultFileService(Checksum checksum, FileRepository fileRepository) {
        this.checksum = checksum;
        this.fileRepository = fileRepository;
    }

    @Override
    public Mono<File> save(FilePart filePart) {
        LOGGER.info("Inside of {}", DefaultFileService.class.getName());
        if (Objects.isNull(filePart)) {
            return Mono.error(() -> {
                LOGGER.error("filePart param is empty");
                return new IllegalArgumentException("filePart param is empty");
            });
        }
        Mono<File> fileMono = Mono.zip(checksum.calculate(filePart), getFileSize(filePart))
                .flatMap(tuple -> fileRepository.store(new File(null, filePart.filename(), tuple.getT2(), HexFormat.of().formatHex(tuple.getT1()))))
                .doOnNext(file -> LOGGER.info("File created: {}", file.toString()));
        fileMono.subscribe();
        return fileMono;
    }

    private Mono<Long> getFileSize(FilePart part) {
        return part.content().map(dataBuffer -> (long) dataBuffer.readableByteCount())
                .reduce(0L, Long::sum);
    }
}

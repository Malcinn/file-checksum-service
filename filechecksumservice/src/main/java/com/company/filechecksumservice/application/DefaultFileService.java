package com.company.filechecksumservice.application;

import com.company.filechecksumservice.domain.File;
import com.company.filechecksumservice.domain.FileRepository;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.HexFormat;

public class DefaultFileService implements FileService {

    private final Checksum checksum;

    private final FileRepository fileRepository;

    public DefaultFileService(Checksum checksum, FileRepository fileRepository) {
        this.checksum = checksum;
        this.fileRepository = fileRepository;
    }

    @Override
    @Transactional
    public Mono<File> save(FilePart filePart) {
        return checksum.calculate(filePart)
                .zipWith(getFileSize(filePart), (checksum, fileSize) ->
                        new File(null, filePart.name(), fileSize, HexFormat.of().formatHex(checksum)))
                .map(fileRepository::store)
                .flatMap(file -> file);
    }

    private Mono<Long> getFileSize(FilePart part) {
        return part.content().map(DataBuffer::readableByteCount).reduce(0L, Long::sum);
    }
}

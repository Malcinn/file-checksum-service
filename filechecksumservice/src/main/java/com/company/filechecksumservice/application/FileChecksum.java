package com.company.filechecksumservice.application;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class FileChecksum implements Checksum {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileChecksum.class);

    private static final String DEFAULT_ALGORITHM = "MD5";

    private final String algorithm;

    public FileChecksum(String algorithm) {
        this.algorithm = StringUtils.isNotBlank(algorithm) ? algorithm : DEFAULT_ALGORITHM;
    }

    @Override
    public Mono<byte[]> calculate(FilePart part) {
        LOGGER.info("Inside of {}", FileChecksum.class.getName());
        if (Objects.isNull(part)) {
            return Mono.error(() -> {
                LOGGER.error("filePart param is empty");
                return new IllegalArgumentException("filePart param is empty");
            });
        }
        return part.content()
                .map(dataBuffer -> {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                        byte[] buffer = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(buffer);
                        messageDigest.update(buffer);
                        return messageDigest.digest();
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }).reduce((a, b) -> {
                    try {
                        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
                        messageDigest.update(a);
                        return messageDigest.digest(b);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}

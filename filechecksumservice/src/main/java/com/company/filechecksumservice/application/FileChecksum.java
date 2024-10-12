package com.company.filechecksumservice.application;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileChecksum implements Checksum {

    private static final String DEFAULT_ALGORITHM = "MD5";

    private final String algorithm;

    public FileChecksum(String algorithm) {
        this.algorithm = StringUtils.isNotBlank(algorithm) ? algorithm : DEFAULT_ALGORITHM;
    }

    @Override
    public Mono<byte[]> calculate(FilePart part) {
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

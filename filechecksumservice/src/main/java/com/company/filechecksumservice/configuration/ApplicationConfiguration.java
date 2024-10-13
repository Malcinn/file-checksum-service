package com.company.filechecksumservice.configuration;

import com.company.filechecksumservice.application.Checksum;
import com.company.filechecksumservice.application.DefaultFileService;
import com.company.filechecksumservice.application.FileChecksum;
import com.company.filechecksumservice.application.FileService;
import com.company.filechecksumservice.domain.FileRepository;
import com.company.filechecksumservice.infrastructure.file.DefaultFileStorage;
import com.company.filechecksumservice.infrastructure.file.FileStorage;
import com.company.filechecksumservice.interfaces.facade.DefaultFileFacade;
import com.company.filechecksumservice.interfaces.facade.FileFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.company.filechecksumservice.infrastructure.r2dbc")
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {

    @Autowired
    Environment env;

    @Bean
    public Checksum checksum() {
        return new FileChecksum(env.getProperty("checksum.algorithm"));
    }

    @Bean
    public FileService fileService(Checksum checksum, FileRepository fileRepository) {
        return new DefaultFileService(checksum, fileRepository);
    }

    @Bean
    public FileStorage fileStorage() {
        return new DefaultFileStorage();
    }

    @Bean
    public FileFacade fileFacade(FileService fileService, FileStorage fileStorage) {
        return new DefaultFileFacade(fileService, fileStorage);
    }
}

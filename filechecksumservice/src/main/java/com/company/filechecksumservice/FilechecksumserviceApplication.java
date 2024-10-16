package com.company.filechecksumservice;

import com.company.filechecksumservice.configuration.ApplicationConfiguration;
import com.company.filechecksumservice.interfaces.web.FileSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApplicationConfiguration.class, FileSecurityConfiguration.class})
public class FilechecksumserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilechecksumserviceApplication.class, args);
    }

}

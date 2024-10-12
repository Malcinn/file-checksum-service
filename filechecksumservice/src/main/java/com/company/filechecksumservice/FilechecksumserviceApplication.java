package com.company.filechecksumservice;

import com.company.filechecksumservice.configuration.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.company.filechecksumservice.infrastructure.r2dbc")
@Import(ApplicationConfiguration.class)
public class FilechecksumserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilechecksumserviceApplication.class, args);
	}

}

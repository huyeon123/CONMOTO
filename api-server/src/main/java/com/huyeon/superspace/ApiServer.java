package com.huyeon.superspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableJpaAuditing
@EnableMongoAuditing
@SpringBootApplication
public class ApiServer {
    public static void main(String[] args) {
        SpringApplication.run(ApiServer.class, args);
    }

}

package com.zipjung.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ZipjungBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZipjungBackendApplication.class, args);
    }

}

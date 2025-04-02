package com.booksphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for BookSphere.
 */
@SpringBootApplication
@EnableJpaAuditing
public class BookSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookSphereApplication.class, args);
    }
} 
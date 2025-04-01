package com.booksphere;

import com.booksphere.model.Role;
import com.booksphere.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the BookSphere application.
 * This class bootstraps and launches the Spring Boot application.
 */
@SpringBootApplication
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    /**
     * Initialize default roles in the system.
     * 
     * @param roleRepository The role repository
     * @return A CommandLineRunner that creates the default roles
     */
    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Create default roles if they don't exist
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("USER", "Regular user role with basic permissions"));
                roleRepository.save(new Role("ADMIN", "Administrative role with full system access"));
                roleRepository.save(new Role("AUTHOR", "Content creator role for book authors"));
                System.out.println("Default roles created successfully.");
            }
        };
    }
}

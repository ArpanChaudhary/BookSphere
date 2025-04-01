package com.booksphere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Security configuration for the BookSphere application.
 * Configures authentication, authorization, and password encryption.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for H2 console
            .csrf(csrf -> csrf.disable())
            // Allow frames for H2 console
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authorizeHttpRequests(authorize -> authorize
                // Public resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/", "/index", "/register", "/login").permitAll()
                // H2 console
                .requestMatchers("/h2-console/**").permitAll()
                // Admin resources
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Author resources
                .requestMatchers("/author/**").hasRole("AUTHOR")
                // User resources (also accessible by admins and authors)
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN", "AUTHOR")
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/error/403")
            );
            
        return http.build();
    }
}

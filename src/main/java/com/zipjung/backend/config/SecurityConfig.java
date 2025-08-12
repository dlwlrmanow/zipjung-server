package com.zipjung.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api-test").permitAll()
                    .requestMatchers("/focus-log/save").permitAll()
                    .requestMatchers("/focus-log/fetch").permitAll()
                    .requestMatchers("/focus-time/fetch/**").permitAll()
                    .requestMatchers("/focus-time/save").permitAll()
                    .requestMatchers("/focus-time/today/fetch").permitAll()
                    .requestMatchers("/focus-time/list/fetch").permitAll()
                    .anyRequest().authenticated()
            );

        return http.build();
    }
}

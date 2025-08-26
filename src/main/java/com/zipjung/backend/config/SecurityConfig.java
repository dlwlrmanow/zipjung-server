package com.zipjung.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                    .requestMatchers("/focus-log/delete/**").permitAll()
                    .requestMatchers("/focus-time/fetch/**").permitAll()
                    .requestMatchers("/focus-time/save").permitAll()
                    .requestMatchers("/focus-time/today/fetch").permitAll()
                    .requestMatchers("/focus-time/list/fetch").permitAll()
                    .requestMatchers("/user/join/**").permitAll()
                    .anyRequest().authenticated()
            );

        http
            .formLogin(login -> login
                    .loginProcessingUrl("/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .successHandler((request, response, authentication) -> {
                        // 로그인 성공 시 JSON 응답 반환
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"status\": \"success\", \"message\": \"로그인 성공!\"}");
                    })
                    .failureHandler((request, response, exception) -> {
                        // 로그인 실패 시 JSON 응답 반환
                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        response.getWriter().write("{\"status\": \"failure\", \"message\": \"로그인 실패ㅠ\"}");
                    })
                    // TODO: 로그아웃 구현
                    // TODO: JWT 구현
                    .permitAll());

        return http.build();
    }

    @Bean // 비밀번호 암호화해주는 bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

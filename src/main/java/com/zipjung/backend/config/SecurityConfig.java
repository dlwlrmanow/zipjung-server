package com.zipjung.backend.config;

import com.zipjung.backend.security.JwtAccessDeniedHandler;
import com.zipjung.backend.security.JwtAuthenticationEntryPoint;
import com.zipjung.backend.security.JwtFilter;
import com.zipjung.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean // 비밀번호 암호화해주는 bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 제외
                    .formLogin(AbstractHttpConfigurer::disable).logout(AbstractHttpConfigurer::disable) // formLogin은 Session방식이라서 JWT와 충돌

                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api-test").permitAll()
//                    .requestMatchers("/focus-log/save").permitAll()
                    .requestMatchers("/focus-log/fetch").permitAll()
                    .requestMatchers("/focus-log/delete/**").permitAll()
                    .requestMatchers("/focus-time/fetch/**").permitAll()
//                    .requestMatchers("/focus-time/save").permitAll()
                    .requestMatchers("/focus-time/today/fetch").permitAll()
                    .requestMatchers("/focus-time/list/fetch").permitAll()
                    .requestMatchers("/user/login", "/user/join/**").permitAll()
                    .anyRequest().authenticated())

                .exceptionHandling(ex -> ex
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    )

                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();

//        http
//            .formLogin(login -> login
//                    .loginProcessingUrl("/login")
//                    .usernameParameter("username")
//                    .passwordParameter("password")
//                    .successHandler((request, response, authentication) -> {
//                        // 로그인 성공 시 JSON 응답 반환
//                        response.setContentType("application/json");
//                        response.setCharacterEncoding("UTF-8");
//                        response.getWriter().write("{\"status\": \"success\", \"message\": \"로그인 성공!\"}");
//                    })
//                    .failureHandler((request, response, exception) -> {
//                        // 로그인 실패 시 JSON 응답 반환
//                        response.setContentType("application/json");
//                        response.setCharacterEncoding("UTF-8");
//                        response.getWriter().write("{\"status\": \"failure\", \"message\": \"로그인 실패ㅠ\"}");
//                    })
//                    .permitAll());
    }
}

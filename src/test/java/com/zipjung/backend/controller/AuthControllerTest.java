package com.zipjung.backend.controller;

import com.zipjung.backend.security.CustomUserDetails;
import com.zipjung.backend.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserDetailsService userDetailsService() {
            // 테스트용 가짜 유저를 리턴하는 서비스
            return username -> new CustomUserDetails(
                    1L,
                    "testUser",
                    "password",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }
    }

    @Test
    @WithUserDetails("testUser")// 위에서 만든 가짜 user
    void logout_Success() throws Exception {
        mockMvc.perform(post("/auth/logout")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(jwtTokenProvider).deleteRefreshToken(anyString());
    }

}

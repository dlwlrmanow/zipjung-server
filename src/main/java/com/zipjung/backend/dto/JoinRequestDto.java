package com.zipjung.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class JoinRequestDto {
    private String username;
    private String password;
    private String email;
}

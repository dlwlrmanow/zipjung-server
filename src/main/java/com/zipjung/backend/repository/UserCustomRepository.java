package com.zipjung.backend.repository;

import com.zipjung.backend.dto.RegisterDto;
import com.zipjung.backend.dto.UserDto;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCustomRepository {
    // 중복 회원, 중복 username 조회
    Long areYouNew(RegisterDto registerDto);
    // 회원 가입
//    boolean registerUser(RegisterDto registerDto);
}

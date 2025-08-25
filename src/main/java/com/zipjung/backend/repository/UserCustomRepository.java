package com.zipjung.backend.repository;

import com.zipjung.backend.dto.JoinRequestDto;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCustomRepository {
    // 중복 회원, 중복 username 조회
    Long areYouNew(JoinRequestDto joinRequestDto);
}

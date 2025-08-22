package com.zipjung.backend.repository;

import com.zipjung.backend.dto.RegisterDto;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCustomRepository {
    Long areYouNew(RegisterDto registerDto);
}

package com.zipjung.backend.repository;

import com.zipjung.backend.dto.JoinRequestDto;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCustomRepository {
    Long areYouNew(JoinRequestDto joinRequestDto);
}

package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.dto.FocusLogDto;
import com.zipjung.backend.entity.QFocusTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FocusLogSaveCustomRepositoryImpl implements FocusLogSaveCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
}

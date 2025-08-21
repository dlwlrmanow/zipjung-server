package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FocusLogSaveCustomRepositoryImpl implements FocusLogSaveCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
}

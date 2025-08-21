package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.dto.RegisterDto;
import com.zipjung.backend.entity.QProfile;
import com.zipjung.backend.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long areYouNew(RegisterDto registerDto) {
        QProfile profile = QProfile.profile;
        QUser user = QUser.user;
        // email에서 가입한 적이 있는 지 조회
        Long isExistUser = jpaQueryFactory
                .select(profile.count())
                .from(profile)
                .where(profile.email.eq(registerDto.getEmail()))
                .fetchOne();
        if (isExistUser > 0) {
            return 2L;
        }
        // 존재하지 않는 이메일
        // 중복 username 조회
        Long isExistUsername = jpaQueryFactory
                .select(profile.count())
                .from(user)
                .where(user.username.eq(registerDto.getUsername()))
                .fetchOne();
        if (isExistUsername > 0) {
            return 3L;
        }
        return 0L;
    }

}

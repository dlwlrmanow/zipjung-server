package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.dto.JoinRequestDto;
import com.zipjung.backend.entity.QProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long areYouNew(JoinRequestDto joinRequestDto) {
        QProfile profile = QProfile.profile;
//        QMember
        // email에서 가입한 적이 있는 지 조회
        Long isExistMember = jpaQueryFactory
                .select(profile.count())
                .from(profile)
                .where(profile.email.eq(joinRequestDto.getEmail()))
                .fetchOne();
        if (isExistMember > 0) {
            return 2L;
        }
        // 존재하지 않는 이메일
        // 중복 username 조회
//        Long isExistUsername = jpaQueryFactory
//                .select(profile.count())
//                .from(member)
//                .where(member.username.eq(registerDto.getUsername()))
//                .fetchOne();
//        if (isExistUsername > 0) {
//            return 3L;
//        }
        return 0L;
    }

}

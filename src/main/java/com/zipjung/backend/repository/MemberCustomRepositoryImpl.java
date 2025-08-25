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
    public boolean areYouNew(JoinRequestDto joinRequestDto) {
        QProfile profile = QProfile.profile;

        Long isExistMember = jpaQueryFactory
                .select(profile.count())
                .from(profile)
                .where(profile.email.eq(joinRequestDto.getEmail()))
                .fetchOne();

        if (isExistMember > 0) {
            return true;
        }

        return false;
    }

}

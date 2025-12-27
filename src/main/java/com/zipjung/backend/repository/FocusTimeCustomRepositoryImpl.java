package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.entity.QFocusLog;
import com.zipjung.backend.entity.QFocusTime;
import com.zipjung.backend.entity.QLocation;
import com.zipjung.backend.entity.QPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FocusTimeCustomRepositoryImpl implements FocusTimeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean isLocationExist(Long focusTimeId) {
        log.info("Checking location existence for focusTimeId: {}", focusTimeId);

        QPost post = QPost.post;
        QFocusTime focusTime = QFocusTime.focusTime;
        QLocation location = QLocation.location;
        QFocusLog focusLog = QFocusLog.focusLog;

        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(location)
                .join(focusLog).on(location.postId.eq(focusLog.postId)) // postId로 연결
                .join(focusTime).on(focusLog.id.eq(focusTime.focusLogId))
                .where(
                        focusTime.id.eq(focusTimeId),
                        location.isDeleted.eq(false),
                        focusLog.isDeleted.eq(false),
                        focusTime.isDeleted.eq(false)
                )
                .fetchFirst(); // 있으면 1 반환

        return fetchOne != null;
    }
}

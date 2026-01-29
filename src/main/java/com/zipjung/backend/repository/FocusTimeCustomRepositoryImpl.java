package com.zipjung.backend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zipjung.backend.dto.FocusTimeWithLocationDto;
import com.zipjung.backend.dto.QFocusTimeWithLocationDto;
import com.zipjung.backend.entity.QFocusLog;
import com.zipjung.backend.entity.QFocusTime;
import com.zipjung.backend.entity.QLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FocusTimeCustomRepositoryImpl implements FocusTimeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean isLocationExist(Long focusTimeId) {
        log.info("Checking location existence for focusTimeId: {}", focusTimeId);

        QFocusTime focusTime = QFocusTime.focusTime;
        QLocation location = QLocation.location;
        QFocusLog focusLog = QFocusLog.focusLog;

        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(location)
                .join(focusTime).on(location.focusLogId.eq(focusTime.focusLogId))
                .join(focusLog).on(focusTime.focusLogId.eq(focusLog.id))
                .where(
                        focusTime.id.eq(focusTimeId),
                        location.isDeleted.eq(false),
                        focusLog.isDeleted.eq(false),
                        focusTime.isDeleted.eq(false)
                )
                .fetchFirst(); // 있으면 1 반환

        return fetchOne != null;
    }

    @Override
    public List<FocusTimeWithLocationDto> getFocusTimeWithLocationDtoList(LocalDateTime startOfDay, LocalDateTime endOfDay, Long memberId){
        QFocusTime focusTime = QFocusTime.focusTime;
        QLocation location = QLocation.location;

        List<FocusTimeWithLocationDto> focusTimeWithLocation = jpaQueryFactory
                .select(new QFocusTimeWithLocationDto(
                        focusTime.id,
                        focusTime.focusedTime,
                        focusTime.startFocusTime,
                        focusTime.endFocusTime,
                        focusTime.focusLogId,
                        location.isDeleted
                ))
                .from(focusTime)
                .leftJoin(location)
                .on(focusTime.focusLogId.eq(location.focusLogId)
                        .and(location.isDeleted.eq(false))) // where절의 조건은 만족하지만 on절의 and는 만족하지 못하는 경우 null로 출력
                .where(
                        focusTime.createdAt.between(startOfDay, endOfDay),
                        focusTime.isDeleted.eq(false),
                        focusTime.memberId.eq(memberId)
                )
                .fetch();

        return focusTimeWithLocation;
    }

    @Override
    public Long getLastTotalFocusedTimeToday(LocalDateTime startOfDay, LocalDateTime endOfDay, Long memberId) {
        QFocusTime focusTime = QFocusTime.focusTime;

        Long totalFocusedTimeToday = jpaQueryFactory
                .select(focusTime.totalToday)
                .from(focusTime)
                .where(
                        focusTime.createdAt.between(startOfDay, endOfDay),
                        focusTime.memberId.eq(memberId),
                        focusTime.isDeleted.isFalse()
                )
//                .groupBy(focusTime.id) // 안묶어도 된다. => 어차피 PK 굳이 묶는 게 의미 없음
                .orderBy(focusTime.id.desc())
                .fetchFirst();

        return totalFocusedTimeToday == null ? 0 : totalFocusedTimeToday; // null 처리
    }

}

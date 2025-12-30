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



//    @Override
//    public List<FocusTimeNoLocationDto> getFocusTimeNoLocationDtoList(LocalDateTime startOfDay, LocalDateTime endOfDay, Long memberId){
//        QFocusTime focusTime = QFocusTime.focusTime;
//        QLocation location = QLocation.location;
//
//        // focus_log_id가 존재하는 경우 -> location_is_deleted가 true인지 확인
//        // focus_log_id == null은 그대로 뽑기
//
//        List<FocusTimeNoLocationDto> focusTimeNoLocation = jpaQueryFactory
//                .select(new QFocusTimeNoLocationDto(
//                        focusTime.id,
//                        focusTime.focusedTime,
//                        focusTime.startFocusTime,
//                        focusTime.endFocusTime)
//                )
//                .from(focusTime)
//                .leftJoin(location).on(focusTime.focusLogId.eq(location.focusLogId))
//                .where(
//                        focusTime.createdAt.between(startOfDay, endOfDay),
//                        focusTime.isDeleted.eq(false),
//                        focusTime.memberId.eq(memberId),
//
//                        // focus_log가 아예 없거나, location데이터가 삭제 됐거나
//                        focusTime.focusLogId.isNull().or(location.isDeleted.eq(true))
//                )
//                .fetch();
//
//        return focusTimeNoLocation;
//    }
}

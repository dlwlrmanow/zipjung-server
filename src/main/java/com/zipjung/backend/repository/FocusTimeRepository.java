package com.zipjung.backend.repository;

import com.zipjung.backend.entity.FocusTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FocusTimeRepository extends JpaRepository<FocusTime, Long> {
    @Modifying
    @Query("UPDATE FocusTime f SET f.focusLogId = :focusLogId WHERE f.id = :focusTimeId")
    int updateFocusLogId(@Param("focusLogId") Long focusLogId, @Param("focusTimeId")Long focusTimeId);

    // TODO: 사용자 집중 시간 리스트에서 최근 일주일 집중시간 + 시간에 대한 기록 없는 것만!!
    @Query("SELECT f FROM FocusTime f WHERE f.createdAt >= :oneWeekAgo AND f.isDeleted = false AND f.focusLogId IS NULL") // 최근 일주일내 기록만 가져오기
    List<FocusTime> getRecentWeekFocusTimes(@Param("oneWeekAgo") LocalDateTime oneWeekAgo);
}

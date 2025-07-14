package com.zipjung.backend.repository;

import com.zipjung.backend.entity.FocusTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FocusTimeRepository extends JpaRepository<FocusTime, Long> {
    @Modifying
    @Query("UPDATE FocusTime f SET f.focusLogId = :focusLogId WHERE f.id = :focusTimeId")
    int updateFocusLogId(@Param("focusLogId") Long focusLogId, @Param("focusTimeId")Long focusTimeId);
}

package com.zipjung.backend.repository;

import com.zipjung.backend.entity.FocusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FocusLogRepository extends JpaRepository<FocusLog, Long> {

}

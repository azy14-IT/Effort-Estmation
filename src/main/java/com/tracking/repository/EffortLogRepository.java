package com.tracking.repository;

import com.tracking.model.EffortLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EffortLogRepository extends JpaRepository<EffortLog, Long> {
    List<EffortLog> findByUserId(Long userId);
}

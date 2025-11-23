package com.coursework.jsontool.repository;

import com.coursework.jsontool.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findTop50ByOrderByTimestampDesc();
}
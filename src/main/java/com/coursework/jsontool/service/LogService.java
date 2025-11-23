package com.coursework.jsontool.service;

import com.coursework.jsontool.model.SystemLog;
import com.coursework.jsontool.repository.SystemLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {
    private final SystemLogRepository logRepository;

    public LogService(SystemLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String level, String service, String message) {
        logRepository.save(new SystemLog(level, service, message));
    }
}
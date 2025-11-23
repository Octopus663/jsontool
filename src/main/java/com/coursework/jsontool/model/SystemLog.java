package com.coursework.jsontool.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Data
@NoArgsConstructor
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level;
    private String message;
    private String service;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    public SystemLog(String level, String service, String message) {
        this.level = level;
        this.service = service;
        this.message = message;
    }
}
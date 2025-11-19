package com.coursework.jsontool.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "project_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId; // Foreign Key до projects.id

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type", nullable = false)
    private String fileType; // 'SCHEMA' або 'JSON_DATA'

    @Column(name = "current_content", columnDefinition = "LONGTEXT")
    private String currentContent; // Поточний вміст JSON/Schema

    @Column(name = "updated_at")
    private java.sql.Timestamp updatedAt;
}
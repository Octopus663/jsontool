package com.coursework.jsontool.repository;

import com.coursework.jsontool.model.ProjectFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {
    List<ProjectFile> findAllByProjectId(Long projectId);
}
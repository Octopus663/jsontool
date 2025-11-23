package com.coursework.jsontool.repository;

import com.coursework.jsontool.model.FileVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileVersionRepository extends JpaRepository<FileVersion, Long> {
    List<FileVersion> findTop10ByFileIdOrderByCreatedAtDesc(Long fileId);
}
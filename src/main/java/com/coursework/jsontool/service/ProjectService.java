package com.coursework.jsontool.service;

import com.coursework.jsontool.model.Project;
import com.coursework.jsontool.model.ProjectFile;
import com.coursework.jsontool.repository.ProjectRepository;
import com.coursework.jsontool.repository.ProjectFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectFileRepository projectFileRepository) {
        this.projectRepository = projectRepository;
        this.projectFileRepository = projectFileRepository;
    }


    @Transactional
    public Project createProject(Long userId, String projectName, String projectDescription) {

        Project newProject = new Project(userId, projectName, projectDescription);
        Project savedProject = projectRepository.save(newProject);

        ProjectFile schemaFile = new ProjectFile();
        schemaFile.setProjectId(savedProject.getId());
        schemaFile.setFileName("schema.json");
        schemaFile.setFileType("SCHEMA");
        schemaFile.setCurrentContent("{\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"type\": \"object\",\n  \"properties\": {}\n}");
        projectFileRepository.save(schemaFile);

        ProjectFile dataFile = new ProjectFile();
        dataFile.setProjectId(savedProject.getId());
        dataFile.setFileName("data.json");
        dataFile.setFileType("JSON_DATA");
        dataFile.setCurrentContent("{\n}");
        projectFileRepository.save(dataFile);

        return savedProject;
    }

    public List<Project> getUserProjects(Long userId) {
        return projectRepository.findAllByUserId(userId);
    }

}
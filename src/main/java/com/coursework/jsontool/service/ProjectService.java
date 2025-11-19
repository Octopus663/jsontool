package com.coursework.jsontool.service;

import com.coursework.jsontool.model.Project;
import com.coursework.jsontool.model.ProjectFile;
import com.coursework.jsontool.repository.ProjectRepository;
import com.coursework.jsontool.repository.ProjectFileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.coursework.jsontool.dto.ValidationResultDto;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SchemaExportService exportService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectFileRepository projectFileRepository,
                          SchemaExportService exportService) {
        this.projectRepository = projectRepository;
        this.projectFileRepository = projectFileRepository;
        this.exportService = exportService;
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

    public Optional<ProjectFile> getFileContent(Long fileId) {
        return projectFileRepository.findById(fileId);
    }

    @Transactional
    public ProjectFile saveFileContent(Long fileId, String newContent) {
        ProjectFile file = projectFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));

        file.setCurrentContent(newContent);

        return projectFileRepository.save(file);
    }


    public ValidationResultDto validateContents(String schemaContent, String dataContent) {
        try {
            JsonNode schemaNode = objectMapper.readTree(schemaContent);
            JsonNode dataNode = objectMapper.readTree(dataContent);

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonSchema schema = factory.getSchema(schemaNode);
            Set<ValidationMessage> validationMessages = schema.validate(dataNode);

            if (validationMessages.isEmpty()) {
                return new ValidationResultDto(true, 0, List.of("Валідація пройшла успішно."));
            } else {
                List<String> errors = validationMessages.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.toList());

                return new ValidationResultDto(false, errors.size(), errors);
            }

        } catch (Exception e) {
            return new ValidationResultDto(
                    false,
                    1,
                    List.of("Помилка парсингу: " + e.getMessage())
            );
        }
    }

    public String exportSchemaToMarkdown(Long schemaFileId) {
        ProjectFile file = projectFileRepository.findById(schemaFileId)
                .orElseThrow(() -> new RuntimeException("Schema file not found with ID: " + schemaFileId));

        return exportService.generateMarkdownTable(file.getCurrentContent());
    }
}
package com.coursework.jsontool.controller;

import com.coursework.jsontool.dto.ProjectCreationDto;
import com.coursework.jsontool.model.Project;
import com.coursework.jsontool.model.ProjectFile;
import com.coursework.jsontool.service.ProjectService;
import com.coursework.jsontool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.coursework.jsontool.model.User;
import com.coursework.jsontool.dto.ValidationResultDto;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> userOpt = userService.findByEmail(userEmail);

        if (userOpt.isPresent()) {
            return userOpt.get().getId();
        } else {
            throw new RuntimeException("Authenticated user not found in database!");
        }
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody ProjectCreationDto projectDto) {
        Long currentUserId = getCurrentUserId();

        Project newProject = projectService.createProject(
                currentUserId,
                projectDto.getName(),
                projectDto.getDescription()
        );

        return ResponseEntity.ok(newProject);
    }


    @GetMapping
    public ResponseEntity<List<Project>> getAllUserProjects() {
        Long currentUserId = getCurrentUserId();

        List<Project> projects = projectService.getUserProjects(currentUserId);

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<ProjectFile> getFile(@PathVariable Long fileId) {
        Long currentUserId = getCurrentUserId();

        ProjectFile file = projectService.getFileContent(fileId)
                .orElseThrow(() -> new RuntimeException("File not found."));

        return ResponseEntity.ok(file);
    }

    @PostMapping("/files/{fileId}")
    public ResponseEntity<ProjectFile> saveFile(
            @PathVariable Long fileId,
            @RequestBody String newContent) {

        ProjectFile updatedFile = projectService.saveFileContent(fileId, newContent);

        return ResponseEntity.ok(updatedFile);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResultDto> validateProjectContent(
            @RequestParam Long schemaFileId,
            @RequestParam Long dataFileId) {

        Optional<ProjectFile> schemaOpt = projectService.getFileContent(schemaFileId);
        Optional<ProjectFile> dataOpt = projectService.getFileContent(dataFileId);

        if (schemaOpt.isEmpty() || dataOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new ValidationResultDto(
                    false, 1, List.of("Один із файлів не знайдено.")
            ));
        }

        ValidationResultDto result = projectService.validateContents(
                schemaOpt.get().getCurrentContent(),
                dataOpt.get().getCurrentContent()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/export/markdown/{schemaFileId}")
    public ResponseEntity<String> exportSchemaToMarkdown(@PathVariable Long schemaFileId) {

        String markdownContent = projectService.exportSchemaToMarkdown(schemaFileId);

        return ResponseEntity.ok()
                .header("Content-Type", "text/markdown; charset=utf-8")
                .body(markdownContent);
    }

    @GetMapping("/{projectId}/files")
    public ResponseEntity<List<ProjectFile>> getProjectFiles(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectFiles(projectId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable Long projectId) {
        return projectService.findProjectById(projectId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/files/{fileId}/flat")
    public ResponseEntity<String> getFlatJsonView(@PathVariable Long fileId) {
        String flatJson = projectService.getFlatJson(fileId);
        return ResponseEntity.ok(flatJson);
    }
}
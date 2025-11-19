package com.coursework.jsontool.controller;

import com.coursework.jsontool.dto.ProjectCreationDto;
import com.coursework.jsontool.model.Project;
import com.coursework.jsontool.service.ProjectService;
import com.coursework.jsontool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import com.coursework.jsontool.model.User;

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
}
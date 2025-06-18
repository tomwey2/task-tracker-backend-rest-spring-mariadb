package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.ProjectResponseDto;
import de.tomwey2.taskappbackend.dto.TaskResponseDto;
import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectResponseDto createProject(Project project) {
        project.setId(null);
        return convertToDto(projectRepository.save(project));
    }

    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public Optional<ProjectResponseDto> getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(this::convertToDto);

    }

    private ProjectResponseDto convertToDto(Project project) {
        return new ProjectResponseDto(project.getId(), project.getName(), project.getCreatedAt(), project.getUpdatedAt());
    }
}
package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.ProjectRequestDto;
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

    public Project createProject(ProjectRequestDto projectRequestDto) {
        Project newProject = new Project();
        newProject.setName(projectRequestDto.name());
        return projectRepository.save(newProject);
    }

    public List<Project> searchProjects(String name) {
        return projectRepository.searchProjects(name);
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

}
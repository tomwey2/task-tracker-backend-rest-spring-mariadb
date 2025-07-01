package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.TaskRequestDto;
import de.tomwey2.taskappbackend.exception.ResourceNotFoundException;
import de.tomwey2.taskappbackend.model.*;
import de.tomwey2.taskappbackend.repository.ProjectRepository;
import de.tomwey2.taskappbackend.repository.TaskRepository;
import de.tomwey2.taskappbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Erzeugt einen Konstruktor für alle 'final' Felder
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository; // UserRepository injecten
    private final ProjectRepository projectRepository;

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Die Methode braucht jetzt die userId
    public Task createTask(TaskRequestDto taskRequestDto, Long projectId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Manuelle Konvertierung vom DTO zur Entität
        Task newTask = new Task();
        newTask.setTitle(taskRequestDto.title());
        newTask.setDescription(taskRequestDto.description());
        newTask.setDeadline(taskRequestDto.deadline());
        newTask.setReportedBy(user);
        newTask.setBelongsTo(project);
        return taskRepository.save(newTask);
    }

    public Optional<Task> updateTask(Long id, TaskRequestDto updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.title());
                    existingTask.setDescription(updatedTask.description());
                    existingTask.setState(updatedTask.state());
                    existingTask.setDeadline(updatedTask.deadline());
                    return taskRepository.save(existingTask);
                });
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Task> searchTasks(Long projectId, Long assignedToUserId, String title) {
        return taskRepository.searchTasks(projectId, assignedToUserId, title);
    }

}
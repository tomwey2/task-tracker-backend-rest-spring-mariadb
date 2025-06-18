package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.TaskRequestDto;
import de.tomwey2.taskappbackend.dto.TaskResponseDto;
import de.tomwey2.taskappbackend.dto.UserResponseDto;
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

    public List<TaskResponseDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto) // Konvertiere jeden Task in ein TaskDto
                .toList();
    }

    public Optional<TaskResponseDto> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::convertToDto); // Konvertiere jeden Task in ein TaskDto

    }

    // Die Methode braucht jetzt die userId
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Project project = projectRepository.findByName(taskRequestDto.projectName())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with name: " + taskRequestDto.projectName()));

        // Manuelle Konvertierung vom DTO zur Entität
        Task newTask = new Task();
        newTask.setTitle(taskRequestDto.title());
        newTask.setDescription(taskRequestDto.description());
        newTask.setDeadline(taskRequestDto.deadline());
        newTask.setReportedBy(user);
        newTask.setBelongsTo(project);

        Task savedTask = taskRepository.save(newTask);

        // Konvertiere die gespeicherte Entität in ein Response-DTO für die Rückgabe
        return convertToDto(savedTask);
    }

    public Optional<TaskResponseDto> updateTask(Long id, TaskRequestDto updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.title());
                    existingTask.setDescription(updatedTask.description());
                    existingTask.setState(updatedTask.state());
                    existingTask.setDeadline(updatedTask.deadline());
                    return convertToDto(taskRepository.save(existingTask));
                });
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<TaskResponseDto> searchTasks(Long projectId, Long assignedToUserId) {
        return taskRepository.searchTasks(projectId, assignedToUserId)
                .stream()
                .map(this::convertToDto) // Wiederverwendung deiner Konvertierungsmethode
                .toList();
    }

    // Private Hilfsmethode zur Konvertierung
    private TaskResponseDto convertToDto(Task task) {
        // Hier wird der Lazy-Proxy initialisiert, weil wir getReportedBy() aufrufen.
        // Das passiert aber innerhalb der Transaktion im Service, was sicher ist.
        UserResponseDto userDtoReportedBy = new UserResponseDto(
                task.getReportedBy().getId(),
                task.getReportedBy().getUsername(),
                task.getReportedBy().getCreatedAt(),
                task.getReportedBy().getUpdatedAt()
        );

        UserResponseDto userDtoAssignedTo = task.getAssignedTo() == null ? null : new UserResponseDto(
                task.getAssignedTo().getId(),
                task.getAssignedTo().getUsername(),
                task.getAssignedTo().getCreatedAt(),
                task.getAssignedTo().getUpdatedAt()
        );

        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getState(),
                task.getDeadline(),
                userDtoReportedBy, // Das UserDto hier einfügen
                userDtoAssignedTo,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
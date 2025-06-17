package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.TaskRequestDto;
import de.tomwey2.taskappbackend.dto.TaskResponseDto;
import de.tomwey2.taskappbackend.dto.UserResponseDto;
import de.tomwey2.taskappbackend.exception.ResourceNotFoundException;
import de.tomwey2.taskappbackend.model.*;
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

        // Manuelle Konvertierung vom DTO zur Entität
        Task newTask = new Task();
        newTask.setTitle(taskRequestDto.title());
        newTask.setDescription(taskRequestDto.description());
        newTask.setReportedBy(user);
        // 'completed' und 'createdAt' werden von der Entität selbst gesetzt

        Task savedTask = taskRepository.save(newTask);

        // Konvertiere die gespeicherte Entität in ein Response-DTO für die Rückgabe
        return convertToDto(savedTask);
    }

    public Optional<TaskResponseDto> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setState(updatedTask.getState());
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

    // Private Hilfsmethode zur Konvertierung
    private TaskResponseDto convertToDto(Task task) {
        // Hier wird der Lazy-Proxy initialisiert, weil wir getReportedBy() aufrufen.
        // Das passiert aber innerhalb der Transaktion im Service, was sicher ist.
        UserResponseDto userDto = new UserResponseDto(
                task.getReportedBy().getId(),
                task.getReportedBy().getUsername(),
                task.getReportedBy().getCreatedAt(),
                task.getReportedBy().getUpdatedAt()
        );

        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getState(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                userDto // Das UserDto hier einfügen
        );
    }
}
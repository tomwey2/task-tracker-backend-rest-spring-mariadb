package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.model.TaskDto;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.model.UserDto;
import de.tomwey2.taskappbackend.repository.TaskRepository;
import de.tomwey2.taskappbackend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Erzeugt einen Konstruktor für alle 'final' Felder
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository; // UserRepository injecten

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDto) // Konvertiere jeden Task in ein TaskDto
                .toList();
    }

    public Optional<TaskDto> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::convertToDto); // Konvertiere jeden Task in ein TaskDto

    }

    // Die Methode braucht jetzt die userId
    public TaskDto createTask(Task task, Long userId) {
        // Finde den User, dem der Task gehören soll
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Setze den gefundenen User auf den Task
        task.setReportedBy(user);
        task.setId(null); // Sicherstellen, dass eine neue Entität erstellt wird

        return convertToDto(taskRepository.save(task));
    }
    public Optional<TaskDto> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setCompleted(updatedTask.isCompleted());
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
    private TaskDto convertToDto(Task task) {
        // Hier wird der Lazy-Proxy initialisiert, weil wir getReportedBy() aufrufen.
        // Das passiert aber innerhalb der Transaktion im Service, was sicher ist.
        UserDto userDto = new UserDto(
                task.getReportedBy().getId(),
                task.getReportedBy().getUsername()
        );

        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                task.getCreatedAt(),
                userDto // Das UserDto hier einfügen
        );
    }
}
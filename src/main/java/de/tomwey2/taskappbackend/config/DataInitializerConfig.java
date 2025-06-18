package de.tomwey2.taskappbackend.config;

import de.tomwey2.taskappbackend.Constants;
import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.repository.ProjectRepository;
import de.tomwey2.taskappbackend.repository.TaskRepository;
import de.tomwey2.taskappbackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Slf4j // Lombok Annotation für einfaches Logging
@Profile("dev") // Diese Bean wird nur aktiviert, wenn das Profil 'dev' aktiv ist
public class DataInitializerConfig {

    @Bean
    public ApplicationRunner initializeDatabase(UserRepository userRepository,
                                                TaskRepository taskRepository,
                                                ProjectRepository projectRepository,
                                                PasswordEncoder passwordEncoder) {

        return args -> {
            log.info("Starting database initialization...");

            // --- Benutzer erstellen ---
            // Nur erstellen, wenn die DB leer ist, um Duplikate zu vermeiden
            if (userRepository.count() == 0) {
                log.info("Creating sample users...");
                User user1 = new User();
                user1.setUsername("erika.muster");
                user1.setEmail("erika@example.com");
                user1.setPassword(passwordEncoder.encode("password123"));
                user1.setRole("ROLE_USER");

                User user2 = new User();
                user2.setUsername("max.power");
                user2.setEmail("max@example.com");
                user2.setPassword(passwordEncoder.encode("password456"));
                user2.setRole("ROLE_USER");

                userRepository.saveAll(List.of(user1, user2));
                log.info("Sample users created.");
            } else {
                log.info("Users already exist, skipping user creation.");
            }

            if (projectRepository.count() == 0) {
                log.info("Creating sample projects...");
                Project project1 = new Project();
                project1.setName("p1");

                projectRepository.saveAll(List.of(project1));
                log.info("Sample projects created; " + projectRepository.count());
            }

            // --- Tasks erstellen ---
            if (taskRepository.count() == 0) {
                log.info("Creating sample tasks...");
                // Benutzer aus der DB laden, um eine Referenz zu haben
                User erika = userRepository.findByUsername("erika.muster").get();
                User max = userRepository.findByUsername("max.power").get();
                Project p1 = projectRepository.findByName("p1").get();

                Task task1 = new Task();
                task1.setTitle("Spring Boot lernen");
                task1.setDescription("Die Grundlagen von Spring Boot und Spring Data JPA verstehen.");
                task1.setDueDate(LocalDate.now().plusDays(1));
                task1.setReportedBy(erika);
                task1.setBelongsTo(p1);

                Task task2 = new Task();
                task2.setTitle("API mit Security absichern");
                task2.setDescription("Einfache Authentifizierung mit Usern aus der DB implementieren.");
                task2.setDueDate(LocalDate.now().plusDays(2));
                task2.setReportedBy(erika);
                task2.setState(Constants.TASK_IN_PROGRESS);
                task2.setUpdatedAt(LocalDateTime.now());
                task2.setBelongsTo(p1);
                task2.setAssignedTo(max);

                Task task3 = new Task();
                task3.setTitle("Frontend entwerfen");
                task3.setDescription("Ein Mockup für das React/Angular Frontend erstellen.");
                task3.setDueDate(LocalDate.now().plusDays(10));
                task3.setReportedBy(max);
                task3.setBelongsTo(p1);
                task3.setAssignedTo(erika);

                taskRepository.saveAll(List.of(task1, task2, task3));
                log.info("Sample tasks created.");
            } else {
                log.info("Tasks already exist, skipping task creation.");
            }

            log.info("Database initialization finished.");
        };
    }
}
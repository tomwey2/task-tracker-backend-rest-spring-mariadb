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
                user1.setUsername("n8n");  // User für n8n Workflows
                user1.setEmail("n8n@example.com");
                user1.setPassword(passwordEncoder.encode("password000"));
                user1.setRole("ROLE_USER");

                User user2 = new User();
                user2.setUsername("erika.muster"); // Projektleiterin "Software"
                user2.setEmail("erika@example.com");
                user2.setPassword(passwordEncoder.encode("password123"));
                user2.setRole("ROLE_USER");

                User user3 = new User();
                user3.setUsername("max.power"); // Projektleiter "Hardware"
                user3.setEmail("max@example.com");
                user3.setPassword(passwordEncoder.encode("password456"));
                user3.setRole("ROLE_USER");

                userRepository.saveAll(List.of(user1, user2, user3));
                log.info("Sample users created.");
            } else {
                log.info("Users already exist, skipping user creation.");
            }

            if (projectRepository.count() == 0) {
                log.info("Creating sample projects...");
                Project projectSoftware = new Project();
                projectSoftware.setName("Software");
                Project projectHardware = new Project();
                projectHardware.setName("Hardware");
                Project projectInstallation = new Project();
                projectInstallation.setName("Installation");

                projectRepository.saveAll(List.of(projectSoftware, projectHardware, projectInstallation));
                log.info("Sample projects created; " + projectRepository.count());
            }

            // --- Tasks erstellen ---
            if (taskRepository.count() == 0) {
                log.info("Creating sample tasks...");
                // Benutzer aus der DB laden, um eine Referenz zu haben
                User erika = userRepository.findByUsername("erika.muster").get();
                User max = userRepository.findByUsername("max.power").get();
                Project projectSoftware = projectRepository.findByName("Software").get();
                Project projectHardware = projectRepository.findByName("Hardware").get();

                Task task1 = new Task();
                task1.setTitle("Login service unavailable");
                task1.setDescription("Users cannot log in to the application. All access is blocked. Investigating authentication provider issue.");
                task1.setComment("");
                task1.setDeadline(LocalDate.now().plusDays(1));
                task1.setReportedBy(erika);
                task1.setBelongsTo(projectSoftware);

                Task task2 = new Task();
                task2.setTitle("Slow API response times for all GET requests");
                task2.setDescription("Users are experiencing significant lag across the application. Response times have degraded from 200ms to >3s.");
                task2.setComment("");
                task2.setDeadline(LocalDate.now().plusDays(2));
                task2.setReportedBy(erika);
                task2.setState(Constants.TASK_IN_PROGRESS);
                task2.setUpdatedAt(LocalDateTime.now());
                task2.setBelongsTo(projectSoftware);
                task2.setAssignedTo(max);

                Task task3 = new Task();
                task3.setTitle("Unit does not power on");
                task3.setDescription("Device is completely unresponsive. No signs of life from display or LEDs, even when connected to a known-good power source.");
                task3.setComment("");
                task3.setDeadline(LocalDate.now().plusDays(10));
                task3.setReportedBy(max);
                task3.setBelongsTo(projectHardware);
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
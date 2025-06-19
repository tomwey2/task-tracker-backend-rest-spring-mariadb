package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.repository.ProjectRepository;
import de.tomwey2.taskappbackend.repository.TaskRepository;
import de.tomwey2.taskappbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Testcontainers // 1. Aktiviert die Testcontainers-JUnit-5-Erweiterung
@Sql("/test-data.sql") // Initialisiert die MariaDb Testdatenbank im Container mit Testdaten
class TaskControllerIntegrationTest {
    private static final String testUsername = "erika.muster";
    private static final String testPassword = "password123";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 2. Deklariert einen MariaDB-Container.
    // 'static' sorgt dafür, dass der Container nur einmal für alle Tests in dieser Klasse gestartet wird.
    @Container
    static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:11.8.2"); // Verwende ein passendes MariaDB-Image

    // 3. Konfiguriert die DataSource-Properties dynamisch zur Laufzeit.
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mariaDBContainer::getUsername);
        registry.add("spring.datasource.password", mariaDBContainer::getPassword);
        // Wir müssen Hibernate sagen, welchen SQL-Dialekt es verwenden soll
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MariaDBDialect");
    }

    @Test
    void whenGetTasks_thenReturnsOk() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .with(httpBasic(testUsername, testPassword)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void whenGetTaskById_thenReturnsCorrectTask() throws Exception {
        // HINWEIS: Deine Tests selbst ändern sich NICHT!
        // Du musst sicherstellen, dass die Testdaten vorhanden sind (z.B. via @Sql).
        // Annahme: Es gibt ein Skript, das einen Task mit ID 1 und Titel "Test Task" anlegt.
        mockMvc.perform(get("/api/tasks/1")
                        .with(httpBasic(testUsername, testPassword)))
                .andExpect(status().isOk())
                //.andDo(print());
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Task 1"));
    }

    @Test
    void whenGetTasksId4711_thenReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/tasks/4711")
                        .with(httpBasic(testUsername, testPassword)))
                .andExpect(status().isNotFound());
    }


}
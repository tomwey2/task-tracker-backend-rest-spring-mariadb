package de.tomwey2.taskappbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // Auditing-Feature aktivieren
public class TaskappBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskappBackendApplication.class, args);
	}

}

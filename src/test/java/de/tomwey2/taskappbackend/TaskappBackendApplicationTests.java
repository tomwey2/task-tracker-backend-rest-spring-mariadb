package de.tomwey2.taskappbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers // 1. Aktiviert die Testcontainers-JUnit-5-Erweiterung
class TaskappBackendApplicationTests extends MariaDbContainerTest {

	@Test
	void contextLoads() {
	}

}

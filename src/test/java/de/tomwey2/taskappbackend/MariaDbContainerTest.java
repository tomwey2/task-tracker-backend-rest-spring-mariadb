package de.tomwey2.taskappbackend;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;

public class MariaDbContainerTest {
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
}

## Spring Boot: Projektstruktur und Setup
Ein gut strukturiertes Projekt ist leichter zu verstehen, zu warten und zu erweitern. Spring Boot folgt 
den Konventionen von Maven und Gradle, was zu einer standardisierten und vorhersehbaren Struktur führt.

### 1. Typische Projektstruktur
Wenn man ein Spring Boot Projekt generiert, sieht die Ordnerstruktur in der Regel wie folgt aus:

    mein-projekt
    ├── .mvn/
    ├── .gradle/
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com
    │   │   │       └── beispiel
    │   │   │           └── meinprojekt
    │   │   │               └── MeinProjektApplication.java  // Hauptklasse der Anwendung
    │   │   └── resources
    │   │       ├── static/         // Für CSS, JavaScript, Bilder etc.
    │   │       ├── templates/      // Für Template-Engines wie Thymeleaf
    │   │       └── application.properties // Konfigurationsdatei
    │   └── test
    │       └── java
    │           └── com
    │               └── beispiel
    │                   └── meinprojekt
    │                       └── MeinProjektApplicationTests.java // Testklasse
    ├── build.gradle              // Für Gradle-Projekte
    └── pom.xml                   // Für Maven-Projekte

**Erläuterung der wichtigsten Ordner:**
- `src/main/java`: Hier befindet sich der gesamte Java-Quellcode. Es ist eine bewährte Praxis, alle 
Klassen in einem Root-Paket (z.B. `com.beispiel.meinprojekt`) zu organisieren. Die Klasse
`MeinProjektApplication.java` mit der `@SpringBootApplication-Annotation` ist der Startpunkt.

- `src/main/resources`: Dieser Ordner enthält alle nicht-Code-Ressourcen.
  - `application.properties` (oder `application.yml`): Die zentrale Datei für die Konfiguration Ihrer 
  Anwendung (z.B. Server-Port, Datenbankverbindung, Logging-Level).
 
  - `static`: Statische Web-Inhalte. Inhalte hier sind direkt über den Browser erreichbar
  (z.B. `http://localhost:8080/style.css`).
   
  - `templates`: Vorlagen für serverseitiges Rendering, wenn man eine Template-Engine wie Thymeleaf oder 
  FreeMarker verwendet.
  
- `src/test/java`: Hier schreiben man die Unit- und Integrationstests.

### 2. Aufsetzen eines Spring Boot Projekts
Der mit Abstand einfachste und empfohlene Weg, ein neues Spring Boot Projekt zu starten, ist der Spring 
Initializr.

#### Methode 1: Spring Initializr (Empfohlen)
Der Spring Initializr ist ein Web-Tool, das die grundlegende Projektstruktur und die Build-Dateien 
generiert.

Schritte:
- Gehe zu `start.spring.io`.
- Wähle das Build-Tool: Maven oder Gradle.
- Wähle die Sprache: Java, Kotlin oder Groovy.
- Wähle eine stabile Spring Boot Version.
- Fülle die Projekt-Metadaten aus: Group, Artifact, Name, Description.
- Füge Abhängigkeiten (Dependencies) hinzu: Klicke auf "ADD DEPENDENCIES..." und wähle die benötigten 
Starter aus.
- Klicke auf "GENERATE".

#### Methode 2: Manuelles Setup
**Für Maven (`pom.xml`):**
(Code-Beispiel wie zuvor)

**Für Gradle (`build.gradle`):**
(Code-Beispiel wie zuvor)


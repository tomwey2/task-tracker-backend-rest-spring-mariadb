## Die Kernkonzepte von Spring Boot
Spring Boot ist ein Framework, das auf dem Spring Framework aufbaut und dessen Ziel es ist, die Erstellung 
von produktionsreifen, eigenständigen Spring-Anwendungen so einfach und schnell wie möglich zu gestalten. 
Es trifft "meinungsstarke" (opinionated) Annahmen über die Konfiguration, wodurch der Entwickler von einem 
Großteil des Boilerplate-Codes befreit wird.

Hier sind die vier wichtigsten Konzepte:

### 1. Autokonfiguration (Autoconfiguration)
Dies ist das Herzstück von Spring Boot. Anstatt jede Komponente (wie einen Datenquellen-Bean, einen 
`EntityManagerFactory` oder einen `DispatcherServlet`) manuell in XML- oder Java-Konfigurationsdateien 
deklarieren zu müssen, analysiert Spring Boot den Classpath der Anwendung.

- **Wie funktioniert das?** Wenn Spring Boot beispielsweise die H2-Datenbank-JAR-Datei im Classpath findet 
    und keine eigene `DataSource` konfiguriert wurde, konfiguriert es automatisch eine In-Memory-Datenbank. 
    Findet es `spring-webmvc` im Classpath, konfiguriert es automatisch einen `DispatcherServlet` und andere 
    Web-Komponenten.
- **Vorteil:** Enorme Zeitersparnis und Reduzierung von Konfigurationsfehlern. Man muss nur dann eingreifen, 
  wenn man von den Standardeinstellungen abweichen möchte.

### 2. „Meinungsstarke“ Starter-Abhängigkeiten (Opinionated Starter Dependencies)
Um die Abhängigkeitsverwaltung zu vereinfachen, bietet Spring Boot eine Reihe von "Startern". Dies sind im 
Wesentlichen `pom.xml`- oder `build.gradle`-Einträge, die eine Gruppe von transitiven Abhängigkeiten bündeln, 
die für einen bestimmten Zweck benötigt werden.

- **Beispiel:** Anstatt manuell `spring-web`, `spring-webmvc`, `jackson-databind` und einen `tomcat-embedded`-Server 
    hinzuzufügen, um eine Webanwendung zu erstellen, fügen man einfach die Abhängigkeit 
    `spring-boot-starter-web` hinzu.
- **Vorteil:** Man erhält eine kuratierte und getestete Sammlung von Bibliotheken, die garantiert
     miteinander kompatibel sind. Das manuelle Suchen und Abstimmen von Versionen entfällt.

### 3. Eigenständige Anwendungen (Standalone Applications)
Traditionell wurden Java-Webanwendungen als WAR-Dateien (Web Application Archive) verpackt und auf einem 
externen Anwendungsserver (wie Apache Tomcat oder JBoss) bereitgestellt.

- **Der Spring Boot Ansatz:** Spring Boot ermöglicht es, die Anwendung als eine einzige, ausführbare 
    JAR-Datei zu verpacken. Diese JAR-Datei enthält nicht nur Ihren Anwendungscode, sondern auch einen 
    eingebetteten Server (standardmäßig Tomcat).

- **Wie wird das ausgeführt?** Man kann die Anwendung einfach über die Kommandozeile mit
    'java -jar IhreAnwendung.jar starten*.

- **Vorteil:** Vereinfacht die Bereitstellung (Deployment) und den Betrieb erheblich. Es ist ideal für
    Microservices-Architekturen und Cloud-Umgebungen.

### 4. Actuator
Der Spring Boot Actuator ist ein Unterprojekt, das produktionsreife Funktionen für Ihre Anwendung 
bereitstellt, ohne dass man viel Code schreiben muss. Sobald man den spring-boot-starter-actuator als 
Abhängigkeit hinzufügen, erhält man eine Reihe von Endpunkten über HTTP (oder JMX).

- **Was bietet er?**
    - `/health`: Überprüft den Zustand der Anwendung (z. B. Verbindung zur Datenbank).
    - `/metrics`: Liefert detaillierte Metriken (z. B. JVM-Speichernutzung, HTTP-Anfragen). 
    - `/info`: Zeigt allgemeine Anwendungsinformationen an.
    - `/env`: Listet alle Umgebungsvariablen und Konfigurationseigenschaften auf.

- **Vorteil:** Erleichtert das Monitoring und die Verwaltung der Anwendung im laufenden Betrieb.

**Zusammenfassend lässt sich sagen:** Spring Boot nimmt Ihnen die wiederkehrenden und fehleranfälligen 
Konfigurationsaufgaben ab, sodass Sie sich voll und ganz auf die Implementierung Ihrer Geschäftslogik 
konzentrieren können. Es ermöglicht eine extrem schnelle Entwicklung ("Rapid Application Development") 
von robusten und leicht zu betreibenden Java-Anwendungen.


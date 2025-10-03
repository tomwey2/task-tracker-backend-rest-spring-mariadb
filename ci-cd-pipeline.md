## Aufbau einer CI/CD-Pipeline mit Jenkins und Docker
Eine CI/CD-Pipeline (Continuous Integration / Continuous Deployment) automatisiert die 
Schritte von der Code-Änderung bis zur Bereitstellung der Anwendung in der 
Produktionsumgebung. Dies erhöht die Geschwindigkeit, Zuverlässigkeit und Effizienz der 
Softwareentwicklung. Jenkins als Automatisierungsserver und Docker zur Containerisierung 
sind eine klassische und leistungsstarke Kombination, um dies zu erreichen.

### Die Kernkomponenten
- **Jenkins:** Ein Open-Source-Automatisierungsserver, der als CI/CD-Orchestrator fungiert. 
Er holt den Code, baut, testet und deployed die Anwendung.

- **Docker:** Eine Plattform zur Erstellung, Verteilung und Ausführung von Anwendungen in 
isolierten Containern. Docker stellt sicher, dass die Anwendung in jeder Umgebung 
(Entwicklung, Test, Produktion) konsistent läuft.

- **Git (z.B. GitHub, GitLab):** Ein Versionskontrollsystem, in dem der Quellcode, der 
Dockerfile und der Jenkinsfile gespeichert sind.

### Der CI/CD-Workflow
Der typische Ablauf sieht wie folgt aus:

1. **Commit & Push:** Ein Entwickler committet Code-Änderungen und pusht sie in ein 
Git-Repository. 
2. **Trigger:** Jenkins erkennt die Änderung (z.B. über einen Webhook) und startet 
automatisch einen neuen Build-Job. 
3. **Checkout:** Jenkins holt den neuesten Code aus dem Repository. 
4. **Build & Test (CI):** Jenkins kompiliert den Code (z.B. mit Maven oder Gradle) und 
führt automatisierte Tests (Unit- und Integrationstests) aus. Schlägt dieser Schritt fehl, wird die Pipeline abgebrochen und das Team benachrichtigt. 
5. **Docker Image erstellen:** Nach erfolgreichen Tests baut Jenkins ein Docker-Image 
basierend auf einem `Dockerfile`, das im Projekt liegt. Die kompilierte Anwendung (z.B. die JAR-Datei) wird in dieses Image kopiert. 
6. **Image pushen:** Das neu erstellte Docker-Image wird in eine Docker Registry (z.B. 
Docker Hub, Google Container Registry, AWS ECR) hochgeladen. Es wird typischerweise mit 
einer eindeutigen Version oder dem Git-Commit-Hash getaggt. 
7. **Deployment (CD):** Jenkins stellt die neue Version bereit, indem es den 
Docker-Container auf einem oder mehreren Zielservern startet. Dies kann eine einfache 
`docker run`-Anweisung oder die Aktualisierung eines Kubernetes-Deployments sein.

### Schritt-für-Schritt-Anleitung am Beispiel einer Spring Boot App
#### Schritt 1: Das Dockerfile im Projekt erstellen
Fügen Sie eine Datei namens Dockerfile (ohne Dateiendung) zum Stammverzeichnis Ihres 
Spring Boot Projekts hinzu.

    # Schritt 1: Verwende ein Basis-Image mit einer Java-Laufzeitumgebung
    FROM openjdk:17-slim

    # Schritt 2: Setze das Arbeitsverzeichnis im Container
    WORKDIR /app

    # Schritt 3: Kopiere die kompilierte JAR-Datei aus dem Build-Kontext in den Container
    # Das ARG stellt sicher, dass der Jenkinsfile den Pfad zur JAR-Datei übergeben kann.
    ARG JAR_FILE=target/*.jar
    COPY ${JAR_FILE} app.jar

    # Schritt 4: Gib den Port an, auf dem die Anwendung lauschen wird
    EXPOSE 8080

    # Schritt 5: Der Befehl zum Starten der Anwendung, wenn der Container gestartet wird
    ENTRYPOINT ["java", "-jar", "app.jar"]

#### Schritt 2: Das Jenkinsfile im Projekt erstellen
Das Jenkinsfile definiert die gesamte Pipeline als Code ("Pipeline as Code"). Fügen Sie 
diese Datei ebenfalls zum Stammverzeichnis Ihres Projekts hinzu.

    // Jenkinsfile
    pipeline {
        agent any // Führe diese Pipeline auf jedem verfügbaren Jenkins-Agenten aus
        // Umgebungsvariablen für die Pipeline
        environment {
            // Name des Docker-Images (ersetze 'dein-dockerhub-username')
            DOCKER_IMAGE = "dein-dockerhub-username/mein-spring-boot-app"
            // ID für die Docker Hub Anmeldeinformationen, die in Jenkins gespeichert sind
            DOCKER_CREDENTIALS_ID = "dockerhub-credentials"
        }

        stages {
            stage('Checkout') {
                steps {
                    // Lade den Code aus dem Git-Repository
                    git '[https://github.com/dein-username/dein-repo.git](https://github.com/dein-username/dein-repo.git)'
                }
            }

            stage('Build') {
                steps {
                    // Baue die Spring Boot Anwendung mit Maven
                    // Der Wrapper stellt sicher, dass die richtige Maven-Version verwendet wird
                    sh './mvnw clean package'
                }
            }

            stage('Build Docker Image') {
                steps {
                    script {
                        // Baue das Docker-Image basierend auf dem Dockerfile
                        // Der Tag ist die Build-Nummer von Jenkins, um Versionen eindeutig zu halten
                        sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    }
                }
            }

            stage('Push Docker Image') {
                steps {
                    // Melde dich bei Docker Hub an und pushe das Image
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                        sh "echo ${PASS} | docker login -u ${USER} --password-stdin"
                        sh "docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                    }
                }
            }

            stage('Deploy') {
                steps {
                    // Simples Deployment: Stoppe den alten Container und starte einen neuen
                    // In einer echten Umgebung würde man hier Tools wie SSH, Ansible oder Kubernetes verwenden
                    script {
                        // Stoppe und entferne einen eventuell laufenden alten Container
                        sh 'docker stop mein-spring-boot-app || true'
                        sh 'docker rm mein-spring-boot-app || true'
                    
                        // Starte den neuen Container
                        sh "docker run -d --name mein-spring-boot-app -p 8080:8080 ${DOCKER_IMAGE}:${BUILD_NUMBER}"
                    }
                }
            }
        }

        post {
            // Aktionen, die nach Abschluss der Pipeline ausgeführt werden
            always {
                // Aufräumen: Docker Logout
                sh 'docker logout'
            }
        }
    }

#### Schritt 3: Jenkins Job konfigurieren
1. **Jenkins Plugins:** Stellen Sie sicher, dass die notwendigen Plugins installiert sind 
(z.B. `Docker Pipeline`, `Git`, `Credentials Binding`). 
2. **Credentials:** Gehen Sie in Jenkins zu Manage Jenkins > Credentials und fügen Sie 
Ihre Docker Hub Anmeldedaten als "Username with password" hinzu. Geben Sie ihnen die ID, 
die im Jenkinsfile definiert ist (z.B. `dockerhub-credentials`). 
3. **Neuen Job erstellen:** Erstellen Sie einen neuen Jenkins-Job vom Typ "Pipeline". 
4. **Pipeline-Konfiguration:**
   - Wählen Sie unter "Definition" die Option "Pipeline script from SCM". 
   - **SCM:** Wählen Sie "Git". 
   - **Repository URL:** Geben Sie die URL zu Ihrem Git-Repository ein. 
   - **Script Path:** Der Standardwert Jenkinsfile ist korrekt, wenn Ihre Datei so heißt. 
   - **Speichern und starten:** Speichern Sie den Job. Jenkins wird nun das Repository scannen, das Jenkinsfile finden und die Pipeline ausführen.

Mit diesem Setup haben Sie eine voll funktionsfähige CI/CD-Pipeline, die jede 
Code-Änderung automatisch baut, testet und bereitstellt.

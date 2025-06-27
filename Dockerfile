# Wir verwenden ein sehr kleines JRE (Java Runtime Environment) Image auf Basis von Alpine Linux.
# Es enthält nur das Nötigste, um eine Java-Anwendung auszuführen.
FROM eclipse-temurin:21-jre-alpine

# Kopiere NUR die gebaute JAR-Datei aus der 'build'-Stufe in unser finales Image.
# Der Pfad zur JAR kann je nach deinem pom.xml variieren (artifactId/version).
# Passe den Pfad ggf. an.
COPY target/taskapp-backend-*.jar app.jar

# Informiere Docker, dass der Container auf Port 8080 lauscht.
EXPOSE 8080

# Der Befehl, der beim Starten des Containers ausgeführt wird.
ENTRYPOINT ["java", "-jar", "app.jar"]
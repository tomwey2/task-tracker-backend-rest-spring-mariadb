Wenn ich in Postman die Liste aller Tasks abfrage "/api/tasks", dann bekomme ich folgende 
Fehlermeldung:

    com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class 
    org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to 
    create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) 
    (through reference chain: 
        java.util.ArrayList[0]->de.tomwey2.taskappbackend.model.Task["reportedBy"]->de.tomwey2.taskappbackend.model.User$HibernateProxy["hibernateLazyInitializer"]
    )

Ah, das ist ein absoluter Klassiker! Willkommen in der Welt der Spring & Hibernate-Entwicklung. Fast jeder, 
der mit diesem Stack arbeitet, läuft früher oder später in genau diesen Fehler.

Keine Sorge, das Problem ist gut verständlich und die Lösung führt dich zu einer noch besseren und 
stabileren API-Architektur.

### Das Problem: Lazy Loading vs. JSON-Serialisierung
1. **Lazy Loading:** In deiner `Task`-Entity hast du `@ManyToOne(fetch = FetchType.LAZY)`. Das ist gut und performant! 
Es bedeutet: "Wenn du einen Task aus der Datenbank lädst, lade den zugehörigen `User` nicht sofort mit. 
Lade ihn erst, wenn ich ihn wirklich brauche (z.B. durch Aufruf von `task.getReportedBy().getUsername())`."
2. **Hibernate-Proxys:** Um das zu ermöglichen, packt Hibernate nicht das echte `User`-Objekt in das 
`reportedBy`-Feld, sondern einen Platzhalter, einen sogenannten Proxy. Das ist ein spezielles Objekt, 
das so aussieht wie ein `User`, aber intern nur die ID des echten Users enthält. Erst bei Bedarf holt 
der Proxy die restlichen Daten aus der Datenbank.
3. **Jackson (Der JSON-Konverter):** Wenn du nun die Task-Liste über deine REST-API zurückgibst, kommt 
Jackson ins Spiel. Jackson soll deine Java-Objekte in JSON umwandeln. Es schaut sich das `Task`-Objekt an, 
sieht das `reportedBy`-Feld und findet darin diesen Hibernate-Proxy. 
4. **Der Fehler:** Jackson weiß nicht, was ein Hibernate-Proxy ist. Es sieht keine normalen Getter wie 
`getUsername()`, sondern nur interne Hibernate-Strukturen wie `hibernateLazyInitializer`. Da es keine 
Ahnung hat, wie es dieses seltsame Proxy-Objekt in JSON umwandeln soll, gibt es auf und wirft 
genau die `InvalidDefinitionException`, die du siehst.

### Die Lösung: Data Transfer Objects (DTOs)
Die beste und sauberste Lösung ist, deine Datenbank-Entitäten (`Task`, `User`) **niemals direkt** über die API 
preiszugeben. Stattdessen erstellst du spezielle Klassen, die nur die Daten enthalten, die du wirklich 
an das Frontend senden willst. Diese Klassen nennt man **Data Transfer Objects (DTOs)**.

Das löst nicht nur das Proxy-Problem, sondern hat weitere große Vorteile:
- **Sicherheit:** Du gibst niemals sensible Daten (wie z.B. das Passwort-Hash im User-Objekt) versehentlich preis.
- **Stabilität:** Deine API-Struktur ist von deiner Datenbank-Struktur entkoppelt. Du kannst deine Entitäten ändern, ohne sofort deine API zu brechen.
- **Performance:** Du sendest nur die Daten, die der Client wirklich braucht.

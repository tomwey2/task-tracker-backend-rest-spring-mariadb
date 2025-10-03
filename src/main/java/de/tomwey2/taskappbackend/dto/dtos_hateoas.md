## DTOs, Validierung und HATEOAS
### Teil 1: Das Konzept des Data Transfer Object (DTO)
Ein Data Transfer Object (DTO) ist ein einfaches Java-Objekt (POJO), dessen einziger Zweck es ist, Daten 
zwischen verschiedenen Schichten oder Teilen einer Anwendung zu transportieren. Im Kontext von Spring Boot 
und REST-APIs ist seine Hauptaufgabe, als "Datenvertrag" zwischen dem Client (z.B. einem Frontend) und dem 
Server zu dienen.

Die Kernidee ist die Trennung der internen Datenrepräsentation (Entity) von der externen 
Datenrepräsentation (API-Schnittstelle). Sie sollten niemals Ihre JPA-Entity-Klassen direkt über Ihre API 
nach außen freigeben.

#### Warum DTOs verwenden? Die Vorteile

1. **Sicherheit und Kapselung:** Sie verhindern, dass sensible Daten (z.B. Passwort-Hashes) versehentlich nach 
außen gelangen. Sie kontrollieren exakt, welche Felder sichtbar sind. 
2. **API-Stabilität:** Ihre interne Datenbankstruktur kann sich ändern, ohne 
dass die öffentliche API davon betroffen ist. Das entkoppelt Ihre API von Ihrem Datenbankschema. 
3. **Vermeidung von Lazy Loading Exceptions und Zirkelbezügen:** Entities haben oft LAZY-geladene Beziehungen. 
Wenn das Objekt zu JSON serialisiert wird, nachdem die Datenbank-Session geschlossen ist, führt dies 
zu Fehlern. DTOs umgehen dieses Problem. 
4. **Maßgeschneiderte Daten:** Sie können DTOs erstellen, die Daten aus mehreren Entities kombinieren oder nur 
eine kleine Teilmenge von Daten für einen bestimmten Anwendungsfall enthalten.
 
#### Validierung von Eingabedaten mit DTOs
DTOs sind der perfekte Ort, um die Validierung von eingehenden Daten zu definieren. Dazu verwendet 
man die Jakarta Bean Validation API (durch den Starter `spring-boot-starter-validation`).

**Beispiel: Ein DTO zur Erstellung eines neuen Benutzers**

    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;

    public class CreateBenutzerDto {
        @NotBlank(message = "Benutzername darf nicht leer sein.")
        @Size(min = 3, max = 20, message = "Benutzername muss zwischen 3 und 20 Zeichen lang sein.")
        private String benutzername;

        @NotBlank(message = "Passwort darf nicht leer sein.")
        @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein.")
        private String passwort;

        @NotBlank(message = "E-Mail darf nicht leer sein.")
        @Email(message = "Bitte eine gültige E-Mail-Adresse angeben.")
        private String email;
    
        // Getter, Setter, etc.
    }

**Im Controller wird die Validierung mit `@Valid` ausgelöst:**

    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateBenutzerDto benutzerDto) {
        // Wenn die Validierung fehlschlägt, gibt Spring automatisch einen HTTP 400 Fehler zurück.
        // Wenn erfolgreich, fahren Sie mit der Logik fort...
        // ...
        return ResponseEntity.created(...).build();
    }

### Teil 2: DTOs in HATEOAS-APIs
**HATEOAS (Hypermedia as the Engine of Application State)** ist ein Prinzip von REST, das besagt, dass eine 
API-Antwort nicht nur die Daten enthalten sollte, sondern auch Links zu Aktionen, die mit diesen Daten 
möglich sind. Dies macht die API "entdeckbar" und entkoppelt den Client von fest codierten URLs.

Spring HATEOAS ist die Bibliothek, die dieses Prinzip in Spring Boot umsetzt. DTOs sind die perfekten 
Kandidaten, um mit HATEOAS-Informationen (Links) angereichert zu werden.

#### Benötigte Abhängigkeit

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-hateoas</artifactId>
    </dependency>

#### Die Kernkomponenten
1. `RepresentationModel<T>`: Eine Basisklasse. Ihr DTO muss davon erben, um Links speichern zu können. 
2. `EntityModel<T>`: Ein Wrapper für ein einzelnes DTO-Objekt (`T`), der die Daten und eine Sammlung von 
Links enthält. 
3. `CollectionModel<T>`: Ein Wrapper für eine Sammlung von Objekten (`T`, meist `EntityModel<DTO>`), der 
ebenfalls Links enthalten kann. 
4. `RepresentationModelAssembler<T, D>`: Eine Komponente, die die wiederkehrende Logik kapselt, ein 
Domain-Objekt (`T`, z.B. die Entity) in ein Repräsentationsmodell (D, z.B. `EntityModel<DTO>`) umzuwandeln 
und die Links hinzuzufügen. 

#### Praxisbeispiel
**Schritt 1: DTO anpassen**

Das DTO zur Darstellung eines Benutzers erbt nun von RepresentationModel.

    import org.springframework.hateoas.RepresentationModel;

    // Das DTO wird nun zu einem Repräsentationsmodell
    public class BenutzerDto extends RepresentationModel<BenutzerDto> {
        private Long id;
        private String benutzername;
        private String email;
        // Getter, Setter...
    }

**Schritt 2: Den RepresentationModelAssembler erstellen**

Diese Komponente ist der empfohlene Weg, um die Link-Logik sauber vom Controller zu trennen.

    import org.springframework.hateoas.EntityModel;
    import org.springframework.hateoas.server.RepresentationModelAssembler;
    import org.springframework.stereotype.Component;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

    @Component
    public class BenutzerDtoAssembler implements RepresentationModelAssembler<Benutzer, EntityModel<BenutzerDto>> {

        // Annahme: Es gibt einen Mapper, der Benutzer-Entity in BenutzerDto umwandelt
        private final BenutzerMapper mapper = new BenutzerMapper(); 

        @Override
        public EntityModel<BenutzerDto> toModel(Benutzer benutzer) {
            // 1. Entity in DTO umwandeln
            BenutzerDto dto = mapper.toDto(benutzer);

            // 2. DTO in EntityModel verpacken und Links hinzufügen
            return EntityModel.of(dto,
                    // Link zur Ressource selbst ("self" link)
                    linkTo(methodOn(BenutzerController.class).getBenutzerById(benutzer.getId())).withSelfRel(),
                    // Link zur Sammlung aller Benutzer
                    linkTo(methodOn(BenutzerController.class).getAllBenutzer()).withRel("benutzer"));
        }
    }

**Schritt 3: Controller mit dem Assembler verwenden**

Der Controller wird nun viel schlanker. Er nutzt den injizierten Assembler, um die API-Antworten zu generieren.

    import org.springframework.hateoas.CollectionModel;
    import org.springframework.hateoas.EntityModel;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;
    import java.util.stream.Collectors;
    import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

    @RestController
    @RequestMapping("/users")
    public class BenutzerController {

        private final BenutzerRepository repository;
        private final BenutzerDtoAssembler assembler;

        public BenutzerController(BenutzerRepository repository, BenutzerDtoAssembler assembler) {
            this.repository = repository;
            this.assembler = assembler;
        }

        // Antwort für eine einzelne Ressource
        @GetMapping("/{id}")
        public ResponseEntity<EntityModel<BenutzerDto>> getBenutzerById(@PathVariable Long id) {
            return repository.findById(id)
                    .map(assembler::toModel) // Assembler anwenden
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        // Antwort für eine Sammlung von Ressourcen
        @GetMapping
        public ResponseEntity<CollectionModel<EntityModel<BenutzerDto>>> getAllBenutzer() {
            List<EntityModel<BenutzerDto>> benutzer = repository.findAll().stream()
                    .map(assembler::toModel) // Assembler für jedes Element anwenden
                    .collect(Collectors.toList());

            // CollectionModel erstellen und einen "self" Link für die Sammlung hinzufügen
            CollectionModel<EntityModel<BenutzerDto>> collectionModel = CollectionModel.of(benutzer,
                    linkTo(methodOn(BenutzerController.class).getAllBenutzer()).withSelfRel());
        
            return ResponseEntity.ok(collectionModel);
        }
    }

### Fazit
Die Kombination von DTOs und HATEOAS ist ein leistungsstarkes Muster:
- **DTOs** definieren einen stabilen und sicheren **Datenvertrag** für Ihre API.
- **HATEOAS** reichert diesen Vertrag mit **Navigationsmöglichkeiten (Links)** an, was Ihre API flexibler und 
für Clients einfacher zu verwenden macht.

Der `RepresentationModelAssembler` ist dabei das zentrale Werkzeug, um diese beiden Konzepte sauber und 
wartbar zu verbinden.
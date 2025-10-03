## Globales Exception Handling in Spring Boot
In einer REST-API ist es entscheidend, bei Fehlern konsistente und aussagekräftige Antworten an den 
Client zu senden. Anstatt jede Controller-Methode mit try-catch-Blöcken zu überladen, bietet Spring 
einen eleganten, zentralisierten Mechanismus zur Fehlerbehandlung: Controller-Advice.

Die Kernidee ist, eine globale Komponente zu erstellen, die alle Ausnahmen abfängt, die von den 
Controllern geworfen werden, und sie in standardisierte HTTP-Antworten umwandelt.

### Die Schlüssel-Annotationen
| Annotation | Erklärung                                                                                                                                                                                                                          |
| --- |------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------| 
| @RestControllerAdvice | Kennzeichnet eine Klasse als globalen Exception Handler speziell für REST-Controller. Es ist eine Kombination aus @ControllerAdvice und @ResponseBody, was bedeutet, dass die Rückgabewerte der Methoden direkt in den HTTP-Response-Body serialisiert werden (z.B. als JSON). | 
| @ExceptionHandler | Wird auf einer Methode innerhalb einer @RestControllerAdvice-Klasse platziert. Die Methode wird immer dann aufgerufen, wenn ein Controller eine Exception des in der Annotation angegebenen Typs (oder eines Subtyps davon) wirft. | 
| @ResponseStatus | Setzt den HTTP-Statuscode der Antwort, die von der Handler-Methode zurückgegeben wird.                                                                                                                                             |

### Schritt-für-Schritt-Anleitung
Wir bauen ein Beispiel, das eine benutzerdefinierte `ResourceNotFoundException` fängt und eine 
standardisierte JSON-Fehlermeldung mit einem `404 Not Found`-Status zurückgibt.

#### Schritt 1: Eine benutzerdefinierte Exception erstellen
Es ist eine gute Praxis, spezifische Exceptions für bestimmte Fehlerfälle zu erstellen.

    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.ResponseStatus;

    // Diese Annotation sorgt dafür, dass Spring standardmäßig einen 404-Status zurückgibt,
    // auch wenn wir keinen expliziten Handler dafür hätten.
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

#### Schritt 2: Ein DTO für die Fehlerantwort erstellen
Anstatt nur einen einfachen String zurückzugeben, definieren wir eine klare Struktur für unsere 
Fehlermeldungen.

    import java.time.LocalDateTime;

    public class ErrorResponseDto {
        private int statusCode;
        private String message;
        private LocalDateTime timestamp;

        // Konstruktor, Getter und Setter...

        public ErrorResponseDto(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
        // ... Getter
    }

#### Schritt 3: Der Controller, der die Exception wirft
Unser `ProduktController` wirft die `ResourceNotFoundException`, wenn ein Produkt nicht gefunden wird.

    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    public class ProduktController {

        @GetMapping("/produkte/{id}")
        public Produkt getProduktById(@PathVariable Long id) {
            // Simuliert eine Datenbankabfrage
            if (id > 100) {
                throw new ResourceNotFoundException("Produkt mit ID " + id + " nicht gefunden.");
            }
            // ... Logik, um ein Produkt zurückzugeben
            return new Produkt(id, "Beispielprodukt");
        }
    }

    // Dummy-Klasse für das Beispiel
    class Produkt { 
        public Long id; 
        public String name; 
        public Produkt(Long i, String n){
            id=i;
            name=n;
        }
    }

#### Schritt 4: Der globale Exception Handler (`@RestControllerAdvice`)
Dies ist die zentrale Komponente. Sie fängt die Exceptions und erstellt die `ErrorResponseDto`.

    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.ExceptionHandler;
    import org.springframework.web.bind.annotation.RestControllerAdvice;
    import org.springframework.web.context.request.WebRequest;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        // Dieser Handler wird speziell für ResourceNotFoundException aufgerufen.
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(
                ResourceNotFoundException ex, WebRequest request) {
        
            ErrorResponseDto errorDetails = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
            );
            return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        // Ein allgemeinerer Handler für andere Validierungsfehler (Beispiel)
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
                IllegalArgumentException ex, WebRequest request) {
            
            ErrorResponseDto errorDetails = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
            );
            return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        // Ein Fallback-Handler für alle anderen Exceptions.
        // Dieser sollte immer am spezifischsten sein (Exception.class).
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDto> handleGlobalException(
                Exception ex, WebRequest request) {
            
            ErrorResponseDto errorDetails = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ein unerwarteter interner Fehler ist aufgetreten."
            );
            return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

#### Ergebnis
Wenn ein Client nun die URL `/produkte/101` aufruft, erhält er nicht einen unstrukturierten Standardfehler, 
sondern eine saubere JSON-Antwort mit dem HTTP-Status 404:

    {
        "statusCode": 404,
        "message": "Produkt mit ID 101 nicht gefunden.",
        "timestamp": "2023-10-27T10:30:00.123456"
    }

#### Fazit
Ein globaler Exception Handler mit `@RestControllerAdvice` ist der Standardweg in modernen Spring Boot 
Anwendungen, um:
- **Controller sauber zu halten:** Die Fehlerbehandlungslogik wird aus den Controllern entfernt.
- **Konsistenz zu gewährleisten:** Alle Fehler-Antworten folgen der gleichen, vorhersehbaren Struktur.
- **Code-Duplizierung zu vermeiden:** Die Logik zur Fehlerbehandlung wird an einem einzigen, zentralen Ort 
geschrieben.
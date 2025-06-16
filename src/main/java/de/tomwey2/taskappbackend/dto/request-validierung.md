# Validierung
Ein reines "400 Bad Request" hilft dem Frontend-Entwickler oder dem API-Nutzer nicht weiter. 
Er muss wissen, welches Feld falsch war und warum.

Die Standard-Fehlermeldung von Spring Boot ist bewusst generisch gehalten. Wir müssen Spring anweisen, 
diese Validierungsfehler abzufangen und in einem von uns definierten, strukturierten Format zurückzugeben.

Der Mechanismus dafür ist ein globaler Exception Handler mit der Annotation @RestControllerAdvice.

## Das Konzept: @RestControllerAdvice
Eine mit @RestControllerAdvice annotierte Klasse ist wie ein globaler "Aufpasser" für alle deine @RestController. 
Innerhalb dieser Klasse kannst du Methoden definieren, die aufgerufen werden, wenn in einem beliebigen Controller eine bestimmte Exception auftritt.

Wenn die @Valid-Annotation fehlschlägt, wirft Spring im Hintergrund eine MethodArgumentNotValidException. 
Wir müssen also genau diese Exception abfangen.

## Schritt-für-Schritt-Anleitung
1. Einen GlobalExceptionHandler erstellen
2. Die Klasse mit @RestControllerAdvice annotieren
3. Den Handler für Validierungsfehler hinzufügen

## Wie es funktioniert
1. Ein Client sendet eine POST-Anfrage mit ungültigen Daten an deinen Controller (z.B. ein TaskRequestDto mit 
einem leeren title).
2. Die @Valid-Annotation im TaskController löst die Validierung aus.
3. Die Validierung schlägt fehl. Spring wirft intern eine MethodArgumentNotValidException.
4. Bevor Spring seine Standard-Fehlerseite generiert, schaut der @RestControllerAdvice nach, ob es eine passende 
@ExceptionHandler-Methode für diese Exception gibt.
5. Es findet unsere handleValidationExceptions-Methode.
6. Die Methode wird aufgerufen. Sie extrahiert alle Fehler, erstellt eine Map, die das Feld dem Fehler 
zuordnet (z.B. "title": "Title cannot be blank").
7. Die Methode verpackt diese Map in eine ResponseEntity mit dem HTTP-Status 400 Bad Request und sendet sie 
als Antwort an den Client.
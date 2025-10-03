## Spring Web MVC: Das Model-View-Controller Framework
Spring Web MVC ist das originale Web-Framework, das auf dem Spring Framework aufbaut. 
Es wurde entwickelt, um flexible und robust flexible Webanwendungen und RESTful APIs 
zu erstellen. Sein Design basiert vollständig auf dem Model-View-Controller (MVC) 
Entwurfsmuster.

### Das MVC-Entwurfsmuster
Das Muster trennt die Anwendungslogik in drei miteinander verbundene Komponenten, 
um die Zuständigkeiten klar aufzuteilen:
1. **Model:** Die Daten der Anwendung. Dies sind in der Regel POJOs (Plain Old Java 
Objects), die Informationen repräsentieren, die dem Benutzer angezeigt oder von 
ihm übermittelt werden. Das Model selbst enthält keine Geschäftslogik.
2. **View:** Die Benutzeroberfläche (UI). Ihre Aufgabe ist es, die Daten aus dem Model 
darzustellen. Beispiele sind eine HTML-Seite (oft mit Thymeleaf gerendert), 
ein PDF-Dokument oder JSON/XML-Daten.
3. **Controller:** Das Gehirn der Anwendung. Er empfängt die Benutzereingaben 
(HTTP-Anfragen), interagiert mit den Backend-Services, um das Model zu erstellen, 
und wählt dann die passende View aus, um die Antwort zu rendern.

### Der DispatcherServlet: Das Herz von Spring MVC
Das zentrale Element in Spring Web MVC ist der DispatcherServlet. Er fungiert als 
Front Controller. Das bedeutet, dass jede einzelne ankommende HTTP-Anfrage zuerst 
an den DispatcherServlet gesendet wird. Dieser orchestriert dann den gesamten 
Prozess der Anfrageverarbeitung.

**Ablauf einer Anfrage:**

1. Ein HTTP-Request trifft beim `DispatcherServlet` ein.
2. Der `DispatcherServlet` konsultiert den `HandlerMapping`, um herauszufinden, welcher 
`Controller` für diese spezifische Anfrage (z.B. basierend auf der URL) zuständig ist.
3. Der `DispatcherServlet` leitet die Anfrage an den entsprechenden `Controller` weiter.
4. Der `Controller` verarbeitet die Anfrage. Er kann Daten von einem Service lesen 
oder schreiben, validiert die Eingabe und packt die Ergebnisdaten in ein 
`Model`-Objekt. Am Ende gibt er einen logischen View-Namen als String 
zurück (z.B. `"user-details"`).
5. Der `DispatcherServlet` empfängt den logischen View-Namen und das Model. 
Er fragt den `ViewResolver`, um den logischen Namen in eine konkrete 
`View`-Implementierung aufzulösen (z.B. die Datei `user-details.html` 
im `templates`-Ordner).
6. Die ausgewählte `View` wird mit den Daten aus dem `Model` gerendert.
7. Das Ergebnis (z.B. eine fertige HTML-Seite) wird als HTTP-Response an den Client 
zurückgeschickt.

### Wichtige Annotationen
- `@Controller`: Kennzeichnet eine Klasse als Web-Controller. Solche Klassen geben 
typischerweise logische View-Namen zurück.
- `@RestController`: Eine Spezialisierung von `@Controller`. Sie wird für REST-APIs 
verwendet und kombiniert `@Controller` und `@ResponseBody`. Jede Methode gibt 
direkt Daten (z.B. JSON) in den Response Body zurück, anstatt einen View-Namen.
- `@RequestMapping`: Die universelle Annotation, um Anfragen auf Controller-Methoden 
abzubilden. Man kann die HTTP-Methode (GET, POST, etc.) und den Pfad angeben.
- `@GetMapping`, `@PostMapping`, etc.: Kurzformen für `@RequestMapping`, die bereits auf 
eine spezifische HTTP-Methode festgelegt sind.@RequestParam: Bindet einen 
HTTP-Request-Parameter an einen Methodenparameter.
- `@PathVariable`: Bindet einen Wert aus dem URL-Pfad (z.B. `/users/{id}`) an einen 
Methodenparameter.
- `@ModelAttribute`: Bindet ein ganzes Objekt an das Model, das dann 
in der View verfügbar ist.
- `@ResponseBody`: Weist Spring an, den Rückgabewert einer Methode direkt in den 
HTTP-Response-Body zu schreiben (wird von @RestController automatisch angewendet).

### Code-Beispiel (@Controller für eine Web-UI)

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestParam;

    @Controller
    public class GreetingController {
        @GetMapping("/greeting")
        public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
            // 1. Controller-Logik: Fügt ein Attribut zum Model hinzu.
            model.addAttribute("name", name);
        
            // 2. Gibt den logischen View-Namen zurück.
            // Spring sucht nach einer Datei namens "greeting.html" (z.B. in src/main/resources/templates).
            return "greeting";
        }
    }

In diesem Beispiel verarbeitet der `GreetingController` eine Anfrage an `/greeting`, 
fügt den name-Parameter zum Model hinzu und gibt die Kontrolle an eine View namens 
`greeting` weiter, die diese Daten dann anzeigen kann.

## Dependency Injection (DI)
Dependency Injection ist ein zentrales Entwurfsmuster in Spring und der Mechanismus, der lose Kopplung 
zwischen Komponenten ermöglicht. Es ist eine Form von Inversion of Control (IoC).

**Die Grundidee:** Anstatt dass eine Komponente ihre Abhängigkeiten (andere Objekte, die sie benötigt) selbst 
erstellt, werden ihr diese Abhängigkeiten von außen "injiziert". In Spring wird diese Aufgabe vom 
IoC-Container (auch `ApplicationContext` genannt) übernommen.

**Warum ist das so wichtig?**
- **Entkopplung:** Klassen sind nicht mehr fest an eine bestimmte Implementierung ihrer Abhängigkeiten gebunden. 
Man arbeitet gegen Schnittstellen, was den Austausch von Implementierungen erleichtert.
- **Bessere Testbarkeit:** Beim Testen einer Klasse kann man einfach gefälschte oder 
"Mock"-Implementierungen ihrer Abhängigkeiten injizieren. Dies ermöglicht es, die Klasse isoliert 
zu testen, ohne das gesamte System hochfahren zu müssen.
- **Zentralisierte Konfiguration:** Die Erstellung und Verwaltung der Objekte (in Spring "Beans" genannt) 
wird vom Spring Framework übernommen.

**Wie funktioniert es in der Praxis?**
Sie deklarieren Objekte als Spring-verwaltete Beans (z.B. mit `@Component`, `@Service` oder `@Repository`) und 
teilen Spring dann mit, wo diese Beans benötigt werden (mit `@Autowired`).

Beispiel: Ein `OrderController` benötigt einen `OrderService`, um eine Bestellung zu bearbeiten.

    // Die Abhängigkeit (Dependency) wird als @Service deklariert.
    // Spring wird automatisch eine Instanz dieser Klasse erstellen und verwalten.
    @Service
    public class OrderService {
        public void processOrder(String product) {
            // Logik zur Verarbeitung der Bestellung...
            System.out.println("Bestellung für " + product + " wird bearbeitet.");
        }
    }
    
    // Die Komponente, die die Abhängigkeit benötigt.
    @RestController
    public class OrderController {
        // Private, finale Variable für den Service.
        private final OrderService orderService;

        // KONSTRUKTOR-INJECTION (empfohlene Methode):
        // Spring sieht, dass der Konstruktor einen OrderService benötigt.
        // Es sucht nach einer OrderService-Bean im Container und übergibt sie hier.
        @Autowired
        public OrderController(OrderService orderService) {
            this.orderService = orderService;
        }

        @GetMapping("/order")
        public String placeOrder(@RequestParam String product) {
            // Der Controller muss nicht wissen, wie der OrderService erstellt wird.
            // Er benutzt ihn einfach.
            orderService.processOrder(product);
            return "Bestellung für " + product + " aufgegeben!";
        }
    }

In diesem Beispiel erstellt der `OrderController` den `OrderService` nicht mit `new OrderService()`. 
Stattdessen wird ihm die Instanz von Spring über den Konstruktor bereitgestellt. Dies macht den 
Code flexibler und viel einfacher zu testen.

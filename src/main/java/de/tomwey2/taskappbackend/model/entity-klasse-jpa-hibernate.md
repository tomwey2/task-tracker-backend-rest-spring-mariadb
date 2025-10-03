## Das Konzept der Entity-Klasse (JPA/Hibernate)
Eine Entity-Klasse ist eine einfache Java-Klasse (ein POJO - Plain Old Java Object), die 
direkt auf eine Tabelle in einer relationalen Datenbank abgebildet wird. Jede Instanz 
dieser Klasse entspricht einer Zeile in dieser Tabelle, und jedes Feld (Attribut) der 
Klasse entspricht einer Spalte in der Tabelle.

Dieses Konzept ist der Kern des Object-Relational Mapping (ORM), einer Technik, die die 
Lücke zwischen der objektorientierten Welt von Java und der relationalen Welt von 
SQL-Datenbanken schließt.

### Die Rolle von JPA und Hibernate
- **JPA (Jakarta Persistence API):** Ist eine offizielle Spezifikation (ein Standard), die 
beschreibt, wie ORM in Java funktionieren soll. Sie definiert eine Reihe von Annotationen 
und Interfaces, die man zur Erstellung von Entity-Klassen und zur Interaktion mit der 
Datenbank verwendet.

- **Hibernate:** Ist die beliebteste und am weitesten verbreitete Implementierung der 
JPA-Spezifikation. Wenn Sie Spring Boot mit `spring-boot-starter-data-jpa` verwenden, 
nutzen Sie im Hintergrund Hibernate. Hibernate kümmert sich um die "Magie", die 
SQL-Abfragen zu generieren, die Java-Objekte in Datenbankzeilen umzuwandeln und umgekehrt.

### Anatomie einer Entity-Klasse
Um eine normale Java-Klasse in eine Entity zu verwandeln, verwendet man JPA-Annotationen.

| Annotation | Erklärung |
| --- | ---- |
| @Entity | Obligatorisch. Kennzeichnet die Klasse als JPA-Entity und teilt Hibernate mit, dass es eine zugehörige Tabelle in der Datenbank gibt. |
| @Table(name="...") | Optional. Gibt den genauen Namen der Datenbanktabelle an. Wenn nicht vorhanden, nimmt Hibernate den Klassennamen. | 
| @Id | Obligatorisch. Kennzeichnet das Feld, das den Primärschlüssel (PRIMARY KEY) der Tabelle darstellt. Jede Entity muss ein @Id-Feld haben. | 
| @GeneratedValue(...) | Gibt an, wie der Wert des Primärschlüssels generiert wird (z.B. automatisch von der Datenbank als AUTO_INCREMENT). | 
| @Column(name="...") | Optional. Gibt den genauen Namen der Spalte in der Datenbank an, wenn dieser vom Feldnamen abweicht. Man kann auch weitere Eigenschaften wie Länge, Null-Werte etc. definieren. | 
| @Transient | Kennzeichnet ein Feld, das nicht in der Datenbank gespeichert werden soll. Es existiert nur im Java-Objekt. | 

### Code-Beispiel: Eine Produkt-Entity
Stellen Sie sich eine Datenbanktabelle namens produkte mit den Spalten id, produkt_name 
und preis vor. Die entsprechende Entity-Klasse in Java würde so aussehen:

    import jakarta.persistence.Entity;
    import jakarta.persistence.GeneratedValue;
    import jakarta.persistence.GenerationType;
    import jakarta.persistence.Id;
    import jakarta.persistence.Table;
    import jakarta.persistence.Column;

    // 1. @Entity: Macht diese Klasse zu einer Datenbank-Entity
    @Entity
    // 2. @Table: Mappt die Klasse auf die Tabelle "produkte"
    @Table(name = "produkte")
    public class Produkt {
        // 3. @Id und @GeneratedValue: Definieren den Primärschlüssel
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY) // Strategie für Auto-Inkrement in MySQL/PostgreSQL
        private Long id;

        // 4. @Column: Mappt das Feld auf die Spalte "produkt_name"
        @Column(name = "produkt_name", nullable = false, length = 100)
        private String name;

        // Kein @Column nötig, da Feldname "preis" dem Spaltennamen entspricht
        private double preis;

        // WICHTIG: JPA erfordert einen parameterlosen Standardkonstruktor
        public Produkt() {
        }

        // Weitere Konstruktoren, Getter und Setter...
        public Produkt(String name, double preis) {
            this.name = name;
            this.preis = preis;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPreis() {
            return preis;
        }

        public void setPreis(double preis) {
            this.preis = preis;
        }
    }

### Wie werden Entities verwendet? (Spring Data JPA)
In einer modernen Spring Boot Anwendung interagieren Sie selten direkt mit dem 
EntityManager von Hibernate. Stattdessen verwenden Sie Spring Data JPA Repositories. 
Sie definieren einfach eine Schnittstelle, die von JpaRepository erbt:

    import org.springframework.data.jpa.repository.JpaRepository;

    public interface ProduktRepository extends JpaRepository<Produkt, Long> {
        // Spring Data JPA generiert automatisch CRUD-Methoden wie:
        // - save(Produkt produkt)
        // - findById(Long id)
        // - findAll()
        // - deleteById(Long id)
        //
        // Man kann auch eigene Abfragen definieren, z.B.:
        // List<Produkt> findByPreisGreaterThan(double preis);
    }

Wenn Sie dieses ProduktRepository in Ihren Service injizieren, können Sie Produkt-Objekte 
speichern, abrufen und löschen, ohne eine einzige Zeile SQL schreiben zu müssen. 
Hibernate erledigt die gesamte Arbeit im Hintergrund.

### Fazit
Entity-Klassen sind das Fundament von JPA und Hibernate. Sie ermöglichen es Ihnen, Ihre 
Datenbankstruktur direkt in Ihrem Java-Code auf eine typsichere und objektorientierte 
Weise abzubilden. Dies reduziert Boilerplate-Code drastisch, verhindert 
SQL-Injection-Fehler und macht Ihre Datenzugriffsschicht weitgehend unabhängig von 
der zugrundeliegenden Datenbank.
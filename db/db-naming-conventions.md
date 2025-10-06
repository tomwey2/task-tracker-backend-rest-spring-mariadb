# SQL Namenskonventionen
Es gibt keinen einzigen, offiziellen Standard, aber es haben sich im Laufe der Zeit sehr bewährte 
Praktiken (Best Practices) etabliert. Die wichtigste Regel von allen ist: Sei konsistent!

## Grundprinzipien
- **Klarheit & Lesbarkeit:** Namen sollten selbsterklärend sein. Ein anderer Entwickler (oder du selbst in 6 Monaten) 
sollte auf den ersten Blick verstehen, was in einer Tabelle oder Spalte gespeichert wird. Vermeide Abkürzungen 
(usr statt users).
- **Konsistenz:** Wähle einen Stil und bleibe im gesamten Projekt dabei. Mische nicht verschiedene Konventionen.
- **Einfachheit:** Halte Namen so kurz wie möglich, aber so lang wie nötig.
- **Vermeidung von reservierten Wörtern:** Jede SQL-Datenbank hat eine Liste von reservierten Wörtern 
(z.B. USER, ORDER, GROUP, TABLE). Die Verwendung dieser Wörter als Namen für Tabellen oder Spalten kann zu Fehlern 
führen und erfordert oft, dass die Namen in Anführungszeichen gesetzt werden, was umständlich ist. 

## Namenskonventionen für Tabellen
### 1. Format: snake_case
Verwende Kleinbuchstaben und trenne Wörter mit einem Unterstrich (_).

- **Gut:** user_profiles, order_details
- **Weniger gut:** UserProfiles (PascalCase), userProfiles (camelCase). snake_case ist plattformübergreifend am 
unproblematischsten, da einige Betriebssysteme bei der Dateispeicherung nicht zwischen Groß- und Kleinschreibung unterscheiden, was zu Problemen führen kann.

### 2. Die große Debatte: Singular oder Plural?
Dies ist die häufigste Diskussion. Beide Ansätze haben valide Argumente.

- **Argumente für Singular (user, task)**
    - **Logik:** Die Tabelle repräsentiert die Entität oder den Bauplan für einen einzelnen Datensatz. 
    Man sagt "die Tabelle user" als Repräsentation eines Benutzers.
    - **ORM-Freundlichkeit:** Objekt-relationale Mapper (wie JPA/Hibernate) mappen oft eine Klasse User auf eine 
    Tabelle user. Das fühlt sich sehr natürlich an.

- **Argumente für Plural (users, tasks)**
    - **Logik:** Die Tabelle ist eine Sammlung oder ein Behälter von mehreren Datensätzen. 
    Man sagt "die Tabelle users enthält alle Benutzer".
    - **SQL-Lesbarkeit:** SQL-Abfragen lesen sich oft natürlicher. SELECT id FROM users; liest sich für viele 
    wie ein normaler englischer Satz, im Gegensatz zu SELECT id FROM user;.

- **Empfehlung: Plural (users, tasks)** hat sich in den letzten Jahren, insbesondere durch den Einfluss von 
Frameworks wie Ruby on Rails oder Laravel, als etwas populärerer Standard durchgesetzt. Es ist eine sichere und weit verbreitete Wahl.

## Namenskonventionen für Spalten (Attribute)
### 1. Format: snake_case
Genau wie bei Tabellen ist snake_case hier der De-facto-Standard.
- **Gut:** first_name, order_date, created_at
- **Schlecht:** FirstName, firstName, first-name

### 2. Primärschlüssel (Primary Keys)
**Empfehlung:** Nenne den Primärschlüssel einfach id.
- **Vorteil:** Es ist kurz, einfach und universell verständlich. Jede Tabelle hat eine Spalte id. ORMs und andere 
Werkzeuge erkennen dies oft automatisch. 
- **Alternative:** tabellenname_id (z.B. in der Tabelle users der Primärschlüssel user_id). Das ist expliziter, 
aber oft unnötig lang.

### 3. Fremdschlüssel (Foreign Keys)
Hier gibt es eine sehr starke und nützliche Konvention:
- **Empfehlung:** singular_tabellenname_id
- Beispiel:
  - In deiner Tabelle tasks verweist eine Spalte auf die Tabelle users. 
  - Der Fremdschlüssel in tasks sollte user_id heißen. 
  - Vorteil: Man erkennt sofort, dass dies ein Fremdschlüssel ist und auf welche Tabelle er verweist. 
  Das ist extrem hilfreich bei JOIN-Operationen: ... ON tasks.user_id = users.id.

### 4. Booleans (Wahrheitswerte)
- **Empfehlung:** Verwende Präfixe wie is_, has_, can_.
- Beispiele: is_active, is_deleted, has_subscription, can_comment.
- Vorteil: Der Name beschreibt eine Frage, die mit Ja oder Nein beantwortet werden kann, 
was die Logik im Code verdeutlicht.

### 5. Daten und Zeitstempel
- **Empfehlung:** Verwende das Suffix _at für Zeitstempel (Datum + Uhrzeit) und _on für reine Datumsangaben.
- Beispiele:
  - created_at, updated_at (sehr verbreitet für Audit-Spalten)
  - deleted_at (für Soft Deletes)
  - published_on (wenn nur das Datum der Veröffentlichung wichtig ist)

## Zusammenfassung der Empfehlung ("House Style")
- **Tabellen:** Plural, snake_case (z.B. users, tasks, comments).
- **Spalten:** snake_case (z.B. first_name).
- **Primärschlüssel:** id.
- **Fremdschlüssel:** singular_tabellenname_id (z.B. user_id, task_id).
- **Booleans:** Präfix is_ oder has_ (z.B. is_admin).
- **Zeitstempel:** Suffix _at (z.B. created_at).
- **Datum:** Suffix _on (z.B. due_on).
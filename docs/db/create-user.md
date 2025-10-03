## in MariaDB einen User mit Namen "user" anlegen
Hier sind die Schritte, um in der MariaDB-Kommandozeile einen neuen Benutzer namens 
`user` anzulegen und ihm die notwendigen Rechte für die Datenbank zu geben.

### Schritt 1: An der MariaDB-Kommandozeile anmelden
Zuerst musst du dich als Benutzer mit administrativen Rechten (normalerweise root) an deiner Datenbank anmelden. Öffne dein Terminal oder deine Kommandozeile und gib Folgendes ein:

    # Meldet dich als Benutzer 'root' an und fragt nach dem Passwort
    mysql -u root -p
    
Du wirst zur Eingabe deines root-Passworts aufgefordert. Danach siehst du den MariaDB-Prompt, der etwa so aussieht: MariaDB [(none)]>.

### Schritt 2: Benutzer anlegen und Passwort vergeben
Jetzt legen wir den neuen Benutzer an. Es ist wichtig, ein sicheres Passwort zu wählen.

Gib den folgenden SQL-Befehl ein und drücke Enter:

    CREATE USER 'user'@'localhost' IDENTIFIED BY 'ein_sicheres_passwort';

#### Was bedeutet das?
- `CREATE USER 'user'`: Legt einen Benutzer mit dem Namen user an.
- `@'localhost'`: Dieser Teil ist wichtig. Er bedeutet, dass sich dieser Benutzer **nur vom selben 
Computer** (also `localhost`) aus mit der Datenbank verbinden darf. Das ist für eine Webanwendung, die auf 
demselben Server wie die Datenbank läuft, die sicherste Einstellung.
- `IDENTIFIED BY 'ein_sicheres_passwort'`: Ersetze `ein_sicheres_passwort` durch ein starkes Passwort deiner 
Wahl. Behalte die Anführungszeichen bei.

### Schritt 3: Berechtigungen für die Datenbank vergeben
Standardmäßig hat ein neuer Benutzer keinerlei Rechte. Er kann sich zwar anmelden, aber nichts sehen 
oder tun. Wir müssen ihm explizit die Rechte für die taskapp_db Datenbank geben.

    GRANT ALL PRIVILEGES ON taskapp_db.* TO 'user'@'localhost';

#### Was bedeutet das?
- `GRANT ALL PRIVILEGES`: Gibt dem Benutzer alle Standardrechte (wie SELECT, INSERT, UPDATE, DELETE, CREATE, 
ALTER usw.). Das ist für deine Spring Boot Anwendung mit ddl-auto=update notwendig, da die Anwendung 
Tabellen ändern können muss.
- `ON taskapp_db.*`: Beschränkt diese Rechte ausschließlich auf die Datenbank taskapp_db und alle ihre 
Tabellen (* steht für "alle Tabellen"). Der Benutzer kann also keine anderen Datenbanken auf dem Server 
sehen oder verändern.
- `TO 'user'@'localhost'`: Gibt diese Rechte an den Benutzer, den wir gerade erstellt haben.

### Schritt 4: Änderungen anwenden
Damit der Server die neuen Berechtigungen sofort übernimmt, solltest du folgenden Befehl ausführen:

    FLUSH PRIVILEGES;

### Schritt 5: Beenden
Du kannst die MariaDB-Kommandozeile nun verlassen:

    EXIT;

### Zusammenfassung der Befehle
Hier sind alle SQL-Befehle noch einmal kompakt zusammengefasst:

    -- 1. Benutzer 'user' anlegen, der sich nur von localhost verbinden darf
    CREATE USER 'user'@'localhost' IDENTIFIED BY 'ein_sicheres_passwort';

    -- 2. Dem Benutzer alle Rechte auf die Datenbank 'taskapp_db' geben
    GRANT ALL PRIVILEGES ON taskapp_db.* TO 'user'@'localhost';

    -- 3. Die Berechtigungstabelle neu laden, um die Änderungen zu aktivieren
    FLUSH PRIVILEGES;

### Nächster wichtiger Schritt
Vergiss nicht, deine application.properties Datei im Spring Boot Projekt mit den neuen Zugangsdaten 
zu aktualisieren:

**Öffne `src/main/resources/application.properties` und ändere:**

    # MariaDB Connection Settings
    spring.datasource.url=jdbc:mariadb://localhost:3306/taskapp_db
    spring.datasource.username=user
    spring.datasource.password=ein_sicheres_passwort
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

    # ... restliche Konfiguration

Jetzt wird deine taskapp-backend Anwendung den neuen, sichereren 
Datenbankbenutzer verwenden.


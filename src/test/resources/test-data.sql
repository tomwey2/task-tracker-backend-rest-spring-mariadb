-- Wir löschen alles, um einen sauberen Zustand zu garantieren
DELETE FROM tasks;
DELETE FROM projects;
DELETE FROM users;

-- Erstelle Test-Benutzer
-- WICHTIG: Passwörter müssen hier bereits gehasht sein! Dies ist ein BCrypt-Hash für "password123".
INSERT INTO users(id, username, email, password, role, created_at, updated_at)
VALUES (1, 'erika.muster', 'erika@test.com', '$2a$10$yyP6hApYzeLeqza7mVON3uB9B9L9Dtgs4b9QylmoulYQRnwB1QXJK', 'ROLE_USER', NOW(), NOW()),
       (2, 'max.power', 'max@test.com', '$2a$10$yyP6hApYzeLeqza7mVON3uB9B9L9Dtgs4b9QylmoulYQRnwB1QXJK', 'ROLE_USER', NOW(), NOW());

-- Erstelle Test-Projekte
INSERT INTO projects(id, name, created_at, updated_at)
VALUES (1, 'P1', NOW(), NOW());

-- Erstelle Test-Tasks
INSERT INTO tasks(id, title, description, state, belongs_to_project_id, reported_by_user_id, assigned_to_user_id, deadline, created_at, updated_at)
VALUES (1, 'Test Task 1', 'Beschreibung für Task 1', 'Open', 1, 1, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), NOW(), NOW()),
       (2, 'Test Task 2', 'Beschreibung für Task 2', 'Open', 1, 1, 2, DATE_ADD(CURDATE(), INTERVAL 10 DAY), NOW(), NOW());

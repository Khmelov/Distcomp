-- +goose Up
INSERT INTO Writer (
  login, password, firstname, lastname
) VALUES (
  'alex.sazonov.13@yandex.ru', 'password', 'Алексей', 'Сазонов'
) ON CONFLICT (login) DO NOTHING;

-- +goose Up
INSERT INTO tbl_writer (
  login, password, firstname, lastname
) VALUES (
  'alex.sazonov.13@yandex.ru', 'password', 'Алексей', 'Сазонов'
) ON CONFLICT (login) DO NOTHING;

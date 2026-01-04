-- Установка схемы по умолчанию
SET search_path TO distcomp;

-- Добавление первого пользователя (требование задания)
INSERT INTO tbl_user (login, password, firstname, lastname, created, modified)
VALUES (
    'su582004@gmail.com',
    'securepassword123',
    'Domina',
    'Cympanosau',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (login) DO NOTHING;
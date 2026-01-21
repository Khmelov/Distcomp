-- Автоматически создаст метки при запуске Spring Boot
INSERT INTO tbl_mark (name, created_at, modified_at)
VALUES ('red12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

INSERT INTO tbl_mark (name, created_at, modified_at)
VALUES ('green12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

INSERT INTO tbl_mark (name, created_at, modified_at)
VALUES ('blue12', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;
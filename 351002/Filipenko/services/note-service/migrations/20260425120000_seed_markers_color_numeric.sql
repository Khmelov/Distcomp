-- +goose Up
-- +goose StatementBegin
-- Автотесты ожидают маркеры red{N}, green{N}, blue{N} (N — суффикс прогона: 7, 41, …).
INSERT INTO tbl_marker (name)
SELECT color || n::text
FROM unnest(ARRAY['red', 'green', 'blue']) AS t(color),
     generate_series(0, 9999) AS n
ON CONFLICT (name) DO NOTHING;
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
DELETE FROM tbl_topic_marker tm
    USING tbl_marker m
    WHERE tm.marker_id = m.id
      AND m.name ~ '^(red|green|blue)[0-9]+$';
DELETE FROM tbl_marker WHERE name ~ '^(red|green|blue)[0-9]+$';
-- +goose StatementEnd

-- +goose Up
-- +goose StatementBegin
ALTER TABLE tbl_author
    ADD COLUMN IF NOT EXISTS role VARCHAR(16) NOT NULL DEFAULT 'CUSTOMER';

COMMENT ON COLUMN tbl_author.role IS 'ADMIN or CUSTOMER';
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
ALTER TABLE tbl_author DROP COLUMN IF EXISTS role;
-- +goose StatementEnd

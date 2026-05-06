-- +goose Up
-- +goose StatementBegin
ALTER TABLE distcomp.tbl_user
    ADD COLUMN IF NOT EXISTS role VARCHAR(16) NOT NULL DEFAULT 'CUSTOMER';
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
ALTER TABLE distcomp.tbl_user DROP COLUMN IF EXISTS role;
-- +goose StatementEnd

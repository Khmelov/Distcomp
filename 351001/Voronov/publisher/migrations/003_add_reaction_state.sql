-- +goose Up
-- +goose StatementBegin
ALTER TABLE distcomp.tbl_reaction
    ADD COLUMN IF NOT EXISTS state VARCHAR(16) NOT NULL DEFAULT 'PENDING';
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
ALTER TABLE distcomp.tbl_reaction DROP COLUMN IF EXISTS state;
-- +goose StatementEnd

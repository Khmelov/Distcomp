-- +goose Up
-- +goose StatementBegin
ALTER TABLE distcomp.tbl_issue ADD CONSTRAINT tbl_issue_title_unique UNIQUE (title);
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
ALTER TABLE distcomp.tbl_issue DROP CONSTRAINT IF EXISTS tbl_issue_title_unique;
-- +goose StatementEnd

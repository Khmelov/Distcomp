-- liquibase formatted sql

-- changeset author:add_state_column
ALTER TABLE tbl_notice ADD COLUMN state VARCHAR(20) NOT NULL DEFAULT 'PENDING';
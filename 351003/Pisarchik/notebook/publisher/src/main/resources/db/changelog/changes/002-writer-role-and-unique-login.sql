--liquibase formatted sql
--changeset notebook:002-1
ALTER TABLE distcomp.tbl_writer
    ADD COLUMN IF NOT EXISTS role VARCHAR(32) NOT NULL DEFAULT 'CUSTOMER';

--changeset notebook:002-2
UPDATE distcomp.tbl_writer
SET role = 'ADMIN'
WHERE id = 69;

--changeset notebook:002-3
CREATE UNIQUE INDEX IF NOT EXISTS uq_writer_login ON distcomp.tbl_writer (login);

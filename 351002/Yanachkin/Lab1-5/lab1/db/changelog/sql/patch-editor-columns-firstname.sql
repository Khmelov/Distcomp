-- Однократно для БД, созданных старым 001-schema.sql (first_name / last_name)
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'distcomp' AND table_name = 'tbl_editor' AND column_name = 'first_name'
  ) THEN
    ALTER TABLE distcomp.tbl_editor RENAME COLUMN first_name TO firstname;
  END IF;
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'distcomp' AND table_name = 'tbl_editor' AND column_name = 'last_name'
  ) THEN
    ALTER TABLE distcomp.tbl_editor RENAME COLUMN last_name TO lastname;
  END IF;
END $$;

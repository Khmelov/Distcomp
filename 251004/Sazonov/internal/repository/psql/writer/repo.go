package writer

import "github.com/jmoiron/sqlx"

type WriterRepo struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) *WriterRepo {
	return &WriterRepo{
		db: db,
	}
}

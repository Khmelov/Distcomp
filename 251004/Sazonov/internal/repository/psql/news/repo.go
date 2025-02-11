package news

import "github.com/jmoiron/sqlx"

type NewsRepo struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) *NewsRepo {
	return &NewsRepo{
		db: db,
	}
}

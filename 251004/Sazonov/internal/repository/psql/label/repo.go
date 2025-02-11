package label

import "github.com/jmoiron/sqlx"

type LabelRepo struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) *LabelRepo {
	return &LabelRepo{
		db: db,
	}
}

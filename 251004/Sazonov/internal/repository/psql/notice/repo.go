package notice

import "github.com/jmoiron/sqlx"

type NoticeRepo struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) *NoticeRepo {
	return &NoticeRepo{
		db: db,
	}
}

package storage

import (
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/issue"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/label"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/writer"
	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
)

type Storage struct {
	db *sqlx.DB

	Writer writer.Repo
	Issue  issue.Repo
	github.com/Khmelov/Distcomp/351001/Ushakov   post.Repo
	Label  label.Repo
}

func New() (Storage, error) {
	cfg := NewConfig()

	db, err := sqlx.Connect("postgres", cfg.DSN())
	if err != nil {
		return Storage{}, err
	}

	return Storage{
		db: db,

		Writer: writer.New(db),
		Issue:  issue.New(db),
		github.com / Khmelov / Distcomp / 351001 / Ushakov: post.New(db),
		Label: label.New(db),
	}, nil
}

func (p Storage) Close() {
	p.db.Close()
}

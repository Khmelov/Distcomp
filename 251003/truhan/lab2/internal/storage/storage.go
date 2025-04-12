package storage

import (
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage/author"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage/comment"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage/label"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage/tweet"
	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
)

type Storage struct {
	db *sqlx.DB

	Author  author.Repo
	Tweet   tweet.Repo
	Comment comment.Repo
	Label   label.Repo
}

func New() (Storage, error) {
	cfg := NewConfig()

	db, err := sqlx.Connect("postgres", cfg.DSN())
	if err != nil {
		return Storage{}, err
	}

	return Storage{
		db: db,

		Author:  author.New(db),
		Tweet:   tweet.New(db),
		Comment: comment.New(db),
		Label:   label.New(db),
	}, nil
}

func (p Storage) Close() {
	p.db.Close()
}

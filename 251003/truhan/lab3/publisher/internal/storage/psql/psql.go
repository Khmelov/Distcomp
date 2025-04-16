package psql

import (
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/psql/creator"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/psql/issue"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/psql/mark"

	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
)

type PSQL struct {
	db *sqlx.DB

	CreatorInst creator.Creator
	IssueInst   issue.Issue
	MarkInst    mark.Mark
}

func New() (*PSQL, error) {
	cfg := NewConfig()

	db, err := sqlx.Connect("postgres", cfg.DSN())
	if err != nil {
		return nil, err
	}

	log.Println("Connected to PostgreSQL")

	return &PSQL{
		db: db,

		CreatorInst: creator.New(db),
		IssueInst:   issue.New(db),
		MarkInst:    mark.New(db),
	}, nil
}

func (p *PSQL) Close() {
	if err := p.db.Close(); err != nil {
		log.Println("Error closing DB:", err)
	}
}

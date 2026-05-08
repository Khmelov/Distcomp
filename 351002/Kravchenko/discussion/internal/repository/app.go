package repository

import (
	"labs/discussion/internal/repository/note"

	"github.com/scylladb/gocqlx/v2"
)

type AppRepository interface {
	NoteRepo() note.Repository
}

func NewCas(session gocqlx.Session) AppRepository {
	return &appRepository{
		noteRepo: note.NewNoteCassandraRepository(session),
	}
}

type appRepository struct {
	noteRepo note.Repository
}

func (r *appRepository) NoteRepo() note.Repository {
	return r.noteRepo
}

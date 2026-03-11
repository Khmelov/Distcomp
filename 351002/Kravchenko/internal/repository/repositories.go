package repository

import (
	"database/sql"
	"lab1/internal/repository/editor"
	"lab1/internal/repository/issue"
	"lab1/internal/repository/note"
	"lab1/internal/repository/sticker"
)

type AppRepository interface {
	EditorRepo() editor.Repository
	IssueRepo() issue.Repository
	NoteRepo() note.Repository
	StickerRepo() sticker.Repository
}

func NewInMemory() AppRepository {
	stickerRepo := sticker.NewStickerInMemoryRepository()
	return &appRepository{
		editorRepo:  editor.NewEditorInMemoryRepository(),
		issueRepo:   issue.NewIssueInMemoryRepository(stickerRepo),
		noteRepo:    note.NewNoteInMemoryRepository(),
		stickerRepo: stickerRepo,
	}
}

func NewPg(db *sql.DB) AppRepository {
	return &appRepository{
		editorRepo:  editor.NewEditorPgRepository(db),
		issueRepo:   issue.NewIssuePgRepository(db),
		noteRepo:    note.NewNotePgRepository(db),
		stickerRepo: sticker.NewStickerPgRepository(db),
	}
}

type appRepository struct {
	editorRepo  editor.Repository
	issueRepo   issue.Repository
	noteRepo    note.Repository
	stickerRepo sticker.Repository
}

func (r *appRepository) EditorRepo() editor.Repository {
	return r.editorRepo
}

func (r *appRepository) IssueRepo() issue.Repository {
	return r.issueRepo
}

func (r *appRepository) NoteRepo() note.Repository {
	return r.noteRepo
}

func (r *appRepository) StickerRepo() sticker.Repository {
	return r.stickerRepo
}

package repository

import (
	"database/sql"
	"labs/publisher/internal/client"
	"labs/publisher/internal/repository/editor"
	"labs/publisher/internal/repository/issue"
	"labs/publisher/internal/repository/note"
	"labs/publisher/internal/repository/sticker"
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

func NewCas(db *sql.DB, disClient client.DiscussionClient) AppRepository {
	return &appRepository{
		editorRepo:  editor.NewEditorPgRepository(db),
		issueRepo:   issue.NewIssuePgRepository(db),
		noteRepo:    note.NewNoteRemoteRepository(disClient),
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

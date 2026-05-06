package repository

import (
	"database/sql"
	"labs/publisher/internal/client"
	"labs/publisher/internal/repository/editor"
	"labs/publisher/internal/repository/issue"
	"labs/publisher/internal/repository/note"
	"labs/publisher/internal/repository/sticker"
	"time"

	"github.com/redis/go-redis/v9"
)

type AppRepository interface {
	EditorRepo() editor.Repository
	IssueRepo() issue.Repository
	NoteRepo() note.Repository
	StickerRepo() sticker.Repository
}

type AppCache interface {
	EditorCache() editor.Cache
	IssueCache() issue.Cache
	NoteCache() note.Cache
	StickerCache() sticker.Cache
}

func NewCache(redisClient *redis.Client, ttl time.Duration) AppCache {
	return &appCache{
		editorCache:  editor.NewCacheRepository(redisClient, ttl),
		issueCache:   issue.NewCacheRepository(redisClient, ttl),
		noteCache:    note.NewCacheRepository(redisClient, ttl),
		stickerCache: sticker.NewCacheRepository(redisClient, ttl),
	}
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

type appCache struct {
	editorCache  editor.Cache
	issueCache   issue.Cache
	noteCache    note.Cache
	stickerCache sticker.Cache
}

func (r *appCache) EditorCache() editor.Cache {
	return r.editorCache
}

func (r *appCache) IssueCache() issue.Cache {
	return r.issueCache
}

func (r *appCache) NoteCache() note.Cache {
	return r.noteCache
}

func (r *appCache) StickerCache() sticker.Cache {
	return r.stickerCache
}

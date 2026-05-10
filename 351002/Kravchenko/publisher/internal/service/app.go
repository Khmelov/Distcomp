package service

import (
	"labs/publisher/internal/repository"
	"labs/publisher/internal/service/editor"
	"labs/publisher/internal/service/issue"
	"labs/publisher/internal/service/note"
	"labs/publisher/internal/service/sticker"
)

type AppService interface {
	EditorService() editor.Service
	IssueService() issue.Service
	NoteService() note.Service
	StickerService() sticker.Service
}

func New(repos repository.AppRepository, caches repository.AppCache) AppService {
	return &appService{
		editorService:  editor.New(repos, caches),
		issueService:   issue.New(repos, caches),
		noteService:    note.New(repos, caches),
		stickerService: sticker.New(repos, caches),
	}
}

type appService struct {
	editorService  editor.Service
	issueService   issue.Service
	noteService    note.Service
	stickerService sticker.Service
}

func (s *appService) EditorService() editor.Service {
	return s.editorService
}

func (s *appService) IssueService() issue.Service {
	return s.issueService
}

func (s *appService) NoteService() note.Service {
	return s.noteService
}

func (s *appService) StickerService() sticker.Service {
	return s.stickerService
}

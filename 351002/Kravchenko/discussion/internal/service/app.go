package service

import (
	"labs/discussion/internal/repository"
	"labs/discussion/internal/service/note"
)

type AppService interface {
	NoteService() note.Service
}

func New(repos repository.AppRepository) AppService {
	return &appService{
		noteService: note.New(repos),
	}
}

type appService struct {
	noteService note.Service
}

func (s *appService) NoteService() note.Service {
	return s.noteService
}

package service

import (
	"errors"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/dto/response"
	"github.com/bsuir/rest-api/internal/repository"
)

// NoteService предоставляет бизнес-логику для работы с заметками
type NoteService struct {
	repo     repository.NoteRepository
	issueRepo repository.IssueRepository
}

// NewNoteService создает новый экземпляр сервиса
func NewNoteService(repo repository.NoteRepository, issueRepo repository.IssueRepository) *NoteService {
	return &NoteService{
		repo:      repo,
		issueRepo: issueRepo,
	}
}

// Create создает новую заметку
func (s *NoteService) Create(req *request.NoteRequestTo) (*response.NoteResponseTo, error) {
	if err := s.validateNoteRequest(req); err != nil {
		return nil, err
	}
	// Проверяем существование задачи
	if _, err := s.issueRepo.GetByID(req.IssueID); err != nil {
		return nil, ErrInvalidIssueID
	}
	note := ToNoteDomain(req, GenerateID())
	created, err := s.repo.Create(note)
	if err != nil {
		return nil, err
	}
	return ToNoteResponse(created), nil
}

// GetByID получает заметку по ID
func (s *NoteService) GetByID(id int64) (*response.NoteResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	note, err := s.repo.GetByID(id)
	if err != nil {
		return nil, ErrNoteNotFound
	}
	return ToNoteResponse(note), nil
}

// GetAll получает все заметки
func (s *NoteService) GetAll() ([]*response.NoteResponseTo, error) {
	notes, err := s.repo.GetAll()
	if err != nil {
		return nil, err
	}
	responses := make([]*response.NoteResponseTo, len(notes))
	for i, note := range notes {
		responses[i] = ToNoteResponse(note)
	}
	return responses, nil
}

// Update обновляет заметку
func (s *NoteService) Update(id int64, req *request.NoteRequestTo) (*response.NoteResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	if err := s.validateNoteRequest(req); err != nil {
		return nil, err
	}
	// Проверяем существование задачи
	if _, err := s.issueRepo.GetByID(req.IssueID); err != nil {
		return nil, ErrInvalidIssueID
	}
	existing, err := s.repo.GetByID(id)
	if err != nil {
		return nil, ErrNoteNotFound
	}
	updatedNote := UpdateNoteDomain(existing, req)
	updated, err := s.repo.Update(id, updatedNote)
	if err != nil {
		return nil, ErrNoteNotFound
	}
	return ToNoteResponse(updated), nil
}

// Delete удаляет заметку
func (s *NoteService) Delete(id int64) error {
	if id == 0 {
		return errors.New("id is required")
	}
	err := s.repo.Delete(id)
	if err != nil {
		return ErrNoteNotFound
	}
	return nil
}

// GetByIssueID получает заметки по ID задачи
func (s *NoteService) GetByIssueID(issueID int64) ([]*response.NoteResponseTo, error) {
	notes, err := s.repo.GetByIssueID(issueID)
	if err != nil {
		return nil, err
	}
	responses := make([]*response.NoteResponseTo, len(notes))
	for i, note := range notes {
		responses[i] = ToNoteResponse(note)
	}
	return responses, nil
}

// validateNoteRequest валидирует запрос заметки
func (s *NoteService) validateNoteRequest(req *request.NoteRequestTo) error {
	if req.Content == "" {
		return errors.New("content is required")
	}
	if len(req.Content) < 2 || len(req.Content) > 2048 {
		return errors.New("content must be between 2 and 2048 characters")
	}
	if req.IssueID == 0 {
		return errors.New("issueId is required")
	}
	return nil
}


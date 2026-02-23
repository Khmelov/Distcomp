package service

import (
	"errors"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/dto/response"
	"github.com/bsuir/rest-api/internal/repository"
)

// MarkerService предоставляет бизнес-логику для работы с метками
type MarkerService struct {
	repo repository.MarkerRepository
}

// NewMarkerService создает новый экземпляр сервиса
func NewMarkerService(repo repository.MarkerRepository) *MarkerService {
	return &MarkerService{repo: repo}
}

// Create создает новую метку
func (s *MarkerService) Create(req *request.MarkerRequestTo) (*response.MarkerResponseTo, error) {
	if err := s.validateMarkerRequest(req); err != nil {
		return nil, err
	}
	marker := ToMarkerDomain(req, GenerateID())
	created, err := s.repo.Create(marker)
	if err != nil {
		return nil, err
	}
	return ToMarkerResponse(created), nil
}

// GetByID получает метку по ID
func (s *MarkerService) GetByID(id int64) (*response.MarkerResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	marker, err := s.repo.GetByID(id)
	if err != nil {
		return nil, ErrMarkerNotFound
	}
	return ToMarkerResponse(marker), nil
}

// GetAll получает все метки
func (s *MarkerService) GetAll() ([]*response.MarkerResponseTo, error) {
	markers, err := s.repo.GetAll()
	if err != nil {
		return nil, err
	}
	responses := make([]*response.MarkerResponseTo, len(markers))
	for i, marker := range markers {
		responses[i] = ToMarkerResponse(marker)
	}
	return responses, nil
}

// Update обновляет метку
func (s *MarkerService) Update(id int64, req *request.MarkerRequestTo) (*response.MarkerResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	if err := s.validateMarkerRequest(req); err != nil {
		return nil, err
	}
	existing, err := s.repo.GetByID(id)
	if err != nil {
		return nil, ErrMarkerNotFound
	}
	updatedMarker := UpdateMarkerDomain(existing, req)
	updated, err := s.repo.Update(id, updatedMarker)
	if err != nil {
		return nil, ErrMarkerNotFound
	}
	return ToMarkerResponse(updated), nil
}

// Delete удаляет метку
func (s *MarkerService) Delete(id int64) error {
	if id == 0 {
		return errors.New("id is required")
	}
	err := s.repo.Delete(id)
	if err != nil {
		return ErrMarkerNotFound
	}
	return nil
}

// GetByIssueID получает метки по ID задачи
func (s *MarkerService) GetByIssueID(issueID int64, issueRepo repository.IssueRepository) ([]*response.MarkerResponseTo, error) {
	issue, err := issueRepo.GetByID(issueID)
	if err != nil {
		return nil, errors.New("issue not found")
	}
	markers := make([]*response.MarkerResponseTo, 0)
	for _, markerID := range issue.MarkerIDs {
		marker, err := s.repo.GetByID(markerID)
		if err == nil {
			markers = append(markers, ToMarkerResponse(marker))
		}
	}
	return markers, nil
}

// validateMarkerRequest валидирует запрос метки
func (s *MarkerService) validateMarkerRequest(req *request.MarkerRequestTo) error {
	if req.Name == "" {
		return errors.New("name is required")
	}
	if len(req.Name) < 2 || len(req.Name) > 32 {
		return errors.New("name must be between 2 and 32 characters")
	}
	return nil
}


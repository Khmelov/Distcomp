package service

import (
	"errors"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/dto/response"
	"github.com/bsuir/rest-api/internal/repository"
)

// AuthorService предоставляет бизнес-логику для работы с авторами
type AuthorService struct {
	repo repository.AuthorRepository
}

// NewAuthorService создает новый экземпляр сервиса
func NewAuthorService(repo repository.AuthorRepository) *AuthorService {
	return &AuthorService{repo: repo}
}

// Create создает нового автора
func (s *AuthorService) Create(req *request.AuthorRequestTo) (*response.AuthorResponseTo, error) {
	if err := s.validateAuthorRequest(req); err != nil {
		return nil, err
	}
	author := ToAuthorDomain(req, GenerateID())
	created, err := s.repo.Create(author)
	if err != nil {
		return nil, err
	}
	return ToAuthorResponse(created), nil
}

// GetByID получает автора по ID
func (s *AuthorService) GetByID(id int64) (*response.AuthorResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	author, err := s.repo.GetByID(id)
	if err != nil {
		return nil, ErrAuthorNotFound
	}
	return ToAuthorResponse(author), nil
}

// GetAll получает всех авторов
func (s *AuthorService) GetAll() ([]*response.AuthorResponseTo, error) {
	authors, err := s.repo.GetAll()
	if err != nil {
		return nil, err
	}
	responses := make([]*response.AuthorResponseTo, len(authors))
	for i, author := range authors {
		responses[i] = ToAuthorResponse(author)
	}
	return responses, nil
}

// Update обновляет автора
func (s *AuthorService) Update(id int64, req *request.AuthorRequestTo) (*response.AuthorResponseTo, error) {
	if id == 0 {
		return nil, errors.New("id is required")
	}
	if err := s.validateAuthorRequest(req); err != nil {
		return nil, err
	}
	existing, err := s.repo.GetByID(id)
	if err != nil {
		return nil, ErrAuthorNotFound
	}
	updatedAuthor := UpdateAuthorDomain(existing, req)
	updated, err := s.repo.Update(id, updatedAuthor)
	if err != nil {
		return nil, ErrAuthorNotFound
	}
	return ToAuthorResponse(updated), nil
}

// Delete удаляет автора
func (s *AuthorService) Delete(id int64) error {
	if id == 0 {
		return errors.New("id is required")
	}
	err := s.repo.Delete(id)
	if err != nil {
		return ErrAuthorNotFound
	}
	return nil
}

// validateAuthorRequest валидирует запрос автора
func (s *AuthorService) validateAuthorRequest(req *request.AuthorRequestTo) error {
	if req.Login == "" {
		return errors.New("login is required")
	}
	if len(req.Login) < 2 || len(req.Login) > 64 {
		return errors.New("login must be between 2 and 64 characters")
	}
	if req.Password == "" {
		return errors.New("password is required")
	}
	if len(req.Password) < 8 || len(req.Password) > 128 {
		return errors.New("password must be between 8 and 128 characters")
	}
	if req.Firstname == "" {
		return errors.New("firstname is required")
	}
	if len(req.Firstname) < 2 || len(req.Firstname) > 64 {
		return errors.New("firstname must be between 2 and 64 characters")
	}
	if req.Lastname == "" {
		return errors.New("lastname is required")
	}
	if len(req.Lastname) < 2 || len(req.Lastname) > 64 {
		return errors.New("lastname must be between 2 and 64 characters")
	}
	return nil
}


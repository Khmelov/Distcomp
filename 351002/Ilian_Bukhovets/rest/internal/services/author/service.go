package author

import (
	"errors"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/storage/author"
	"math/rand/v2"
)

var (
	ErrInvalidBadRequest = errors.New("invalid request body")
	ErrAlreadyExists     = errors.New("author already exists")
)

type Service struct {
	storage *author.Storage
}

func NewService(storage *author.Storage) *Service {
	return &Service{storage: storage}
}

func checks(author *model.Author) error {
	if author.Login == "" || author.Password == "" || author.FirstName == "" || author.LastName == "" {
		return ErrInvalidBadRequest
	}

	if len(author.Login) < 2 || len(author.Login) > 64 {
		return ErrInvalidBadRequest
	}

	if len(author.Password) < 8 || len(author.Password) > 128 {
		return ErrInvalidBadRequest
	}

	if len(author.FirstName) < 2 || len(author.FirstName) > 64 {
		return ErrInvalidBadRequest
	}

	if len(author.LastName) < 2 || len(author.LastName) > 64 {
		return ErrInvalidBadRequest
	}

	return nil
}

func (s *Service) CreateAuthor(a *model.Author) error {
	if err := checks(a); err != nil {
		return err
	}

	a.ID = int64(rand.Int())

	err := s.storage.CreateAuthor(a)
	if err != nil {
		if errors.Is(err, author.ErrAlreadyExists) {
			return ErrAlreadyExists
		}

		return err
	}

	return nil
}

func (s *Service) GetAuthorByID(id int64) (*model.Author, error) {
	return s.storage.GetAuthorByID(id)
}

func (s *Service) GetAuthors() ([]*model.Author, error) {
	return s.storage.GetAuthors()
}

func (s *Service) UpdateAuthor(author *model.Author) error {
	if err := checks(author); err != nil {
		return err
	}

	user, err := s.storage.GetAuthorByID(author.ID)
	if err != nil {
		if user == nil {
			return errors.New("author not found")
		}

		return err
	}

	return s.storage.UpdateAuthor(author)
}

func (s *Service) DeleteAuthor(id int64) error {
	return s.storage.DeleteAuthor(id)
}

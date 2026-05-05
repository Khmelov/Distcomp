package issue

import (
	"errors"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/storage/issue"
	"math/rand/v2"
	"time"
)

type Service struct {
	storage *issue.Storage
}

var (
	ErrInvalidBody    = errors.New("invalid request body")
	ErrAlreadyExists  = errors.New("issue already exists")
	ErrAuthorNotFound = errors.New("issue not found")
)

func NewService(storage *issue.Storage) *Service {
	return &Service{storage: storage}
}

func checks(issue *model.Issue) error {
	if issue.AuthorID == 0 || issue.Title == "" || issue.Content == "" {
		return errors.New("author ID, title, and content are required")
	}

	if len(issue.Title) < 2 || len(issue.Title) > 64 {
		return ErrInvalidBody
	}

	if len(issue.Content) < 4 || len(issue.Content) > 2048 {
		return ErrInvalidBody
	}

	if issue.Created.IsZero() || issue.Modified.IsZero() {
		now := time.Now()
		if issue.Created.IsZero() {
			issue.Created = now
		}
		if issue.Modified.IsZero() {
			issue.Modified = now
		}
	}

	return nil
}

func (s *Service) CreateIssue(is *model.Issue) error {
	if err := checks(is); err != nil {
		return err
	}

	is.ID = int64(rand.Int()) % 1000

	if err := s.storage.CreateIssue(is); err != nil {
		if errors.Is(err, issue.ErrAlreadyExists) {
			return ErrAlreadyExists
		}

		if errors.Is(err, issue.ErrAuthorNotFound) {
			return ErrAuthorNotFound
		}

		return err
	}

	return nil
}

func (s *Service) GetIssueByID(id int64) (*model.Issue, error) {
	return s.storage.GetIssueByID(id)
}

func (s *Service) GetIssues() ([]*model.Issue, error) {
	return s.storage.GetIssues()
}

func (s *Service) UpdateIssue(issue *model.Issue) error {
	if issue.ID == 0 {
		return errors.New("issue ID is required")
	}
	existingIssue, err := s.storage.GetIssueByID(issue.ID)
	if err != nil {
		return err
	}

	if issue.AuthorID != existingIssue.AuthorID {
		return errors.New("issue author ID does not match")
	}

	if err := checks(issue); err != nil {
		return err
	}

	issue.Modified = time.Now()

	return s.storage.UpdateIssue(issue)
}

func (s *Service) DeleteIssue(id int64) error {
	return s.storage.DeleteIssue(id)
}

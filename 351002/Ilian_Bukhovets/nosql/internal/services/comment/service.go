package comment

import (
	"errors"
	"fmt"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/storage/comment"
	"math/rand"
)

type Service struct {
	storage *comment.Storage
}

var (
	ErrInvalidBody   = errors.New("invalid body")
	ErrIssueNotFound = errors.New("issue not found")
)

func checks(comment *model.Comment) error {
	if comment.IssueID == 0 || comment.Country == "" {
		return ErrInvalidBody
	}

	if len(comment.Country) < 2 {
		return ErrInvalidBody
	}

	return nil
}

func NewService(storage *comment.Storage) *Service {
	return &Service{storage: storage}
}

func (s *Service) CreateComment(c *model.Comment) error {
	if err := checks(c); err != nil {
		return err
	}

	c.ID = int64(rand.Int()) % 1000000

	fmt.Println(c.ID)

	if err := s.storage.CreateComment(c); err != nil {
		if errors.Is(err, comment.ErrIssueNotFound) {
			return ErrIssueNotFound
		}
		return err
	}

	return nil
}

func (s *Service) GetCommentByID(id int64) (*model.Comment, error) {
	return s.storage.GetCommentByID(id)
}

func (s *Service) GetComments() ([]*model.Comment, error) {
	return s.storage.GetComments()
}

func (s *Service) UpdateComment(comment *model.Comment) error {
	if comment.ID == 0 {
		return errors.New("comment ID is required")
	}
	existingComment, err := s.storage.GetCommentByID(comment.ID)
	if err != nil {
		return err
	}

	if existingComment == nil {
		return errors.New("comment not found")
	}

	if err := checks(comment); err != nil {
		return err
	}

	return s.storage.UpdateComment(comment)
}

func (s *Service) DeleteComment(id int64) error {
	return s.storage.DeleteComment(id)
}

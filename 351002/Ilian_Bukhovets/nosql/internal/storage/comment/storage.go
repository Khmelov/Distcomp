package comment

import (
	"errors"
	"fmt"

	"github.com/gocql/gocql"
	"gridusko_rest/internal/model"
)

type Storage struct {
	session *gocql.Session
}

var (
	ErrIssueNotFound = errors.New("issue not found")
)

func NewStorage(session *gocql.Session) *Storage {
	return &Storage{session: session}
}

func (s *Storage) CreateComment(comment *model.Comment) error {
	query := `
        INSERT INTO tbl_comment (id, issue_id, country)
        VALUES (?, ?, ?)
    `
	err := s.session.Query(query, comment.ID, comment.IssueID, comment.Country).Exec()
	if err != nil {
		return fmt.Errorf("failed to create comment: %w", err)
	}
	return nil
}

func (s *Storage) GetCommentByID(id int64) (*model.Comment, error) {
	var comment model.Comment
	query := `
        SELECT id, issue_id, country
        FROM tbl_comment
        WHERE id = ?
    `
	err := s.session.Query(query, id).Consistency(gocql.One).Scan(&comment.ID, &comment.IssueID, &comment.Country)
	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return nil, errors.New("comment not found")
		}
		return nil, fmt.Errorf("failed to get comment: %w", err)
	}

	return &comment, nil
}

func (s *Storage) GetComments() ([]*model.Comment, error) {
	var comments []*model.Comment
	query := `
        SELECT id, issue_id, country
        FROM tbl_comment
    `
	iter := s.session.Query(query).Iter()
	var comment model.Comment
	for iter.Scan(&comment.ID, &comment.IssueID, &comment.Country) {
		comments = append(comments, &comment)
	}
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to fetch comments: %w", err)
	}
	return comments, nil
}

func (s *Storage) UpdateComment(comment *model.Comment) error {
	query := `
        UPDATE tbl_comment
        SET country = ?
        WHERE id = ?
    `
	err := s.session.Query(query, comment.Country, comment.ID).Exec()
	if err != nil {
		return fmt.Errorf("failed to update comment: %w", err)
	}
	return nil
}

func (s *Storage) DeleteComment(id int64) error {
	query := `
        DELETE FROM tbl_comment
        WHERE id = ?
    `
	err := s.session.Query(query, id).Exec()
	if err != nil {
		return fmt.Errorf("failed to delete comment: %w", err)
	}
	return nil
}

func (s *Storage) GetCommentsByID(id int64) ([]model.Comment, error) {
	var comments []model.Comment
	query := `
        SELECT id, issue_id, country
        FROM tbl_comment
        WHERE issue_id = ?
    `
	iter := s.session.Query(query, id).Iter()
	var comment model.Comment
	for iter.Scan(&comment.ID, &comment.IssueID, &comment.Country) {
		comments = append(comments, comment)
	}
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to fetch comments by ID: %w", err)
	}
	return comments, nil
}

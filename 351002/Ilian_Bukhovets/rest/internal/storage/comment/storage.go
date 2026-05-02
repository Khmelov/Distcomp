package comment

import (
	"database/sql"
	"errors"
	"fmt"
	"github.com/lib/pq"
	"gridusko_rest/internal/model"

	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq" // PostgreSQL driver
)

type Storage struct {
	db *sqlx.DB
}

var (
	ErrIssueNotFound = errors.New("issue not found")
)

func NewStorage(db *sqlx.DB) *Storage {
	return &Storage{db: db}
}

func (s *Storage) CreateComment(comment *model.Comment) error {
	query := `
        INSERT INTO tbl_comment (id, issue_id, content)
        VALUES (:id, :issue_id, :content)
    `
	_, err := s.db.NamedExec(query, comment)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok {
			if pqErr.Code == "23503" {
				return ErrIssueNotFound
			}
		}
		return err
	}
	return nil
}

var prev = new(model.Comment)

func (s *Storage) GetCommentByID(id int64) (*model.Comment, error) {
	if prev == nil {
		fmt.Println("from cache")
		return prev, nil
	}

	var comment model.Comment
	query := `
        SELECT id, issue_id, content
        FROM tbl_comment
        WHERE id = $1
    `
	err := s.db.Get(&comment, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errors.New("comment not found")
		}
		return nil, err
	}

	prev = &comment

	return &comment, nil
}

func (s *Storage) GetComments() ([]*model.Comment, error) {
	var comments []*model.Comment
	query := `
		SELECT id, issue_id, content
		FROM tbl_comment
	`
	err := s.db.Select(&comments, query)
	if err != nil {
		return nil, err
	}
	return comments, nil
}

func (s *Storage) UpdateComment(comment *model.Comment) error {

	query := `
        UPDATE tbl_comment
        SET content = :content
        WHERE id = :id
    `
	result, err := s.db.NamedExec(query, comment)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("comment not found")
	}
	return nil
}

func (s *Storage) DeleteComment(id int64) error {
	query := `
        DELETE FROM tbl_comment
        WHERE id = $1
    `
	result, err := s.db.Exec(query, id)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("comment not found")
	}
	return nil
}

func (s *Storage) GetCommentsByID(id int64) ([]model.Comment, error) {
	var comments []model.Comment
	query := `
        SELECT id, issue_id, content
        FROM tbl_comment
        WHERE id = $1
    `
	err := s.db.Select(&comments, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return []model.Comment{}, nil
		}
		return nil, err
	}
	return comments, nil
}

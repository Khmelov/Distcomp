package issue

import (
	"database/sql"
	"errors"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
	_ "github.com/lib/pq"
	"gridusko_rest/internal/model"
	"strings"
)

type Storage struct {
	db *sqlx.DB
}

var (
	ErrAlreadyExists  = errors.New("issue already exists")
	ErrAuthorNotFound = errors.New("author not found")
)

func NewStorage(db *sqlx.DB) *Storage {
	return &Storage{db: db}
}

func (s *Storage) CreateIssue(issue *model.Issue) error {
	query := `
        INSERT INTO tbl_issue (id, author_id, title, content, created, modified)
        VALUES (:id, :author_id, :title, :content, :created, :modified)
    `
	_, err := s.db.NamedExec(query, issue)
	if err != nil {
		if strings.Contains(err.Error(), "violates unique constraint") {
			return ErrAlreadyExists
		}

		if pqErr, ok := err.(*pq.Error); ok {
			if pqErr.Code == "23503" {
				return ErrAuthorNotFound
			}
		}

		return err
	}

	return nil
}

func (s *Storage) GetIssueByID(id int64) (*model.Issue, error) {
	var issue model.Issue
	query := `
        SELECT id, author_id, title, content, created, modified
        FROM tbl_issue
        WHERE id = $1
    `
	err := s.db.Get(&issue, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errors.New("issue not found")
		}
		return nil, err
	}
	return &issue, nil
}

func (s *Storage) GetIssues() ([]*model.Issue, error) {
	var issues []*model.Issue
	query := `
		SELECT id, author_id, title, content, created, modified
		FROM tbl_issue
	`

	err := s.db.Select(&issues, query)
	if err != nil {
		return nil, err
	}
	return issues, nil
}

func (s *Storage) UpdateIssue(issue *model.Issue) error {
	query := `
        UPDATE tbl_issue
        SET title = :title, content = :content, modified = :modified
        WHERE id = :id
    `
	result, err := s.db.NamedExec(query, issue)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("issue not found")
	}
	return nil
}

func (s *Storage) DeleteIssue(id int64) error {
	query := `
        DELETE FROM tbl_issue
        WHERE id = $1
    `
	result, err := s.db.Exec(query, id)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("issue not found")
	}
	return nil
}

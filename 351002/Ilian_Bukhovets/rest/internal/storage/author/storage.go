package author

import (
	"database/sql"
	"errors"
	"gridusko_rest/internal/model"
	"strings"

	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
)

type Storage struct {
	db *sqlx.DB
}

func NewStorage(db *sqlx.DB) *Storage {
	return &Storage{db: db}
}

var (
	ErrAlreadyExists = errors.New("author already exists")
)

func (s *Storage) CreateAuthor(author *model.Author) error {
	query := `
        INSERT INTO tbl_author (id, login, password, firstname, lastname)
        VALUES (:id, :login, :password, :firstname, :lastname)
    `
	_, err := s.db.NamedExec(query, author)
	if err != nil {
		if strings.Contains(err.Error(), "violates unique constraint") {
			return ErrAlreadyExists
		}
		return err
	}
	return nil
}

func (s *Storage) GetAuthorByID(id int64) (*model.Author, error) {
	var author model.Author
	query := `
        SELECT id, login, password, firstname, lastname
        FROM tbl_author
        WHERE id = $1
    `
	err := s.db.Get(&author, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errors.New("author not found")
		}
		return nil, err
	}
	return &author, nil
}

func (s *Storage) GetAuthors() ([]*model.Author, error) {
	var authors []*model.Author
	query := `
		SELECT id, login, password, firstname, lastname
		FROM tbl_author
	`

	err := s.db.Select(&authors, query)
	if err != nil {
		return nil, err
	}
	return authors, nil
}

func (s *Storage) UpdateAuthor(author *model.Author) error {
	query := `
        UPDATE tbl_author
        SET login = :login, password = :password, firstname = :firstname, lastname = :lastname
        WHERE id = :id
    `
	result, err := s.db.NamedExec(query, author)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("author not found")
	}
	return nil
}

func (s *Storage) DeleteAuthor(id int64) error {
	query := `
        DELETE FROM tbl_author
        WHERE id = $1
    `
	result, err := s.db.Exec(query, id)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("author not found")
	}
	return nil
}

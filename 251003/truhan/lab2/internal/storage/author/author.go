package author

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrLoginExists    = fmt.Errorf("author with this login already exists")
	ErrAuthorNotFound = fmt.Errorf("author not found")
)

type Repo interface {
	Create(ctx context.Context, req model.Author) (model.Author, error)
	GetList(ctx context.Context) ([]model.Author, error)
	Get(ctx context.Context, id int64) (model.Author, error)
	Update(ctx context.Context, req model.Author) (model.Author, error)
	Delete(ctx context.Context, id int64) error
}

type repo struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) Repo {
	return repo{
		db: db,
	}
}

func (r repo) Create(ctx context.Context, req model.Author) (model.Author, error) {
	query := `INSERT INTO tbl_author (login, password, firstname, lastname) 
	          VALUES ($1, $2, $3, $4) RETURNING id`

	var id int64

	err := r.db.QueryRowContext(ctx, query, req.Login, req.Password, req.FirstName, req.LastName).Scan(&id)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23505" {
			return model.Author{}, ErrLoginExists
		}

		return model.Author{}, fmt.Errorf("failed to create Repo: %w", err)
	}

	req.ID = id

	return req, nil
}

func (r repo) GetList(ctx context.Context) ([]model.Author, error) {
	var result []model.Author

	query := `SELECT * FROM tbl_author`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.Author{}, nil
	}

	return result, nil
}

func (r repo) Get(ctx context.Context, id int64) (model.Author, error) {
	var result model.Author

	query := `SELECT id, login, password, firstname, lastname FROM tbl_author WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrAuthorNotFound
		}

		return result, fmt.Errorf("failed to retrieve Repo by ID: %w", err)
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.Author) (model.Author, error) {
	var result model.Author

	query := `UPDATE tbl_author SET login = $1, password = $2, firstname = $3, lastname = $4 
              WHERE id = $5 RETURNING id, login, password, firstname, lastname`

	err := r.db.QueryRowContext(ctx, query, req.Login, req.Password, req.FirstName, req.LastName, req.ID).
		Scan(&result.ID, &result.Login, &result.Password, &result.FirstName, &result.LastName)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrAuthorNotFound
		}

		return result, fmt.Errorf("failed to update Repo: %w", err)
	}

	return result, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_author WHERE id = $1`

	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("failed to delete Repo: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return ErrAuthorNotFound
	}

	return nil
}

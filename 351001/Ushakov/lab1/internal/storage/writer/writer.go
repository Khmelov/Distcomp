package writer

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrLoginExists    = fmt.Errorf("writer with this login already exists")
	ErrWriterNotFound = fmt.Errorf("writer not found")
)

type Repo interface {
	Create(ctx context.Context, req model.Writer) (model.Writer, error)
	GetList(ctx context.Context) ([]model.Writer, error)
	Get(ctx context.Context, id int64) (model.Writer, error)
	Update(ctx context.Context, req model.Writer) (model.Writer, error)
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

func (r repo) Create(ctx context.Context, req model.Writer) (model.Writer, error) {
	query := `INSERT INTO writers (login, password, firstname, lastname) 
	          VALUES ($1, $2, $3, $4) RETURNING id`

	var id int64

	err := r.db.QueryRowContext(ctx, query, req.Login, req.Password, req.FirstName, req.LastName).Scan(&id)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23505" {
			return model.Writer{}, ErrLoginExists
		}

		return model.Writer{}, fmt.Errorf("failed to create Repo: %w", err)
	}

	req.ID = id

	return req, nil
}

func (r repo) GetList(ctx context.Context) ([]model.Writer, error) {
	var result []model.Writer

	query := `SELECT * FROM writers`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.Writer{}, nil
	}

	return result, nil
}

func (r repo) Get(ctx context.Context, id int64) (model.Writer, error) {
	var result model.Writer

	query := `SELECT id, login, password, firstname, lastname FROM writers WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrWriterNotFound
		}

		return result, fmt.Errorf("failed to retrieve Repo by ID: %w", err)
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.Writer) (model.Writer, error) {
	var result model.Writer

	query := `UPDATE writers SET login = $1, password = $2, firstname = $3, lastname = $4 
              WHERE id = $5 RETURNING id, login, password, firstname, lastname`

	err := r.db.QueryRowContext(ctx, query, req.Login, req.Password, req.FirstName, req.LastName, req.ID).
		Scan(&result.ID, &result.Login, &result.Password, &result.FirstName, &result.LastName)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrWriterNotFound
		}

		return result, fmt.Errorf("failed to update Repo: %w", err)
	}

	return result, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM writers WHERE id = $1`

	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("failed to delete Repo: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return ErrWriterNotFound
	}

	return nil
}

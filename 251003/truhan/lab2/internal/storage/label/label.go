package label

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
	ErrCommentNotFound  = fmt.Errorf("comment not found")
	ErrConstraintsCheck = fmt.Errorf("invalid data passed")
)

type repo struct {
	db *sqlx.DB
}

type Repo interface {
	Create(ctx context.Context, req model.Label) (model.Label, error)
	GetList(ctx context.Context) ([]model.Label, error)
	Get(ctx context.Context, id int64) (model.Label, error)
	Update(ctx context.Context, req model.Label) (model.Label, error)
	Delete(ctx context.Context, id int64) error
}

func New(db *sqlx.DB) Repo {
	return repo{
		db: db,
	}
}

func (r repo) Create(ctx context.Context, req model.Label) (model.Label, error) {
	query := `INSERT INTO tbl_label (name) 
	          VALUES ($1) RETURNING id`

	var id int64

	err := r.db.QueryRowContext(ctx, query, req.Name).Scan(&id)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23505" {
			return model.Label{}, ErrConstraintsCheck
		}

		return model.Label{}, fmt.Errorf("failed to create Repo: %w", err)
	}

	req.ID = id

	return req, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_label WHERE id = $1`

	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("failed to delete Repo: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return ErrCommentNotFound
	}

	return nil
}

func (r repo) Get(ctx context.Context, id int64) (model.Label, error) {
	var result model.Label

	query := `SELECT * FROM tbl_label WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrCommentNotFound
		}
		return result, fmt.Errorf("failed to retrieve Repo by ID: %w", err)
	}

	return result, nil
}

func (r repo) GetList(ctx context.Context) ([]model.Label, error) {
	var result []model.Label

	query := `SELECT * FROM tbl_label`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.Label{}, nil
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.Label) (model.Label, error) {
	query := `UPDATE tbl_label SET name = $1
	          WHERE id = $2 RETURNING id, name`

	var result model.Label

	err := r.db.QueryRowContext(ctx, query, req.Name, req.ID).Scan(&result.ID, &result.Name)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrCommentNotFound
		}

		return result, fmt.Errorf("failed to update Repo: %w", err)
	}

	return result, nil
}

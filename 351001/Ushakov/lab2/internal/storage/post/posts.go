package post

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrPostNotFound      = fmt.Errorf("Repo not found")
	ErrFailedToUpdate    = fmt.Errorf("failed to update Repo")
	ErrFailedToDelete    = fmt.Errorf("failed to delete Repo")
	ErrInvalidForeignKey = fmt.Errorf("invalid foreign key passed")
)

type repo struct {
	db *sqlx.DB
}

type Repo interface {
	Create(ctx context.Context, req model.Post) (model.Post, error)
	GetList(ctx context.Context) ([]model.Post, error)
	Get(ctx context.Context, id int64) (model.Post, error)
	Update(ctx context.Context, req model.Post) (model.Post, error)
	Delete(ctx context.Context, id int64) error
}

func New(db *sqlx.DB) Repo {
	return repo{
		db: db,
	}
}

func (r repo) Create(ctx context.Context, req model.Post) (model.Post, error) {
	query := `INSERT INTO tbl_post (issue_id, content) 
	          VALUES ($1, $2) RETURNING id`

	var id int64

	err := r.db.QueryRowContext(ctx, query, req.IssueID, req.Content).Scan(&id)
	if err != nil {
		log.Println(err)
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23503" {
			return model.Post{}, ErrInvalidForeignKey
		}

		return model.Post{}, fmt.Errorf("failed to create Repo: %w", err)
	}

	req.ID = id

	return req, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_post WHERE id = $1`

	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("failed to delete Repo: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return ErrPostNotFound
	}

	return nil
}

func (r repo) Get(ctx context.Context, id int64) (model.Post, error) {
	var result model.Post

	query := `SELECT * FROM tbl_post WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrPostNotFound
		}

		return result, fmt.Errorf("failed to retrieve Repo by ID: %w", err)
	}

	return result, nil
}

func (r repo) GetList(ctx context.Context) ([]model.Post, error) {
	var result []model.Post

	query := `SELECT * FROM tbl_post`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.Post{}, nil
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.Post) (model.Post, error) {
	query := `UPDATE tbl_post SET issue_id = $1, content = $2
	          WHERE id = $3 RETURNING id, issue_id, content`

	var result model.Post

	err := r.db.QueryRowContext(ctx, query, req.IssueID, req.Content, req.ID).
		Scan(&result.ID, &result.IssueID, &result.Content)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrPostNotFound
		}

		return result, fmt.Errorf("failed to update Repo: %w", err)
	}

	return result, nil
}

package post

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	Errgithub.com/Khmelov/Distcomp/351001/UshakovNotFound = fmt.Errorf(
"Repo not found"
)
ErrFailedToUpdate    = fmt.Errorf("failed to update Repo")
ErrFailedToDelete = fmt.Errorf("failed to delete Repo")
ErrInvalidForeignKey = fmt.Errorf("invalid foreign key passed")
)
type repo struct {
	db *sqlx.DB
}

type Repo interface {
	Create(ctx context.Context, req model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	GetList(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	Get(ctx context.Context, id int64) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	Update(ctx context.Context, req model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	Delete(ctx context.Context, id int64) error
}

func New(db *sqlx.DB) Repo {
	return repo{
		db: db,
	}
}

func (r repo) Create(ctx context.Context, req model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	query := `INSERT INTO posts (issue_id, content) 
	          VALUES ($1, $2) RETURNING id`

	var id int64

	err := r.db.QueryRowContext(ctx, query, req.IssueID, req.Content).Scan(&id)
	if err != nil {
		log.Println(err)
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23503" {
			return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, ErrInvalidForeignKey
		}

		return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, fmt.Errorf("failed to create Repo: %w", err)
	}

	req.ID = id

	return req, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM posts WHERE id = $1`

	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("failed to delete Repo: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return Errgithub.com / Khmelov / Distcomp / 351001 / UshakovNotFound
	}

	return nil
}

func (r repo) Get(ctx context.Context, id int64) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	var result model.github.com / Khmelov / Distcomp / 351001 / Ushakov

	query := `SELECT * FROM posts WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, Errgithub.com / Khmelov / Distcomp / 351001 / UshakovNotFound
		}

		return result, fmt.Errorf("failed to retrieve Repo by ID: %w", err)
	}

	return result, nil
}

func (r repo) GetList(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	var result []model.github.com / Khmelov / Distcomp / 351001 / Ushakov

	query := `SELECT * FROM posts`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, nil
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	query := `UPDATE posts SET issue_id = $1, content = $2
	          WHERE id = $3 RETURNING id, issue_id, content`

	var result model.github.com / Khmelov / Distcomp / 351001 / Ushakov

	err := r.db.QueryRowContext(ctx, query, req.IssueID, req.Content, req.ID).
		Scan(&result.ID, &result.IssueID, &result.Content)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, Errgithub.com / Khmelov / Distcomp / 351001 / UshakovNotFound
		}

		return result, fmt.Errorf("failed to update Repo: %w", err)
	}

	return result, nil
}

package comment

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrCommentNotFound   = fmt.Errorf("Repo not found")
	ErrFailedToUpdate    = fmt.Errorf("failed to update Repo")
	ErrFailedToDelete    = fmt.Errorf("failed to delete Repo")
	ErrInvalidForeignKey = fmt.Errorf("invalid foreign key passed")
)

type repo struct {
	db *sqlx.DB
}

type Repo interface {
	Create(ctx context.Context, req model.Comment) (model.Comment, error)
	GetList(ctx context.Context) ([]model.Comment, error)
	Get(ctx context.Context, id int64) (model.Comment, error)
	Update(ctx context.Context, req model.Comment) (model.Comment, error)
	Delete(ctx context.Context, id int64) error
}

func New(db *sqlx.DB) Repo {
	return repo{
		db: db,
	}
}

func (r repo) Create(ctx context.Context, req model.Comment) (model.Comment, error) {
	query := `INSERT INTO tbl_comment (tweet_id, content) 
	          VALUES ($1, $2) RETURNING id`

	var id int64

	err := r.db.QueryRowContext(ctx, query, req.TweetID, req.Content).Scan(&id)
	if err != nil {
		log.Println(err)
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23503" {
			return model.Comment{}, ErrInvalidForeignKey
		}

		return model.Comment{}, fmt.Errorf("failed to create Repo: %w", err)
	}

	req.ID = id

	return req, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_comment WHERE id = $1`

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

func (r repo) Get(ctx context.Context, id int64) (model.Comment, error) {
	var result model.Comment

	query := `SELECT * FROM tbl_comment WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrCommentNotFound
		}

		return result, fmt.Errorf("failed to retrieve Repo by ID: %w", err)
	}

	return result, nil
}

func (r repo) GetList(ctx context.Context) ([]model.Comment, error) {
	var result []model.Comment

	query := `SELECT * FROM tbl_comment`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.Comment{}, nil
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.Comment) (model.Comment, error) {
	query := `UPDATE tbl_comment SET tweet_id = $1, content = $2
	          WHERE id = $3 RETURNING id, tweet_id, content`

	var result model.Comment

	err := r.db.QueryRowContext(ctx, query, req.TweetID, req.Content, req.ID).
		Scan(&result.ID, &result.TweetID, &result.Content)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrCommentNotFound
		}

		return result, fmt.Errorf("failed to update Repo: %w", err)
	}

	return result, nil
}

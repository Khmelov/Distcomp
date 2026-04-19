package issue

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrIssueNotFound     = fmt.Errorf("issues not found")
	ErrInvalidIssueData  = fmt.Errorf("invalid issue data")
	ErrInvalidForeignKey = fmt.Errorf("invalid foreign key passed")
)

type Repo interface {
	Create(ctx context.Context, req model.Issue) (model.Issue, error)
	GetList(ctx context.Context) ([]model.Issue, error)
	Get(ctx context.Context, id int64) (model.Issue, error)
	Update(ctx context.Context, req model.Issue) (model.Issue, error)
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

func (r repo) Create(ctx context.Context, req model.Issue) (model.Issue, error) {
	query := `INSERT INTO issues (writer_id, title, content) 
	          VALUES ($1, $2, $3) RETURNING id, created`

	var id int64
	var created time.Time

	err := r.db.QueryRowContext(ctx, query, req.WriterID, req.Title, req.Content).Scan(&id, &created)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23503" {
			return model.Issue{}, ErrIssueNotFound
		}

		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23505" {
			return model.Issue{}, ErrInvalidIssueData
		}

		return model.Issue{}, fmt.Errorf("failed to create issue: %w", err)
	}

	req.ID = id
	req.Created = created

	return req, nil
}

func (r repo) GetList(ctx context.Context) ([]model.Issue, error) {
	var result []model.Issue

	query := `SELECT * FROM issues`

	err := r.db.SelectContext(ctx, &result, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve result: %w", err)
	}

	if len(result) == 0 {
		return []model.Issue{}, nil
	}

	return result, nil
}

func (r repo) Get(ctx context.Context, id int64) (model.Issue, error) {
	var result model.Issue

	query := `SELECT id, writer_id, title, content, created, modified FROM issues WHERE id = $1`

	err := r.db.GetContext(ctx, &result, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrIssueNotFound
		}

		return result, fmt.Errorf("failed to retrieve issue by ID: %w", err)
	}

	return result, nil
}

func (r repo) Update(ctx context.Context, req model.Issue) (model.Issue, error) {
	var result model.Issue

	query := `UPDATE issues SET writer_id = $1, title = $2, content = $3, modified = $4 
	          WHERE id = $5 RETURNING id, writer_id, title, content, created, modified`

	err := r.db.QueryRowContext(ctx, query, req.WriterID, req.Title, req.Content, req.Modified, req.ID).
		Scan(&result.ID, &result.WriterID, &result.Title, &result.Content, &result.Created, &result.Modified)

	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return result, ErrIssueNotFound
		}

		return result, fmt.Errorf("failed to update issue: %w", err)
	}

	return result, nil
}

func (r repo) Delete(ctx context.Context, id int64) error {
	query := `DELETE FROM issues WHERE id = $1`

	result, err := r.db.ExecContext(ctx, query, id)
	if err != nil {
		return fmt.Errorf("failed to delete issue: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		return ErrIssueNotFound
	}

	return nil
}

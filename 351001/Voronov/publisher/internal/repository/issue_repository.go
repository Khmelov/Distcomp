package repository

import (
	"context"
	"errors"
	"fmt"

	apperrors "publisher/internal/errors"
	"publisher/internal/model"

	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
)

type pgIssueRepository struct {
	pool *pgxpool.Pool
}

func NewIssueRepository(pool *pgxpool.Pool) IssueRepository {
	return &pgIssueRepository{pool: pool}
}

func (r *pgIssueRepository) FindByID(ctx context.Context, id int64) (*model.Issue, error) {
	query := "SELECT id, user_id, title, content, created, modified FROM distcomp.tbl_issue WHERE id = $1"
	var i model.Issue
	err := r.pool.QueryRow(ctx, query, id).Scan(&i.ID, &i.UserID, &i.Title, &i.Content, &i.Created, &i.Modified)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &i, nil
}

func (r *pgIssueRepository) FindAll(ctx context.Context, opts *QueryOptions) ([]*model.Issue, int64, error) {
	if opts == nil {
		opts = NewQueryOptions()
	}

	var total int64
	if err := r.pool.QueryRow(ctx, "SELECT COUNT(*) FROM distcomp.tbl_issue").Scan(&total); err != nil {
		return nil, 0, err
	}

	orderField, orderDir := sortParams(opts.Sort)
	offset := (opts.Pagination.Page - 1) * opts.Pagination.PageSize
	query := fmt.Sprintf(
		"SELECT id, user_id, title, content, created, modified FROM distcomp.tbl_issue ORDER BY %s %s LIMIT $1 OFFSET $2",
		orderField, orderDir,
	)

	rows, err := r.pool.Query(ctx, query, opts.Pagination.PageSize, offset)
	if err != nil {
		return nil, 0, err
	}
	defer rows.Close()

	items := make([]*model.Issue, 0)
	for rows.Next() {
		var i model.Issue
		if err := rows.Scan(&i.ID, &i.UserID, &i.Title, &i.Content, &i.Created, &i.Modified); err != nil {
			return nil, 0, err
		}
		items = append(items, &i)
	}
	return items, total, nil
}

func (r *pgIssueRepository) Create(ctx context.Context, issue *model.Issue) (*model.Issue, error) {
	query := "INSERT INTO distcomp.tbl_issue (user_id, title, content, created, modified) VALUES ($1, $2, $3, $4, $5) RETURNING id"
	var id int64
	err := r.pool.QueryRow(ctx, query, issue.UserID, issue.Title, issue.Content, issue.Created, issue.Modified).Scan(&id)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && (pgErr.Code == "23505" || pgErr.Code == "23503") {
			return nil, apperrors.ErrDuplicate
		}
		return nil, apperrors.FromDBError(err)
	}
	issue.ID = id
	return issue, nil
}

func (r *pgIssueRepository) Update(ctx context.Context, id int64, issue *model.Issue) (*model.Issue, error) {
	query := "UPDATE distcomp.tbl_issue SET user_id = $1, title = $2, content = $3, modified = $4 WHERE id = $5"
	result, err := r.pool.Exec(ctx, query, issue.UserID, issue.Title, issue.Content, issue.Modified, id)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && (pgErr.Code == "23505" || pgErr.Code == "23503") {
			return nil, apperrors.ErrDuplicate
		}
		return nil, apperrors.FromDBError(err)
	}
	if result.RowsAffected() == 0 {
		return nil, apperrors.ErrNotFound
	}
	return r.FindByID(ctx, id)
}

func (r *pgIssueRepository) Delete(ctx context.Context, id int64) error {
	result, err := r.pool.Exec(ctx, "DELETE FROM distcomp.tbl_issue WHERE id = $1", id)
	if err != nil {
		return apperrors.FromDBError(err)
	}
	if result.RowsAffected() == 0 {
		return apperrors.ErrNotFound
	}
	return nil
}

func (r *pgIssueRepository) FindByUserID(ctx context.Context, userID int64) ([]*model.Issue, error) {
	query := "SELECT id, user_id, title, content, created, modified FROM distcomp.tbl_issue WHERE user_id = $1"
	rows, err := r.pool.Query(ctx, query, userID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	items := make([]*model.Issue, 0)
	for rows.Next() {
		var i model.Issue
		if err := rows.Scan(&i.ID, &i.UserID, &i.Title, &i.Content, &i.Created, &i.Modified); err != nil {
			return nil, err
		}
		items = append(items, &i)
	}
	return items, nil
}

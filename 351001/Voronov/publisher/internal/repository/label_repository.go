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

type pgLabelRepository struct {
	pool *pgxpool.Pool
}

func NewLabelRepository(pool *pgxpool.Pool) LabelRepository {
	return &pgLabelRepository{pool: pool}
}

func (r *pgLabelRepository) FindByID(ctx context.Context, id int64) (*model.Label, error) {
	var l model.Label
	err := r.pool.QueryRow(ctx, "SELECT id, name FROM distcomp.tbl_label WHERE id = $1", id).Scan(&l.ID, &l.Name)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &l, nil
}

func (r *pgLabelRepository) FindByName(ctx context.Context, name string) (*model.Label, error) {
	var l model.Label
	err := r.pool.QueryRow(ctx, "SELECT id, name FROM distcomp.tbl_label WHERE name = $1", name).Scan(&l.ID, &l.Name)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &l, nil
}

func (r *pgLabelRepository) FindAll(ctx context.Context, opts *QueryOptions) ([]*model.Label, int64, error) {
	if opts == nil {
		opts = NewQueryOptions()
	}

	var total int64
	if err := r.pool.QueryRow(ctx, "SELECT COUNT(*) FROM distcomp.tbl_label").Scan(&total); err != nil {
		return nil, 0, err
	}

	orderField, orderDir := sortParams(opts.Sort)
	offset := (opts.Pagination.Page - 1) * opts.Pagination.PageSize
	query := fmt.Sprintf(
		"SELECT id, name FROM distcomp.tbl_label ORDER BY %s %s LIMIT $1 OFFSET $2",
		orderField, orderDir,
	)

	rows, err := r.pool.Query(ctx, query, opts.Pagination.PageSize, offset)
	if err != nil {
		return nil, 0, err
	}
	defer rows.Close()

	items := make([]*model.Label, 0)
	for rows.Next() {
		var l model.Label
		if err := rows.Scan(&l.ID, &l.Name); err != nil {
			return nil, 0, err
		}
		items = append(items, &l)
	}
	return items, total, nil
}

func (r *pgLabelRepository) Create(ctx context.Context, label *model.Label) (*model.Label, error) {
	var id int64
	err := r.pool.QueryRow(ctx, "INSERT INTO distcomp.tbl_label (name) VALUES ($1) RETURNING id", label.Name).Scan(&id)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, apperrors.ErrDuplicate
		}
		return nil, apperrors.FromDBError(err)
	}
	label.ID = id
	return label, nil
}

func (r *pgLabelRepository) Update(ctx context.Context, id int64, label *model.Label) (*model.Label, error) {
	result, err := r.pool.Exec(ctx, "UPDATE distcomp.tbl_label SET name = $1 WHERE id = $2", label.Name, id)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, apperrors.ErrDuplicate
		}
		return nil, apperrors.FromDBError(err)
	}
	if result.RowsAffected() == 0 {
		return nil, apperrors.ErrNotFound
	}
	return r.FindByID(ctx, id)
}

func (r *pgLabelRepository) Delete(ctx context.Context, id int64) error {
	result, err := r.pool.Exec(ctx, "DELETE FROM distcomp.tbl_label WHERE id = $1", id)
	if err != nil {
		return apperrors.FromDBError(err)
	}
	if result.RowsAffected() == 0 {
		return apperrors.ErrNotFound
	}
	return nil
}

func (r *pgLabelRepository) FindByIssueID(ctx context.Context, issueID int64) ([]*model.Label, error) {
	query := `SELECT l.id, l.name FROM distcomp.tbl_label l
		JOIN distcomp.tbl_issue_label il ON l.id = il.label_id
		WHERE il.issue_id = $1`
	rows, err := r.pool.Query(ctx, query, issueID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	items := make([]*model.Label, 0)
	for rows.Next() {
		var l model.Label
		if err := rows.Scan(&l.ID, &l.Name); err != nil {
			return nil, err
		}
		items = append(items, &l)
	}
	return items, nil
}

func (r *pgLabelRepository) FindIssuesByLabelID(ctx context.Context, labelID int64) ([]int64, error) {
	rows, err := r.pool.Query(ctx,
		"SELECT issue_id FROM distcomp.tbl_issue_label WHERE label_id = $1",
		labelID,
	)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var ids []int64
	for rows.Next() {
		var id int64
		if err := rows.Scan(&id); err != nil {
			return nil, err
		}
		ids = append(ids, id)
	}
	return ids, nil
}

func (r *pgLabelRepository) AddLabelToIssue(ctx context.Context, issueID, labelID int64) error {
	_, err := r.pool.Exec(ctx,
		"INSERT INTO distcomp.tbl_issue_label (issue_id, label_id) VALUES ($1, $2) ON CONFLICT DO NOTHING",
		issueID, labelID,
	)
	return err
}

func (r *pgLabelRepository) RemoveLabelFromIssue(ctx context.Context, issueID, labelID int64) error {
	_, err := r.pool.Exec(ctx,
		"DELETE FROM distcomp.tbl_issue_label WHERE issue_id = $1 AND label_id = $2",
		issueID, labelID,
	)
	return err
}

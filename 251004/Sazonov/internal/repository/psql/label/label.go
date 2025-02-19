package label

import (
	"context"
	"database/sql"
	stderrors "errors"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/jackc/pgerrcode"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/stackus/errors"
)

var (
	ErrLabelNotFound = errors.Wrap(errors.ErrNotFound, "label is not found")

	ErrLabelAlreadyExists = errors.Wrap(errors.ErrForbidden, "label already exists")
)

func (n *LabelRepo) GetLabel(ctx context.Context, id int64) (model.Label, error) {
	const query = `SELECT * FROM tbl_label WHERE id = $1 LIMIT 1`

	var label model.Label
	if err := n.db.GetContext(ctx, &label, query, id); err != nil {
		if stderrors.Is(err, sql.ErrNoRows) {
			return model.Label{}, ErrLabelNotFound
		}

		return model.Label{}, err
	}

	return label, nil
}

func (n *LabelRepo) ListLabels(ctx context.Context) ([]model.Label, error) {
	const query = `SELECT * FROM tbl_label`

	labels := []model.Label{}
	if err := n.db.SelectContext(ctx, &labels, query); err != nil {
		return nil, err
	}

	return labels, nil
}

func (n *LabelRepo) CreateLabel(ctx context.Context, args model.Label) (model.Label, error) {
	const query = `INSERT INTO tbl_label (
		name	
	) VALUES (
		:name
	) RETURNING *`

	rows, err := n.db.NamedQueryContext(ctx, query, args)
	if err != nil {
		var pgErr *pgconn.PgError

		if stderrors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return model.Label{}, ErrLabelAlreadyExists
		}

		return model.Label{}, err
	}
	defer rows.Close()

	var label model.Label

	for rows.Next() {
		if err := rows.StructScan(&label); err != nil {
			return model.Label{}, err
		}
	}

	if err := rows.Err(); err != nil {
		return model.Label{}, err
	}

	return label, nil
}

func (n *LabelRepo) UpdateLabel(ctx context.Context, args model.Label) (model.Label, error) {
	const query = `UPDATE tbl_label SET
		name = COALESCE(NULLIF(:name, ''), name)
	WHERE id = :id
	RETURNING *`

	rows, err := n.db.NamedQueryContext(ctx, query, args)
	if err != nil {
		var pgErr *pgconn.PgError

		if stderrors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return model.Label{}, ErrLabelAlreadyExists
		}

		return model.Label{}, err
	}
	defer rows.Close()

	if !rows.Next() {
		return model.Label{}, ErrLabelNotFound
	}

	var label model.Label
	if err := rows.StructScan(&label); err != nil {
		return model.Label{}, err
	}

	if err := rows.Err(); err != nil {
		return model.Label{}, err
	}

	return label, nil
}

func (n *LabelRepo) DeleteLabel(ctx context.Context, id int64) error {
	const query = `DELETE FROM tbl_label WHERE id = $1`

	result, err := n.db.ExecContext(ctx, query, id)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected != 1 {
		return ErrLabelNotFound
	}

	return nil
}

package notice

import (
	"context"
	"database/sql"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/jackc/pgerrcode"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/stackus/errors"
)

var (
	ErrNoticeNotFound = errors.Wrap(errors.ErrNotFound, "notice is not found")

	ErrNoticeAlreadyExists = errors.Wrap(errors.ErrAlreadyExists, "notice already exists")
)

func (n *NoticeRepo) GetNotice(ctx context.Context, id int64) (model.Notice, error) {
	const query = `SELECT * FROM Notice WHERE id = $1 LIMIT 1`

	var notice model.Notice
	if err := n.db.GetContext(ctx, &notice, query, id); err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return model.Notice{}, ErrNoticeNotFound
		}

		return model.Notice{}, err
	}

	return notice, nil
}

func (n *NoticeRepo) ListNotices(ctx context.Context) ([]model.Notice, error) {
	const query = `SELECT * FROM Notice`

	notices := []model.Notice{}
	if err := n.db.SelectContext(ctx, &notices, query); err != nil {
		return nil, err
	}

	return notices, nil
}

func (n *NoticeRepo) CreateNotice(ctx context.Context, args model.Notice) (model.Notice, error) {
	const query = `INSERT INTO Notice (
		newsId, content
	) VALUES (
		:newsid, :content
	) RETURNING *`

	rows, err := n.db.NamedQueryContext(ctx, query, args)
	if err != nil {
		var pgErr *pgconn.PgError

		if errors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return model.Notice{}, ErrNoticeAlreadyExists
		}

		return model.Notice{}, err
	}
	defer rows.Close()

	var notice model.Notice

	for rows.Next() {
		if err := rows.StructScan(&notice); err != nil {
			return model.Notice{}, err
		}
	}

	if err := rows.Err(); err != nil {
		return model.Notice{}, err
	}

	return notice, nil
}

func (n *NoticeRepo) UpdateNotice(ctx context.Context, args model.Notice) (model.Notice, error) {
	const query = `UPDATE Notice SET
		newsId = COALESCE(NULLIF(:newsid, 0), newsId),
		content = COALESCE(NULLIF(:content, ''), content)	
	WHERE id = :id
	RETURNING *`

	rows, err := n.db.NamedQueryContext(ctx, query, args)
	if err != nil {
		var pgErr *pgconn.PgError

		if errors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return model.Notice{}, ErrNoticeAlreadyExists
		}

		return model.Notice{}, err
	}
	defer rows.Close()

	if !rows.Next() {
		return model.Notice{}, ErrNoticeNotFound
	}

	var notice model.Notice
	if err := rows.StructScan(&notice); err != nil {
		return model.Notice{}, err
	}

	if err := rows.Err(); err != nil {
		return model.Notice{}, err
	}

	return notice, nil
}

func (n *NoticeRepo) DeleteNotice(ctx context.Context, id int64) error {
	const query = `DELETE FROM Notice WHERE id = $1`

	result, err := n.db.ExecContext(ctx, query, id)
	if err != nil {
		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected != 1 {
		return ErrNoticeNotFound
	}

	return nil
}

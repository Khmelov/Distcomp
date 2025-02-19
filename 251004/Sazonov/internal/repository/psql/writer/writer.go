package writer

import (
	"context"
	"database/sql"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/jackc/pgerrcode"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/stackus/errors"
)

var (
	ErrWriterNotFound = errors.Wrap(errors.ErrNotFound, "writer is not found")

	ErrWriterAlreadyExists = errors.Wrap(errors.ErrForbidden, "writer already exists")
)

func (w *WriterRepo) GetWriter(ctx context.Context, id int64) (model.Writer, error) {
	const query = `SELECT * FROM tbl_writer WHERE id = $1 LIMIT 1`

	var writer model.Writer
	if err := w.db.GetContext(ctx, &writer, query, id); err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return model.Writer{}, ErrWriterNotFound
		}

		return model.Writer{}, err
	}

	return writer, nil
}

func (w *WriterRepo) ListWriters(ctx context.Context) ([]model.Writer, error) {
	const query = `SELECT * FROM tbl_writer`

	writers := []model.Writer{}
	if err := w.db.SelectContext(ctx, &writers, query); err != nil {
		return []model.Writer{}, err
	}

	return writers, nil
}

func (w *WriterRepo) CreateWriter(ctx context.Context, args model.Writer) (model.Writer, error) {
	const query = `INSERT INTO tbl_writer (
		login, password, firstname, lastname
	) VALUES (
		:login, :password, :firstname, :lastname
	) RETURNING *`

	rows, err := w.db.NamedQueryContext(ctx, query, args)
	if err != nil {
		var pgErr *pgconn.PgError

		if errors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return model.Writer{}, ErrWriterAlreadyExists
		}

		return model.Writer{}, err
	}
	defer rows.Close()

	var writer model.Writer

	for rows.Next() {
		if err := rows.StructScan(&writer); err != nil {
			return model.Writer{}, err
		}
	}

	if err := rows.Err(); err != nil {
		return model.Writer{}, err
	}

	return writer, nil
}

func (w *WriterRepo) UpdateWriter(ctx context.Context, args model.Writer) (model.Writer, error) {
	const query = `UPDATE tbl_writer SET
		login = COALESCE(NULLIF(:login, ''), login),
		password = COALESCE(NULLIF(:password, ''), password),
		firstname = COALESCE(NULLIF(:firstname, ''), firstname),
		lastname = COALESCE(NULLIF(:lastname, ''), lastname)
	WHERE id = :id
	RETURNING *`

	rows, err := w.db.NamedQueryContext(ctx, query, args)
	if err != nil {
		var pgErr *pgconn.PgError

		if errors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return model.Writer{}, ErrWriterAlreadyExists
		}

		return model.Writer{}, err
	}
	defer rows.Close()

	if !rows.Next() {
		return model.Writer{}, ErrWriterNotFound
	}

	var writer model.Writer
	if err := rows.StructScan(&writer); err != nil {
		return model.Writer{}, err
	}

	if err := rows.Err(); err != nil {
		return model.Writer{}, err
	}

	return writer, nil
}

func (w *WriterRepo) DeleteWriter(ctx context.Context, id int64) error {
	const query = `DELETE FROM tbl_writer WHERE id = $1`

	result, err := w.db.ExecContext(ctx, query, id)
	if err != nil {
		var pgErr *pgconn.PgError

		if errors.As(err, &pgErr) && pgErr.Code == pgerrcode.UniqueViolation {
			return ErrWriterAlreadyExists
		}

		return err
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected != 1 {
		return ErrWriterNotFound
	}

	return nil
}

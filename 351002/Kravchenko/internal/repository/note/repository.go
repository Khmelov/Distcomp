package note

import (
	"context"
	"database/sql"
	"errors"

	sq "github.com/Masterminds/squirrel"

	notemodel "lab1/internal/model/note"
)

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

type notePgRepository struct {
	db *sql.DB
}

func NewNotePgRepository(db *sql.DB) Repository {
	return &notePgRepository{db: db}
}

func (r *notePgRepository) Create(ctx context.Context, note *notemodel.Note) (*notemodel.Note, error) {
	query, args, err := psql.Insert("tbl_note").
		Columns("issue_id", "content").
		Values(note.IssueID, note.Content).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return nil, err
	}

	err = r.db.QueryRowContext(ctx, query, args...).Scan(&note.ID)
	if err != nil {
		return nil, err
	}
	return note, nil
}

func (r *notePgRepository) GetByID(ctx context.Context, id int64) (*notemodel.Note, error) {
	query, args, err := psql.Select("id", "issue_id", "content").
		From("tbl_note").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var note notemodel.Note
	err = r.db.QueryRowContext(ctx, query, args...).Scan(&note.ID, &note.IssueID, &note.Content)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, notemodel.ErrNotFound
		}
		return nil, err
	}
	return &note, nil
}

func (r *notePgRepository) Update(ctx context.Context, note *notemodel.Note) error {
	query, args, err := psql.Update("tbl_note").
		Set("issue_id", note.IssueID).
		Set("content", note.Content).
		Where(sq.Eq{"id": note.ID}).
		ToSql()
	if err != nil {
		return err
	}

	res, err := r.db.ExecContext(ctx, query, args...)
	if err != nil {
		return err
	}

	rows, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if rows == 0 {
		return notemodel.ErrNotFound
	}
	return nil
}

func (r *notePgRepository) Delete(ctx context.Context, id int64) error {
	query, args, err := psql.Delete("tbl_note").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return err
	}

	res, err := r.db.ExecContext(ctx, query, args...)
	if err != nil {
		return err
	}

	rows, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if rows == 0 {
		return notemodel.ErrNotFound
	}
	return nil
}

func (r *notePgRepository) List(ctx context.Context, limit, offset int) ([]*notemodel.Note, error) {
	query, args, err := psql.Select("id", "issue_id", "content").
		From("tbl_note").
		OrderBy("id ASC").
		Limit(uint64(limit)).
		Offset(uint64(offset)).
		ToSql()
	if err != nil {
		return nil, err
	}

	rows, err := r.db.QueryContext(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var result []*notemodel.Note
	for rows.Next() {
		note := &notemodel.Note{}
		if err := rows.Scan(&note.ID, &note.IssueID, &note.Content); err != nil {
			return nil, err
		}
		result = append(result, note)
	}
	return result, rows.Err()
}

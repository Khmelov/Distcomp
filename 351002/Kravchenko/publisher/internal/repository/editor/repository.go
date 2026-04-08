package editor

import (
	"context"
	"database/sql"
	"errors"
	editormodel "labs/shared/model/editor"
	"strings"

	sq "github.com/Masterminds/squirrel"
)

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

type editorPgRepository struct {
	db *sql.DB
}

func NewEditorPgRepository(db *sql.DB) Repository {
	return &editorPgRepository{db: db}
}

func (r *editorPgRepository) Create(ctx context.Context, editor *editormodel.Editor) (*editormodel.Editor, error) {
	query, args, err := psql.Insert("tbl_editor").
		Columns("login", "password", "firstname", "lastname").
		Values(editor.Login, editor.Password, editor.Firstname, editor.Lastname).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return nil, err
	}

	err = r.db.QueryRowContext(ctx, query, args...).Scan(&editor.ID)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			return nil, editormodel.ErrLoginTaken
		}
		return nil, err
	}
	return editor, nil
}

func (r *editorPgRepository) GetByID(ctx context.Context, id int64) (*editormodel.Editor, error) {
	query, args, err := psql.Select("id", "login", "password", "firstname", "lastname").
		From("tbl_editor").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var e editormodel.Editor
	err = r.db.QueryRowContext(ctx, query, args...).Scan(&e.ID, &e.Login, &e.Password, &e.Firstname, &e.Lastname)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, editormodel.ErrNotFound
		}
		return nil, err
	}
	return &e, nil
}

func (r *editorPgRepository) GetByLogin(ctx context.Context, login string) (*editormodel.Editor, error) {
	query, args, err := psql.Select("id", "login", "password", "firstname", "lastname").
		From("tbl_editor").
		Where(sq.Eq{"login": login}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var e editormodel.Editor
	err = r.db.QueryRowContext(ctx, query, args...).Scan(&e.ID, &e.Login, &e.Password, &e.Firstname, &e.Lastname)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, editormodel.ErrNotFound
		}
		return nil, err
	}
	return &e, nil
}

func (r *editorPgRepository) Update(ctx context.Context, editor *editormodel.Editor) error {
	query, args, err := psql.Update("tbl_editor").
		Set("login", editor.Login).
		Set("password", editor.Password).
		Set("firstname", editor.Firstname).
		Set("lastname", editor.Lastname).
		Where(sq.Eq{"id": editor.ID}).
		ToSql()
	if err != nil {
		return err
	}

	res, err := r.db.ExecContext(ctx, query, args...)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			return editormodel.ErrLoginTaken
		}
		return err
	}

	rows, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if rows == 0 {
		return editormodel.ErrNotFound
	}
	return nil
}

func (r *editorPgRepository) Delete(ctx context.Context, id int64) error {
	query, args, err := psql.Delete("tbl_editor").
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
		return editormodel.ErrNotFound
	}
	return nil
}

func (r *editorPgRepository) List(ctx context.Context, limit, offset int) ([]*editormodel.Editor, error) {
	query, args, err := psql.Select("id", "login", "password", "firstname", "lastname").
		From("tbl_editor").
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

	var result []*editormodel.Editor
	for rows.Next() {
		e := &editormodel.Editor{}
		if err := rows.Scan(&e.ID, &e.Login, &e.Password, &e.Firstname, &e.Lastname); err != nil {
			return nil, err
		}
		result = append(result, e)
	}
	return result, rows.Err()
}

package auth

import (
	"context"
	"database/sql"
	"errors"
	editormodel "labs/shared/model/editorV2"
	"strings"

	sq "github.com/Masterminds/squirrel"
)

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

type authPgRepository struct {
	db *sql.DB
}

func NewAuthPgRepository(db *sql.DB) Repository {
	return &authPgRepository{db: db}
}

func (r *authPgRepository) GetByLogin(ctx context.Context, login string) (*editormodel.Editor, error) {
	query, args, err := psql.Select("id", "login", "password", "firstname", "lastname", "role").
		From("tbl_editor").
		Where(sq.Eq{"login": login}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var e editormodel.Editor
	err = r.db.QueryRowContext(ctx, query, args...).Scan(
		&e.ID, &e.Login, &e.Password, &e.Firstname, &e.Lastname, &e.Role,
	)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, editormodel.ErrNotFound
		}
		return nil, err
	}
	return &e, nil
}

func (r *authPgRepository) Register(ctx context.Context, e *editormodel.Editor) (*editormodel.Editor, error) {
	query, args, err := psql.Insert("tbl_editor").
		Columns("login", "password", "firstname", "lastname", "role").
		Values(e.Login, e.Password, e.Firstname, e.Lastname, e.Role).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return nil, err
	}

	err = r.db.QueryRowContext(ctx, query, args...).Scan(&e.ID)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			return nil, editormodel.ErrLoginTaken
		}
		return nil, err
	}
	return e, nil
}

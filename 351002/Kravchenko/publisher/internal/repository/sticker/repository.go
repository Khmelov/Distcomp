package sticker

import (
	"context"
	"database/sql"
	"errors"
	stickermodel "labs/shared/model/sticker"
	"strings"

	sq "github.com/Masterminds/squirrel"
)

var psql = sq.StatementBuilder.PlaceholderFormat(sq.Dollar)

type stickerPgRepository struct {
	db *sql.DB
}

func NewStickerPgRepository(db *sql.DB) Repository {
	return &stickerPgRepository{db: db}
}

func (r *stickerPgRepository) Create(ctx context.Context, sticker *stickermodel.Sticker) (*stickermodel.Sticker, error) {
	query, args, err := psql.Insert("tbl_sticker").
		Columns("name").
		Values(sticker.Name).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return nil, err
	}

	err = r.db.QueryRowContext(ctx, query, args...).Scan(&sticker.ID)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			return nil, stickermodel.ErrNameTaken
		}
		return nil, err
	}
	return sticker, nil
}

func (r *stickerPgRepository) GetByID(ctx context.Context, id int64) (*stickermodel.Sticker, error) {
	query, args, err := psql.Select("id", "name").
		From("tbl_sticker").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var sticker stickermodel.Sticker
	err = r.db.QueryRowContext(ctx, query, args...).Scan(&sticker.ID, &sticker.Name)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, stickermodel.ErrNotFound
		}
		return nil, err
	}
	return &sticker, nil
}

func (r *stickerPgRepository) GetByName(ctx context.Context, name string) (*stickermodel.Sticker, error) {
	query, args, err := psql.Select("id", "name").
		From("tbl_sticker").
		Where(sq.Eq{"name": name}).
		ToSql()
	if err != nil {
		return nil, err
	}

	var sticker stickermodel.Sticker
	err = r.db.QueryRowContext(ctx, query, args...).Scan(&sticker.ID, &sticker.Name)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, stickermodel.ErrNotFound
		}
		return nil, err
	}
	return &sticker, nil
}

func (r *stickerPgRepository) Update(ctx context.Context, sticker *stickermodel.Sticker) error {
	query, args, err := psql.Update("tbl_sticker").
		Set("name", sticker.Name).
		Where(sq.Eq{"id": sticker.ID}).
		ToSql()
	if err != nil {
		return err
	}

	res, err := r.db.ExecContext(ctx, query, args...)
	if err != nil {
		if strings.Contains(err.Error(), "23505") {
			return stickermodel.ErrNameTaken
		}
		return err
	}

	rows, err := res.RowsAffected()
	if err != nil {
		return err
	}
	if rows == 0 {
		return stickermodel.ErrNotFound
	}
	return nil
}

func (r *stickerPgRepository) Delete(ctx context.Context, id int64) error {
	query, args, err := psql.Delete("tbl_sticker").
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
		return stickermodel.ErrNotFound
	}
	return nil
}

func (r *stickerPgRepository) List(ctx context.Context, limit, offset int) ([]*stickermodel.Sticker, error) {
	query, args, err := psql.Select("id", "name").
		From("tbl_sticker").
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

	var result []*stickermodel.Sticker
	for rows.Next() {
		sticker := &stickermodel.Sticker{}
		if err := rows.Scan(&sticker.ID, &sticker.Name); err != nil {
			return nil, err
		}
		result = append(result, sticker)
	}
	return result, rows.Err()
}

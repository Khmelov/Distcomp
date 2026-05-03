package repository

import (
	"context"
	"errors"
	sq "github.com/Masterminds/squirrel"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"news-board/publisher/internal/domain/models"
)

type noticeRepo struct {
	db   *pgxpool.Pool
	psql sq.StatementBuilderType
}

func NewNoticeRepository(db *pgxpool.Pool) *noticeRepo {
	return &noticeRepo{
		db:   db,
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (r *noticeRepo) Create(ctx context.Context, notice *models.Notice) error {
	query, args, err := r.psql.
		Insert("tbl_notice").
		Columns("news_id", "content").
		Values(notice.NewsID, notice.Content).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return err
	}
	return r.db.QueryRow(ctx, query, args...).Scan(&notice.ID)
}

func (r *noticeRepo) GetByID(ctx context.Context, id int64) (*models.Notice, error) {
	query, args, err := r.psql.
		Select("id", "news_id", "content").
		From("tbl_notice").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	notice := &models.Notice{}
	err = row.Scan(&notice.ID, &notice.NewsID, &notice.Content)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return notice, err
}

func (r *noticeRepo) GetAll(ctx context.Context, limit, offset int) ([]models.Notice, error) {
	query, args, err := r.psql.
		Select("id", "news_id", "content").
		From("tbl_notice").
		OrderBy("id").
		Limit(uint64(limit)).
		Offset(uint64(offset)).
		ToSql()
	if err != nil {
		return nil, err
	}
	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var notices []models.Notice
	for rows.Next() {
		var n models.Notice
		if err := rows.Scan(&n.ID, &n.NewsID, &n.Content); err != nil {
			return nil, err
		}
		notices = append(notices, n)
	}
	return notices, rows.Err()
}

func (r *noticeRepo) Update(ctx context.Context, notice *models.Notice) (bool, error) {
	query, args, err := r.psql.
		Update("tbl_notice").
		Set("news_id", notice.NewsID).
		Set("content", notice.Content).
		Where(sq.Eq{"id": notice.ID}).
		ToSql()
	if err != nil {
		return false, err
	}
	tag, err := r.db.Exec(ctx, query, args...)
	if err != nil {
		return false, err
	}
	return tag.RowsAffected() > 0, nil
}

func (r *noticeRepo) Delete(ctx context.Context, id int64) (bool, error) {
	query, args, err := r.psql.
		Delete("tbl_notice").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return false, err
	}
	tag, err := r.db.Exec(ctx, query, args...)
	if err != nil {
		return false, err
	}
	return tag.RowsAffected() > 0, nil
}

func (r *noticeRepo) GetByNewsID(ctx context.Context, newsID int64) ([]models.Notice, error) {
	query, args, err := r.psql.
		Select("id", "news_id", "content").
		From("tbl_notice").
		Where(sq.Eq{"news_id": newsID}).
		OrderBy("id").
		ToSql()
	if err != nil {
		return nil, err
	}
	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var notices []models.Notice
	for rows.Next() {
		var n models.Notice
		if err := rows.Scan(&n.ID, &n.NewsID, &n.Content); err != nil {
			return nil, err
		}
		notices = append(notices, n)
	}
	return notices, rows.Err()
}

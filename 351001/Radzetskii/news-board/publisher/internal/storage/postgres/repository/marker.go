package repository

import (
	"context"
	"errors"

	sq "github.com/Masterminds/squirrel"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"news-board/publisher/internal/domain/models"
)

type markerRepo struct {
	db   *pgxpool.Pool
	psql sq.StatementBuilderType
}

func NewMarkerRepository(db *pgxpool.Pool) *markerRepo {
	return &markerRepo{
		db:   db,
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (r *markerRepo) Create(ctx context.Context, marker *models.Marker) error {
	query, args, err := r.psql.
		Insert("tbl_marker").
		Columns("name").
		Values(marker.Name).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return err
	}
	return r.db.QueryRow(ctx, query, args...).Scan(&marker.ID)
}

func (r *markerRepo) GetByID(ctx context.Context, id int64) (*models.Marker, error) {
	query, args, err := r.psql.
		Select("id", "name").
		From("tbl_marker").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	marker := &models.Marker{}
	err = row.Scan(&marker.ID, &marker.Name)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return marker, err
}

func (r *markerRepo) GetAll(ctx context.Context, limit, offset int) ([]models.Marker, error) {
	query, args, err := r.psql.
		Select("id", "name").
		From("tbl_marker").
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
	var markers []models.Marker
	for rows.Next() {
		var m models.Marker
		if err := rows.Scan(&m.ID, &m.Name); err != nil {
			return nil, err
		}
		markers = append(markers, m)
	}
	return markers, rows.Err()
}

func (r *markerRepo) Update(ctx context.Context, marker *models.Marker) (bool, error) {
	query, args, err := r.psql.
		Update("tbl_marker").
		Set("name", marker.Name).
		Where(sq.Eq{"id": marker.ID}).
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

func (r *markerRepo) Delete(ctx context.Context, id int64) (bool, error) {
	query, args, err := r.psql.
		Delete("tbl_marker").
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

func (r *markerRepo) GetByNewsID(ctx context.Context, newsID int64) ([]models.Marker, error) {
	query, args, err := r.psql.
		Select("m.id", "m.name").
		From("tbl_marker m").
		Join("tbl_news_marker nm ON nm.marker_id = m.id").
		Where(sq.Eq{"nm.news_id": newsID}).
		ToSql()
	if err != nil {
		return nil, err
	}
	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var markers []models.Marker
	for rows.Next() {
		var m models.Marker
		if err := rows.Scan(&m.ID, &m.Name); err != nil {
			return nil, err
		}
		markers = append(markers, m)
	}
	return markers, rows.Err()
}

func (r *markerRepo) GetByName(ctx context.Context, name string) (*models.Marker, error) {
	query, args, err := r.psql.
		Select("id", "name").
		From("tbl_marker").
		Where(sq.Eq{"name": name}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	marker := &models.Marker{}
	err = row.Scan(&marker.ID, &marker.Name)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return marker, err
}

func (r *markerRepo) CountNewsByMarker(ctx context.Context, markerID int64) (int, error) {
	query, args, err := r.psql.
		Select("COUNT(*)").
		From("tbl_news_marker").
		Where(sq.Eq{"marker_id": markerID}).
		ToSql()
	if err != nil {
		return 0, err
	}
	var count int
	err = r.db.QueryRow(ctx, query, args...).Scan(&count)
	return count, err
}

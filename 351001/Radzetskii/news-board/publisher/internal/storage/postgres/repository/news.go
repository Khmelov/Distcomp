package repository

import (
	"context"
	"errors"
	"strings"
	"time"

	sq "github.com/Masterminds/squirrel"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgconn"
	"github.com/jackc/pgx/v5/pgxpool"
	"news-board/publisher/internal/domain/models"
)

type newsRepo struct {
	db   *pgxpool.Pool
	psql sq.StatementBuilderType
}

func NewNewsRepository(db *pgxpool.Pool) *newsRepo {
	return &newsRepo{
		db:   db,
		psql: sq.StatementBuilder.PlaceholderFormat(sq.Dollar),
	}
}

func (r *newsRepo) Create(ctx context.Context, news *models.News) error {
	news.Created = time.Now()
	news.Modified = news.Created
	query, args, err := r.psql.
		Insert("tbl_news").
		Columns("user_id", "title", "content", "created", "modified").
		Values(news.UserID, news.Title, news.Content, news.Created, news.Modified).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return err
	}
	err = r.db.QueryRow(ctx, query, args...).Scan(&news.ID)
	if err != nil {
		return err
	}
	return nil
}

func (r *newsRepo) CreateWithMarkers(ctx context.Context, news *models.News, markerNames []string) error {
	tx, err := r.db.BeginTx(ctx, pgx.TxOptions{})
	if err != nil {
		return err
	}
	defer tx.Rollback(ctx)

	news.Created = time.Now()
	news.Modified = news.Created

	createNewsQuery, createNewsArgs, err := r.psql.
		Insert("tbl_news").
		Columns("user_id", "title", "content", "created", "modified").
		Values(news.UserID, news.Title, news.Content, news.Created, news.Modified).
		Suffix("RETURNING id").
		ToSql()
	if err != nil {
		return err
	}
	if err := tx.QueryRow(ctx, createNewsQuery, createNewsArgs...).Scan(&news.ID); err != nil {
		return err
	}

	for _, markerName := range markerNames {
		markerName = normalizeMarkerName(markerName)
		if markerName == "" {
			continue
		}

		var markerID int64
		getMarkerQuery, getMarkerArgs, err := r.psql.
			Select("id").
			From("tbl_marker").
			Where(sq.Eq{"name": markerName}).
			ToSql()
		if err != nil {
			return err
		}
		err = tx.QueryRow(ctx, getMarkerQuery, getMarkerArgs...).Scan(&markerID)
		if errors.Is(err, pgx.ErrNoRows) {
			createMarkerQuery, createMarkerArgs, err := r.psql.
				Insert("tbl_marker").
				Columns("name").
				Values(markerName).
				Suffix("RETURNING id").
				ToSql()
			if err != nil {
				return err
			}
			if err := tx.QueryRow(ctx, createMarkerQuery, createMarkerArgs...).Scan(&markerID); err != nil {
				var pgErr *pgconn.PgError
				if errors.As(err, &pgErr) && pgErr.Code == "23505" {
					if err := tx.QueryRow(ctx, getMarkerQuery, getMarkerArgs...).Scan(&markerID); err != nil {
						return err
					}
				} else {
					return err
				}
			}
		} else if err != nil {
			return err
		}

		linkQuery, linkArgs, err := r.psql.
			Insert("tbl_news_marker").
			Columns("news_id", "marker_id").
			Values(news.ID, markerID).
			Suffix("ON CONFLICT DO NOTHING").
			ToSql()
		if err != nil {
			return err
		}
		if _, err := tx.Exec(ctx, linkQuery, linkArgs...); err != nil {
			return err
		}
	}

	return tx.Commit(ctx)
}

func (r *newsRepo) GetByID(ctx context.Context, id int64) (*models.News, error) {
	query, args, err := r.psql.
		Select("id", "user_id", "title", "content", "created", "modified").
		From("tbl_news").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return nil, err
	}
	row := r.db.QueryRow(ctx, query, args...)
	news := &models.News{}
	err = row.Scan(&news.ID, &news.UserID, &news.Title, &news.Content, &news.Created, &news.Modified)
	if errors.Is(err, pgx.ErrNoRows) {
		return nil, nil
	}
	return news, err
}

func (r *newsRepo) GetAll(ctx context.Context, limit, offset int, filters map[string]interface{}, sort string) ([]models.News, error) {
	builder := r.psql.Select("id", "user_id", "title", "content", "created", "modified").
		From("tbl_news").
		Limit(uint64(limit)).
		Offset(uint64(offset))

	if val, ok := filters["user_id"]; ok {
		builder = builder.Where(sq.Eq{"user_id": val})
	}
	if val, ok := filters["title"]; ok {
		builder = builder.Where(sq.Eq{"title": val})
	}

	if sort != "" {
		builder = builder.OrderBy(sort)
	} else {
		builder = builder.OrderBy("id")
	}

	query, args, err := builder.ToSql()
	if err != nil {
		return nil, err
	}
	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var newsList []models.News
	for rows.Next() {
		var n models.News
		if err := rows.Scan(&n.ID, &n.UserID, &n.Title, &n.Content, &n.Created, &n.Modified); err != nil {
			return nil, err
		}
		newsList = append(newsList, n)
	}
	return newsList, rows.Err()
}

func (r *newsRepo) Update(ctx context.Context, news *models.News) (bool, error) {
	news.Modified = time.Now()
	query, args, err := r.psql.
		Update("tbl_news").
		Set("user_id", news.UserID).
		Set("title", news.Title).
		Set("content", news.Content).
		Set("modified", news.Modified).
		Where(sq.Eq{"id": news.ID}).
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

func (r *newsRepo) Delete(ctx context.Context, id int64) (bool, error) {
	query, args, err := r.psql.
		Delete("tbl_news").
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

func (r *newsRepo) DeleteWithCleanup(ctx context.Context, id int64) (bool, error) {
	tx, err := r.db.BeginTx(ctx, pgx.TxOptions{})
	if err != nil {
		return false, err
	}
	defer tx.Rollback(ctx)

	markerQuery, markerArgs, err := r.psql.
		Select("marker_id").
		From("tbl_news_marker").
		Where(sq.Eq{"news_id": id}).
		ToSql()
	if err != nil {
		return false, err
	}
	rows, err := tx.Query(ctx, markerQuery, markerArgs...)
	if err != nil {
		return false, err
	}

	var markerIDs []int64
	for rows.Next() {
		var markerID int64
		if err := rows.Scan(&markerID); err != nil {
			rows.Close()
			return false, err
		}
		markerIDs = append(markerIDs, markerID)
	}
	if err := rows.Err(); err != nil {
		rows.Close()
		return false, err
	}
	rows.Close()

	deleteNewsQuery, deleteNewsArgs, err := r.psql.
		Delete("tbl_news").
		Where(sq.Eq{"id": id}).
		ToSql()
	if err != nil {
		return false, err
	}
	deleteTag, err := tx.Exec(ctx, deleteNewsQuery, deleteNewsArgs...)
	if err != nil {
		return false, err
	}
	if deleteTag.RowsAffected() == 0 {
		return false, nil
	}

	for _, markerID := range markerIDs {
		countQuery, countArgs, err := r.psql.
			Select("COUNT(*)").
			From("tbl_news_marker").
			Where(sq.Eq{"marker_id": markerID}).
			ToSql()
		if err != nil {
			return false, err
		}

		var count int
		if err := tx.QueryRow(ctx, countQuery, countArgs...).Scan(&count); err != nil {
			return false, err
		}
		if count > 0 {
			continue
		}

		deleteMarkerQuery, deleteMarkerArgs, err := r.psql.
			Delete("tbl_marker").
			Where(sq.Eq{"id": markerID}).
			ToSql()
		if err != nil {
			return false, err
		}
		if _, err := tx.Exec(ctx, deleteMarkerQuery, deleteMarkerArgs...); err != nil {
			return false, err
		}
	}

	if err := tx.Commit(ctx); err != nil {
		return false, err
	}
	return true, nil
}

func (r *newsRepo) AddMarker(ctx context.Context, newsID, markerID int64) error {
	query, args, err := r.psql.
		Insert("tbl_news_marker").
		Columns("news_id", "marker_id").
		Values(newsID, markerID).
		ToSql()
	if err != nil {
		return err
	}
	_, err = r.db.Exec(ctx, query, args...)
	return err
}

func (r *newsRepo) RemoveMarker(ctx context.Context, newsID, markerID int64) (bool, error) {
	query, args, err := r.psql.
		Delete("tbl_news_marker").
		Where(sq.Eq{"news_id": newsID, "marker_id": markerID}).
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

func (r *newsRepo) GetMarkers(ctx context.Context, newsID int64) ([]models.Marker, error) {
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

func (r *newsRepo) Search(ctx context.Context, filters map[string]interface{}, limit, offset int) ([]models.News, error) {
	builder := r.psql.Select("n.id", "n.user_id", "n.title", "n.content", "n.created", "n.modified").
		From("tbl_news n").
		Distinct().
		Limit(uint64(limit)).
		Offset(uint64(offset))

	joinedNewsMarker := false
	if markerIDs, ok := filters["marker_ids"]; ok {
		if !joinedNewsMarker {
			builder = builder.Join("tbl_news_marker nm ON nm.news_id = n.id")
			joinedNewsMarker = true
		}
		builder = builder.Where(sq.Eq{"nm.marker_id": markerIDs})
	}

	if markerNames, ok := filters["marker_names"]; ok {
		if !joinedNewsMarker {
			builder = builder.Join("tbl_news_marker nm ON nm.news_id = n.id")
			joinedNewsMarker = true
		}
		builder = builder.
			Join("tbl_marker m ON m.id = nm.marker_id").
			Where(sq.Eq{"m.name": markerNames})
	}

	if userLogin, ok := filters["user_login"]; ok {
		builder = builder.Join("tbl_user u ON u.id = n.user_id").
			Where(sq.Eq{"u.login": userLogin})
	}

	if title, ok := filters["title"]; ok {
		builder = builder.Where(sq.Like{"n.title": "%" + title.(string) + "%"})
	}

	if content, ok := filters["content"]; ok {
		builder = builder.Where(sq.Like{"n.content": "%" + content.(string) + "%"})
	}

	query, args, err := builder.ToSql()
	if err != nil {
		return nil, err
	}

	rows, err := r.db.Query(ctx, query, args...)
	if err != nil {
		return nil, err
	}
	defer rows.Close()
	var newsList []models.News
	for rows.Next() {
		var n models.News
		if err := rows.Scan(&n.ID, &n.UserID, &n.Title, &n.Content, &n.Created, &n.Modified); err != nil {
			return nil, err
		}
		newsList = append(newsList, n)
	}
	return newsList, rows.Err()
}

func normalizeMarkerName(name string) string {
	return strings.TrimSpace(name)
}

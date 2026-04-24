package repository

import (
	"context"
	"sync/atomic"
	"time"

	"github.com/gocql/gocql"
	"news-board/discussion/internal/domain/models"
)

type noticeRepo struct {
	session   *gocql.Session
	idCounter atomic.Int64
}

func NewNoticeRepository(session *gocql.Session) *noticeRepo {
	repo := &noticeRepo{session: session}
	repo.idCounter.Store(time.Now().UnixNano())
	return repo
}

func (r *noticeRepo) Create(ctx context.Context, notice *models.Notice) error {
	notice.ID = r.idCounter.Add(1)
	return r.session.Query(
		"INSERT INTO tbl_notice (country, news_id, id, content) VALUES (?, ?, ?, ?)",
		notice.Country, notice.NewsID, notice.ID, notice.Content,
	).WithContext(ctx).Exec()
}

func (r *noticeRepo) GetAll(ctx context.Context, country string, limit, offset int) ([]models.Notice, error) {
	iter := r.session.Query(
		"SELECT country, news_id, id, content FROM tbl_notice WHERE country = ?",
		country,
	).WithContext(ctx).Iter()
	defer iter.Close()

	notices := make([]models.Notice, 0)
	var notice models.Notice
	index := 0
	for iter.Scan(&notice.Country, &notice.NewsID, &notice.ID, &notice.Content) {
		if index >= offset && len(notices) < limit {
			notices = append(notices, notice)
		}
		index++
		notice = models.Notice{}
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}
	return notices, nil
}

func (r *noticeRepo) GetAllAny(ctx context.Context, limit, offset int) ([]models.Notice, error) {
	iter := r.session.Query(
		"SELECT country, news_id, id, content FROM tbl_notice LIMIT ? ALLOW FILTERING",
		limit+offset,
	).WithContext(ctx).Iter()
	defer iter.Close()

	notices := make([]models.Notice, 0)
	var notice models.Notice
	index := 0
	for iter.Scan(&notice.Country, &notice.NewsID, &notice.ID, &notice.Content) {
		if index >= offset && len(notices) < limit {
			notices = append(notices, notice)
		}
		index++
		notice = models.Notice{}
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}
	return notices, nil
}

func (r *noticeRepo) GetByID(ctx context.Context, country string, newsID, id int64) (*models.Notice, error) {
	var notice models.Notice
	err := r.session.Query(
		"SELECT country, news_id, id, content FROM tbl_notice WHERE country = ? AND news_id = ? AND id = ? LIMIT 1",
		country, newsID, id,
	).WithContext(ctx).Scan(&notice.Country, &notice.NewsID, &notice.ID, &notice.Content)
	if err == gocql.ErrNotFound {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	return &notice, nil
}

func (r *noticeRepo) GetByGlobalID(ctx context.Context, id int64) (*models.Notice, error) {
	var notice models.Notice
	err := r.session.Query(
		"SELECT country, news_id, id, content FROM tbl_notice WHERE id = ? LIMIT 1 ALLOW FILTERING",
		id,
	).WithContext(ctx).Scan(&notice.Country, &notice.NewsID, &notice.ID, &notice.Content)
	if err == gocql.ErrNotFound {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	return &notice, nil
}

func (r *noticeRepo) Update(ctx context.Context, previousCountry string, previousNewsID, previousID int64, notice *models.Notice) (bool, error) {
	existing, err := r.GetByID(ctx, previousCountry, previousNewsID, previousID)
	if err != nil {
		return false, err
	}
	if existing == nil {
		return false, nil
	}

	if previousCountry != notice.Country || previousNewsID != notice.NewsID {
		if _, err := r.Delete(ctx, previousCountry, previousNewsID, previousID); err != nil {
			return false, err
		}
	}

	if err := r.session.Query(
		"INSERT INTO tbl_notice (country, news_id, id, content) VALUES (?, ?, ?, ?)",
		notice.Country, notice.NewsID, previousID, notice.Content,
	).WithContext(ctx).Exec(); err != nil {
		return false, err
	}

	notice.ID = previousID
	return true, nil
}

func (r *noticeRepo) Delete(ctx context.Context, country string, newsID, id int64) (bool, error) {
	existing, err := r.GetByID(ctx, country, newsID, id)
	if err != nil {
		return false, err
	}
	if existing == nil {
		return false, nil
	}

	if err := r.session.Query(
		"DELETE FROM tbl_notice WHERE country = ? AND news_id = ? AND id = ?",
		country, newsID, id,
	).WithContext(ctx).Exec(); err != nil {
		return false, err
	}
	return true, nil
}

func (r *noticeRepo) GetByNewsID(ctx context.Context, country string, newsID int64) ([]models.Notice, error) {
	iter := r.session.Query(
		"SELECT country, news_id, id, content FROM tbl_notice WHERE country = ? AND news_id = ?",
		country, newsID,
	).WithContext(ctx).Iter()
	defer iter.Close()

	notices := make([]models.Notice, 0)
	var notice models.Notice
	for iter.Scan(&notice.Country, &notice.NewsID, &notice.ID, &notice.Content) {
		notices = append(notices, notice)
		notice = models.Notice{}
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}
	return notices, nil
}

func (r *noticeRepo) GetByNewsIDAny(ctx context.Context, newsID int64) ([]models.Notice, error) {
	iter := r.session.Query(
		"SELECT country, news_id, id, content FROM tbl_notice WHERE news_id = ? ALLOW FILTERING",
		newsID,
	).WithContext(ctx).Iter()
	defer iter.Close()

	notices := make([]models.Notice, 0)
	var notice models.Notice
	for iter.Scan(&notice.Country, &notice.NewsID, &notice.ID, &notice.Content) {
		notices = append(notices, notice)
		notice = models.Notice{}
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}
	return notices, nil
}

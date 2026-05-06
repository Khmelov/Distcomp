package repository

import (
	"context"
	"errors"
	"sync/atomic"
	"time"

	"news-board/discussion/internal/domain/models"

	"github.com/gocql/gocql"
)

type noticeRepo struct {
	session   *gocql.Session
	idCounter atomic.Int64
}

func NewNoticeRepository(session *gocql.Session) *noticeRepo {
	repo := &noticeRepo{session: session}
	repo.idCounter.Store(time.Now().UnixNano() / 1e6)
	return repo
}

func (r *noticeRepo) Create(ctx context.Context, notice *models.Notice) error {
	if notice.ID == 0 {
		notice.ID = r.idCounter.Add(1)
	}

	return r.session.Query(
		"INSERT INTO tbl_notice (news_id, id, content) VALUES (?, ?, ?)",
		notice.NewsID, notice.ID, notice.Content,
	).WithContext(ctx).Exec()
}

func (r *noticeRepo) GetAll(ctx context.Context, limit, offset int) ([]models.Notice, error) {
	iter := r.session.Query(
		"SELECT news_id, id, content FROM tbl_notice LIMIT ?",
		limit+offset,
	).WithContext(ctx).Iter()
	defer iter.Close()

	notices := make([]models.Notice, 0)
	var notice models.Notice
	index := 0

	for iter.Scan(&notice.NewsID, &notice.ID, &notice.Content) {
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

func (r *noticeRepo) GetByID(ctx context.Context, id int64) (*models.Notice, error) {
	var notice models.Notice
	err := r.session.Query(
		"SELECT news_id, id, content FROM tbl_notice WHERE id = ? LIMIT 1 ALLOW FILTERING",
		id,
	).WithContext(ctx).Scan(&notice.NewsID, &notice.ID, &notice.Content)

	if errors.Is(err, gocql.ErrNotFound) {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	return &notice, nil
}

func (r *noticeRepo) Update(ctx context.Context, previousNewsID, id int64, notice *models.Notice) (bool, error) {
	existing, err := r.GetByID(ctx, id)
	if err != nil {
		return false, err
	}
	if existing == nil {
		return false, nil
	}

	if previousNewsID != notice.NewsID {
		if _, err := r.Delete(ctx, previousNewsID, id); err != nil {
			return false, err
		}
	}

	if err := r.session.Query(
		"INSERT INTO tbl_notice (news_id, id, content) VALUES (?, ?, ?)",
		notice.NewsID, id, notice.Content,
	).WithContext(ctx).Exec(); err != nil {
		return false, err
	}

	notice.ID = id
	return true, nil
}

func (r *noticeRepo) Delete(ctx context.Context, newsID, id int64) (bool, error) {
	existing, err := r.GetByID(ctx, id)
	if err != nil {
		return false, err
	}
	if existing == nil {
		return false, nil
	}

	if err := r.session.Query(
		"DELETE FROM tbl_notice WHERE news_id = ? AND id = ?",
		newsID, id,
	).WithContext(ctx).Exec(); err != nil {
		return false, err
	}
	return true, nil
}

func (r *noticeRepo) GetByNewsID(ctx context.Context, newsID int64) ([]models.Notice, error) {
	iter := r.session.Query(
		"SELECT news_id, id, content FROM tbl_notice WHERE news_id = ?",
		newsID,
	).WithContext(ctx).Iter()
	defer iter.Close()

	notices := make([]models.Notice, 0)
	var notice models.Notice
	for iter.Scan(&notice.NewsID, &notice.ID, &notice.Content) {
		notices = append(notices, notice)
		notice = models.Notice{}
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}
	return notices, nil
}

package notice

import (
	"context"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
)

type newsRepo interface {
	GetNews(ctx context.Context, id int64) (model.News, error)
}

type syncNotice interface {
	GetNotice(ctx context.Context, id int64) (model.Notice, error)
	ListNotices(ctx context.Context) ([]model.Notice, error)
	CreateNotice(ctx context.Context, args model.Notice) (model.Notice, error)
	UpdateNotice(ctx context.Context, args model.Notice) (model.Notice, error)
	DeleteNotice(ctx context.Context, id int64) error
}

type asyncNotice interface {
	CreateNoticeAsync(ctx context.Context, args model.Notice) error
}

type noticeCache interface {
	UpdateNoticeCache(ctx context.Context, notice model.Notice) error
	GetNoticeFromCache(ctx context.Context, id int64) (model.Notice, error)
	DeleteNoticeFromCache(ctx context.Context, id int64) error
}

type noticeService struct {
	syncNotice
	asyncNotice
	news  newsRepo
	cache noticeCache
}

func New(
	syncNotice syncNotice,
	asyncNotice asyncNotice,
	news newsRepo,
	cache noticeCache,
) *noticeService {
	return &noticeService{
		syncNotice:  syncNotice,
		asyncNotice: asyncNotice,
		news:        news,
		cache:       cache,
	}
}

func (n *noticeService) CreateNoticeAsync(
	ctx context.Context,
	args model.Notice,
) error {
	_, err := n.news.GetNews(ctx, args.NewsID)
	if err != nil {
		return err
	}

	err = n.asyncNotice.CreateNoticeAsync(ctx, args)
	if err != nil {
		return err
	}

	return nil
}

func (n *noticeService) CreateNotice(ctx context.Context, args model.Notice) (model.Notice, error) {
	_, err := n.news.GetNews(ctx, args.NewsID)
	if err != nil {
		return model.Notice{}, err
	}

	notice, err := n.syncNotice.CreateNotice(ctx, args)
	if err != nil {
		return model.Notice{}, err
	}

	if err := n.cache.UpdateNoticeCache(ctx, notice); err != nil {
		return model.Notice{}, err
	}

	return notice, nil
}

func (n *noticeService) GetNotice(ctx context.Context, id int64) (model.Notice, error) {
	notice, err := n.cache.GetNoticeFromCache(ctx, id)
	if err == nil {
		return notice, nil
	}

	notice, err = n.syncNotice.GetNotice(ctx, id)
	if err != nil {
		return model.Notice{}, err
	}

	return notice, nil
}

func (n *noticeService) UpdateNotice(ctx context.Context, args model.Notice) (model.Notice, error) {
	notice, err := n.syncNotice.UpdateNotice(ctx, args)
	if err != nil {
		return model.Notice{}, err
	}

	if err := n.cache.UpdateNoticeCache(ctx, notice); err != nil {
		return model.Notice{}, nil
	}

	return notice, nil
}

func (n *noticeService) DeleteNotice(ctx context.Context, id int64) error {
	if err := n.syncNotice.DeleteNotice(ctx, id); err != nil {
		return err
	}

	if err := n.cache.DeleteNoticeFromCache(ctx, id); err != nil {
		return err
	}

	return nil
}

package service

import (
	"context"
	"errors"
	"fmt"
	"github.com/jackc/pgx/v5/pgconn"
	"news-board/publisher/internal/domain"
	"news-board/publisher/internal/domain/models"
	"news-board/publisher/internal/dto"
)

type NewsService struct {
	newsRepo   models.NewsRepository
	markerRepo models.MarkerRepository
}

func NewNewsService(newsRepo models.NewsRepository, markerRepo models.MarkerRepository) *NewsService {
	return &NewsService{
		newsRepo:   newsRepo,
		markerRepo: markerRepo,
	}
}

func (s *NewsService) Create(ctx context.Context, req *dto.NewsRequestTo) (*dto.NewsResponseTo, error) {
	news := &models.News{
		UserID:  req.UserID,
		Title:   req.Title,
		Content: req.Content,
	}
	if err := s.newsRepo.CreateWithMarkers(ctx, news, req.Markers); err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) {
			switch pgErr.Code {
			case "23503":
				return nil, domain.ErrNewsUserNotFound
			case "23505":
				return nil, domain.ErrNewsDuplicate
			}
		}
		return nil, fmt.Errorf("failed to create news: %w", err)
	}

	return &dto.NewsResponseTo{
		ID:       news.ID,
		UserID:   news.UserID,
		Title:    news.Title,
		Content:  news.Content,
		Created:  news.Created,
		Modified: news.Modified,
	}, nil
}

func (s *NewsService) GetAll(ctx context.Context, limit, offset int) ([]dto.NewsResponseTo, error) {
	newsList, err := s.newsRepo.GetAll(ctx, limit, offset, map[string]interface{}{}, "")
	if err != nil {
		return nil, fmt.Errorf("failed to get news: %w", err)
	}
	resp := make([]dto.NewsResponseTo, 0, len(newsList))
	for _, n := range newsList {
		resp = append(resp, dto.NewsResponseTo{
			ID:       n.ID,
			UserID:   n.UserID,
			Title:    n.Title,
			Content:  n.Content,
			Created:  n.Created,
			Modified: n.Modified,
		})
	}
	return resp, nil
}

func (s *NewsService) GetByID(ctx context.Context, id int64) (*dto.NewsResponseTo, error) {
	news, err := s.newsRepo.GetByID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("failed to get news: %w", err)
	}
	if news == nil {
		return nil, domain.ErrNewsNotFound
	}
	return &dto.NewsResponseTo{
		ID:       news.ID,
		UserID:   news.UserID,
		Title:    news.Title,
		Content:  news.Content,
		Created:  news.Created,
		Modified: news.Modified,
	}, nil
}

func (s *NewsService) Update(ctx context.Context, id int64, req *dto.NewsRequestTo) (*dto.NewsResponseTo, error) {
	news := &models.News{
		ID:      id,
		UserID:  req.UserID,
		Title:   req.Title,
		Content: req.Content,
	}
	updated, err := s.newsRepo.Update(ctx, news)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) {
			switch pgErr.Code {
			case "23503":
				return nil, domain.ErrNewsUserNotFound
			case "23505":
				return nil, domain.ErrNewsDuplicate
			}
		}
		return nil, fmt.Errorf("failed to update news: %w", err)
	}
	if !updated {
		return nil, domain.ErrNewsNotFound
	}
	return &dto.NewsResponseTo{
		ID:       news.ID,
		UserID:   news.UserID,
		Title:    news.Title,
		Content:  news.Content,
		Created:  news.Created,
		Modified: news.Modified,
	}, nil
}

func (s *NewsService) Delete(ctx context.Context, id int64) error {
	deleted, err := s.newsRepo.DeleteWithCleanup(ctx, id)
	if err != nil {
		return fmt.Errorf("failed to delete news: %w", err)
	}
	if !deleted {
		return domain.ErrNewsNotFound
	}
	return nil
}

func (s *NewsService) AddMarker(ctx context.Context, newsID, markerID int64) error {
	news, err := s.newsRepo.GetByID(ctx, newsID)
	if err != nil {
		return fmt.Errorf("failed to verify news: %w", err)
	}
	if news == nil {
		return domain.ErrNewsNotFound
	}
	marker, err := s.markerRepo.GetByID(ctx, markerID)
	if err != nil {
		return fmt.Errorf("failed to verify marker: %w", err)
	}
	if marker == nil {
		return domain.ErrMarkerNotFound
	}
	if err := s.newsRepo.AddMarker(ctx, newsID, markerID); err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return domain.ErrNewsMarkerDuplicate
		}
		return fmt.Errorf("failed to add marker to news: %w", err)
	}
	return nil
}

func (s *NewsService) RemoveMarker(ctx context.Context, newsID, markerID int64) error {
	removed, err := s.newsRepo.RemoveMarker(ctx, newsID, markerID)
	if err != nil {
		return fmt.Errorf("failed to remove marker from news: %w", err)
	}
	if !removed {
		return domain.ErrMarkerNotFound
	}
	return nil
}

func (s *NewsService) GetMarkersByNewsID(ctx context.Context, newsID int64) ([]dto.MarkerResponseTo, error) {
	markers, err := s.newsRepo.GetMarkers(ctx, newsID)
	if err != nil {
		return nil, fmt.Errorf("failed to get markers for news: %w", err)
	}
	resp := make([]dto.MarkerResponseTo, 0, len(markers))
	for _, m := range markers {
		resp = append(resp, dto.MarkerResponseTo{
			ID:   m.ID,
			Name: m.Name,
		})
	}
	return resp, nil
}

func (s *NewsService) Search(ctx context.Context, filters map[string]interface{}, limit, offset int) ([]dto.NewsResponseTo, error) {
	newsList, err := s.newsRepo.Search(ctx, filters, limit, offset)
	if err != nil {
		return nil, fmt.Errorf("failed to search news: %w", err)
	}
	resp := make([]dto.NewsResponseTo, 0, len(newsList))
	for _, n := range newsList {
		resp = append(resp, dto.NewsResponseTo{
			ID:       n.ID,
			UserID:   n.UserID,
			Title:    n.Title,
			Content:  n.Content,
			Created:  n.Created,
			Modified: n.Modified,
		})
	}
	return resp, nil
}

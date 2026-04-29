package service

import (
	"context"
	"news-board/discussion/internal/domain"
	"news-board/discussion/internal/domain/models"
	"news-board/discussion/internal/dto"
	"strings"
)

const defaultNoticeCountry = "BY"

type NoticeService struct {
	repo models.NoticeRepository
}

func NewNoticeService(repo models.NoticeRepository) *NoticeService {
	return &NoticeService{repo: repo}
}

func (s *NoticeService) Create(ctx context.Context, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	notice := &models.Notice{
		Country: normalizeCountry(req.Country),
		NewsID:  req.NewsID,
		Content: req.Content,
	}
	if err := s.repo.Create(ctx, notice); err != nil {
		return nil, err
	}
	return toResponse(notice), nil
}

func (s *NoticeService) GetAll(ctx context.Context, country string, limit, offset int) ([]dto.NoticeResponseTo, error) {
	var (
		notices []models.Notice
		err     error
	)
	if country == "" {
		notices, err = s.repo.GetAllAny(ctx, limit, offset)
	} else {
		notices, err = s.repo.GetAll(ctx, country, limit, offset)
	}
	if err != nil {
		return nil, err
	}
	return toResponses(notices), nil
}

func (s *NoticeService) GetByID(ctx context.Context, country string, newsID, id int64) (*dto.NoticeResponseTo, error) {
	var (
		notice *models.Notice
		err    error
	)
	if country == "" || newsID == 0 {
		notice, err = s.repo.GetByGlobalID(ctx, id)
	} else {
		notice, err = s.repo.GetByID(ctx, country, newsID, id)
	}
	if err != nil {
		return nil, err
	}
	if notice == nil {
		return nil, domain.ErrNoticeNotFound
	}
	return toResponse(notice), nil
}

func (s *NoticeService) Update(ctx context.Context, country string, newsID, id int64, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	if country == "" || newsID == 0 {
		existing, err := s.repo.GetByGlobalID(ctx, id)
		if err != nil {
			return nil, err
		}
		if existing == nil {
			return nil, domain.ErrNoticeNotFound
		}
		country = existing.Country
		newsID = existing.NewsID
	}

	notice := &models.Notice{
		Country: normalizeCountry(req.Country),
		NewsID:  req.NewsID,
		ID:      id,
		Content: req.Content,
	}
	updated, err := s.repo.Update(ctx, country, newsID, id, notice)
	if err != nil {
		return nil, err
	}
	if !updated {
		return nil, domain.ErrNoticeNotFound
	}
	return toResponse(notice), nil
}

func (s *NoticeService) Delete(ctx context.Context, country string, newsID, id int64) error {
	if country == "" || newsID == 0 {
		existing, err := s.repo.GetByGlobalID(ctx, id)
		if err != nil {
			return err
		}
		if existing == nil {
			return domain.ErrNoticeNotFound
		}
		country = existing.Country
		newsID = existing.NewsID
	}

	deleted, err := s.repo.Delete(ctx, country, newsID, id)
	if err != nil {
		return err
	}
	if !deleted {
		return domain.ErrNoticeNotFound
	}
	return nil
}

func (s *NoticeService) GetByNewsID(ctx context.Context, country string, newsID int64) ([]dto.NoticeResponseTo, error) {
	var (
		notices []models.Notice
		err     error
	)
	if country == "" {
		notices, err = s.repo.GetByNewsIDAny(ctx, newsID)
	} else {
		notices, err = s.repo.GetByNewsID(ctx, country, newsID)
	}
	if err != nil {
		return nil, err
	}
	return toResponses(notices), nil
}

func toResponse(notice *models.Notice) *dto.NoticeResponseTo {
	return &dto.NoticeResponseTo{
		Country: notice.Country,
		ID:      notice.ID,
		NewsID:  notice.NewsID,
		Content: notice.Content,
	}
}

func toResponses(notices []models.Notice) []dto.NoticeResponseTo {
	resp := make([]dto.NoticeResponseTo, 0, len(notices))
	for _, notice := range notices {
		resp = append(resp, dto.NoticeResponseTo{
			Country: notice.Country,
			ID:      notice.ID,
			NewsID:  notice.NewsID,
			Content: notice.Content,
		})
	}
	return resp
}

func normalizeCountry(country string) string {
	country = strings.TrimSpace(country)
	if country == "" {
		return defaultNoticeCountry
	}
	return country
}

package service

import (
	"context"
	"fmt"
	"log"
	"news-board/discussion/internal/domain"
	"news-board/discussion/internal/domain/models"
	"news-board/discussion/internal/dto"
)

type NoticeService struct {
	repo models.NoticeRepository
}

func NewNoticeService(repo models.NoticeRepository) *NoticeService {
	return &NoticeService{repo: repo}
}

func (s *NoticeService) HandleAsyncRequest(ctx context.Context, event *NoticeEvent, producer *KafkaProducer) {
	var err error

	switch event.Type {
	case EventTypeNoticeCreated:
		notice := &models.Notice{
			ID:      event.Data.ID,
			NewsID:  event.Data.NewsID,
			Content: event.Data.Content,
		}
		err = s.repo.Create(ctx, notice)
	case EventTypeNoticeUpdated:
		notice := &models.Notice{
			ID:      event.Data.ID,
			NewsID:  event.Data.NewsID,
			Content: event.Data.Content,
		}
		_, err = s.repo.Update(ctx, event.Data.NewsID, event.Data.ID, notice)
	case EventTypeNoticeDeleted:
		_, err = s.repo.Delete(ctx, event.Data.NewsID, event.Data.ID)
	}

	if err != nil {
		event.Error = err.Error()
	}

	log.Printf("[DISCUSSION] Sending reply to %s (corrID: %s)", OutTopic, event.CorrelationID)
	_ = producer.PublishNoticeEvent(ctx, OutTopic, event)
}

func (s *NoticeService) ProcessEvent(ctx context.Context, event *NoticeEvent) error {
	switch event.Type {
	case EventTypeNoticeCreated:
		return s.handleNoticeCreated(ctx, event)
	case EventTypeNoticeUpdated:
		return s.handleNoticeUpdated(ctx, event)
	case EventTypeNoticeDeleted:
		return s.handleNoticeDeleted(ctx, event)
	default:
		return fmt.Errorf("unknown event type: %s", event.Type)
	}
}

func (s *NoticeService) handleNoticeCreated(ctx context.Context, event *NoticeEvent) error {
	notice := &models.Notice{
		ID:      event.Data.ID,
		NewsID:  event.Data.NewsID,
		Content: event.Data.Content,
	}
	return s.repo.Create(ctx, notice)
}

func (s *NoticeService) handleNoticeUpdated(ctx context.Context, event *NoticeEvent) error {
	notice := &models.Notice{
		ID:      event.Data.ID,
		NewsID:  event.Data.NewsID,
		Content: event.Data.Content,
	}
	_, err := s.repo.Update(ctx, event.Data.NewsID, event.Data.ID, notice)
	return err
}

func (s *NoticeService) handleNoticeDeleted(ctx context.Context, event *NoticeEvent) error {
	_, err := s.repo.Delete(ctx, event.Data.NewsID, event.Data.ID)
	return err
}

func (s *NoticeService) Create(ctx context.Context, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	notice := &models.Notice{
		NewsID:  req.NewsID,
		Content: req.Content,
	}
	if err := s.repo.Create(ctx, notice); err != nil {
		return nil, err
	}
	return toResponse(notice), nil
}

func (s *NoticeService) GetAll(ctx context.Context, limit, offset int) ([]dto.NoticeResponseTo, error) {
	notices, err := s.repo.GetAll(ctx, limit, offset)
	if err != nil {
		return nil, err
	}
	return toResponses(notices), nil
}

func (s *NoticeService) GetByID(ctx context.Context, id int64) (*dto.NoticeResponseTo, error) {
	notice, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if notice == nil {
		return nil, domain.ErrNoticeNotFound
	}
	return toResponse(notice), nil
}

func (s *NoticeService) Update(ctx context.Context, id int64, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	existing, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if existing == nil {
		return nil, domain.ErrNoticeNotFound
	}

	notice := &models.Notice{
		NewsID:  req.NewsID,
		ID:      id,
		Content: req.Content,
	}

	updated, err := s.repo.Update(ctx, req.NewsID, id, notice)
	if err != nil {
		return nil, err
	}
	if !updated {
		return nil, domain.ErrNoticeNotFound
	}
	return toResponse(notice), nil
}

func (s *NoticeService) Delete(ctx context.Context, id int64) error {
	existing, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return err
	}
	if existing == nil {
		return domain.ErrNoticeNotFound
	}

	deleted, err := s.repo.Delete(ctx, existing.NewsID, id)
	if err != nil {
		return err
	}
	if !deleted {
		return domain.ErrNoticeNotFound
	}
	return nil
}

func (s *NoticeService) GetByNewsID(ctx context.Context, newsID int64) ([]dto.NoticeResponseTo, error) {
	notices, err := s.repo.GetByNewsID(ctx, newsID)
	if err != nil {
		return nil, err
	}
	return toResponses(notices), nil
}

func toResponse(notice *models.Notice) *dto.NoticeResponseTo {
	return &dto.NoticeResponseTo{
		ID:      notice.ID,
		NewsID:  notice.NewsID,
		Content: notice.Content,
	}
}

func toResponses(notices []models.Notice) []dto.NoticeResponseTo {
	resp := make([]dto.NoticeResponseTo, 0, len(notices))
	for _, notice := range notices {
		resp = append(resp, *toResponse(&notice))
	}
	return resp
}

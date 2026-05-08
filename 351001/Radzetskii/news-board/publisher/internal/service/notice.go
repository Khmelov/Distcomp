package service

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"strings"
	"sync"
	"time"

	"news-board/publisher/internal/domain"
	"news-board/publisher/internal/domain/models"
	"news-board/publisher/internal/dto"

	"github.com/google/uuid"
	"github.com/redis/go-redis/v9"
)

type NoticeService struct {
	kafkaProducer *KafkaProducer
	client        *http.Client
	newsRepo      models.NewsRepository
	redis         *redis.Client
	baseURL       string

	pendingRequests map[string]chan *NoticeEvent
	mu              sync.Mutex
}

func NewNoticeService(kafkaProducer *KafkaProducer, newsRepo models.NewsRepository, baseURL string, rdb *redis.Client) *NoticeService {
	return &NoticeService{
		kafkaProducer:   kafkaProducer,
		client:          &http.Client{Timeout: 10 * time.Second},
		newsRepo:        newsRepo,
		redis:           rdb,
		baseURL:         strings.TrimRight(baseURL, "/"),
		pendingRequests: make(map[string]chan *NoticeEvent),
	}
}

func (s *NoticeService) sendAndWait(ctx context.Context, event *NoticeEvent) error {
	ch := make(chan *NoticeEvent, 1)

	s.mu.Lock()
	s.pendingRequests[event.CorrelationID] = ch
	s.mu.Unlock()

	defer func() {
		s.mu.Lock()
		delete(s.pendingRequests, event.CorrelationID)
		s.mu.Unlock()
	}()

	if err := s.kafkaProducer.PublishNoticeEvent(ctx, InTopic, event); err != nil {
		return err
	}

	select {
	case reply := <-ch:
		if reply.Error != "" {
			return errors.New(reply.Error)
		}
		return nil
	case <-time.After(3 * time.Second): // 3 секунды ожидания с запасом
		return errors.New("timeout: no response from discussion service")
	case <-ctx.Done():
		return ctx.Err()
	}
}

func (s *NoticeService) MatchResponse(event *NoticeEvent) {
	s.mu.Lock()
	defer s.mu.Unlock()
	if ch, exists := s.pendingRequests[event.CorrelationID]; exists {
		ch <- event
	}
}

func (s *NoticeService) Create(ctx context.Context, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	if err := s.ensureNewsExists(ctx, req.NewsID); err != nil {
		return nil, err
	}

	generatedID := time.Now().UnixNano() / 1e6
	corrID := uuid.New().String()

	event := NewNoticeCreatedEvent(corrID, generatedID, req.NewsID, req.Content)
	if err := s.sendAndWait(ctx, event); err != nil {
		return nil, err
	}

	resp := &dto.NoticeResponseTo{
		ID:      generatedID,
		NewsID:  req.NewsID,
		Content: req.Content,
	}

	s.cacheNotice(ctx, resp)
	return resp, nil
}

func (s *NoticeService) Update(ctx context.Context, id int64, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	existing, err := s.GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	newsID := existing.NewsID
	if req.NewsID != 0 {
		newsID = req.NewsID
	}

	if err := s.ensureNewsExists(ctx, newsID); err != nil {
		return nil, err
	}

	corrID := uuid.New().String()
	event := NewNoticeUpdatedEvent(corrID, id, newsID, req.Content)

	if err := s.sendAndWait(ctx, event); err != nil {
		return nil, err
	}

	resp := &dto.NoticeResponseTo{
		ID:      id,
		NewsID:  newsID,
		Content: req.Content,
	}

	s.cacheNotice(ctx, resp)
	return resp, nil
}

func (s *NoticeService) Delete(ctx context.Context, id int64) error {
	existing, err := s.GetByID(ctx, id)
	if err != nil {
		return err // Вернет 404, если не найдено
	}

	corrID := uuid.New().String()
	event := NewNoticeDeletedEvent(corrID, id, existing.NewsID)

	if err := s.sendAndWait(ctx, event); err != nil {
		return err
	}

	s.redis.Del(ctx, fmt.Sprintf("notice:%d", id))
	return nil
}

func (s *NoticeService) GetByID(ctx context.Context, id int64) (*dto.NoticeResponseTo, error) {
	cacheKey := fmt.Sprintf("notice:%d", id)
	val, err := s.redis.Get(ctx, cacheKey).Result()
	if err == nil {
		var cached dto.NoticeResponseTo
		if err := json.Unmarshal([]byte(val), &cached); err == nil {
			return &cached, nil
		}
	}

	var resp dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/%d", id)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}

	s.cacheNotice(ctx, &resp)
	return &resp, nil
}

func (s *NoticeService) GetAll(ctx context.Context, limit, offset int) ([]dto.NoticeResponseTo, error) {
	var resp []dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices?limit=%d&offset=%d", limit, offset)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *NoticeService) GetByNewsID(ctx context.Context, newsID int64) ([]dto.NoticeResponseTo, error) {
	var resp []dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/by-news/%d", newsID)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *NoticeService) cacheNotice(ctx context.Context, notice *dto.NoticeResponseTo) {
	data, _ := json.Marshal(notice)
	cacheKey := fmt.Sprintf("notice:%d", notice.ID)
	s.redis.Set(ctx, cacheKey, data, 10*time.Minute)
}

func (s *NoticeService) ensureNewsExists(ctx context.Context, newsID int64) error {
	news, err := s.newsRepo.GetByID(ctx, newsID)
	if err != nil || news == nil {
		return domain.ErrNewsNotFound
	}
	return nil
}

func (s *NoticeService) doJSON(ctx context.Context, method, path string, payload any, out any) error {
	var body io.Reader
	if payload != nil {
		raw, _ := json.Marshal(payload)
		body = bytes.NewReader(raw)
	}
	req, err := http.NewRequestWithContext(ctx, method, s.baseURL+path, body)
	if err != nil {
		return err
	}
	if payload != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	resp, err := s.client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	if resp.StatusCode >= http.StatusBadRequest {
		return mapRemoteError(resp)
	}
	if out != nil && resp.StatusCode != http.StatusNoContent {
		return json.NewDecoder(resp.Body).Decode(out)
	}
	return nil
}

func mapRemoteError(resp *http.Response) error {
	var apiErr struct {
		ErrorMessage string `json:"errorMessage"`
		ErrorCode    string `json:"errorCode"`
	}
	json.NewDecoder(resp.Body).Decode(&apiErr)
	if resp.StatusCode == http.StatusNotFound {
		return domain.ErrNoticeNotFound
	}
	if apiErr.ErrorMessage != "" {
		return errors.New(apiErr.ErrorMessage)
	}
	return fmt.Errorf("remote service error: %d", resp.StatusCode)
}

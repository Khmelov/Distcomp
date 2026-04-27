package service

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strconv"
	"strings"
	"time"

	"news-board/publisher/internal/domain"
	"news-board/publisher/internal/domain/models"
	"news-board/publisher/internal/dto"
)

const defaultNoticeCountry = "BY"

type NoticeService struct {
	baseURL  string
	client   *http.Client
	newsRepo models.NewsRepository
}

func NewNoticeService(baseURL string, newsRepo models.NewsRepository) *NoticeService {
	return &NoticeService{
		baseURL: strings.TrimRight(baseURL, "/"),
		client: &http.Client{
			Timeout: 10 * time.Second,
		},
		newsRepo: newsRepo,
	}
}

func (s *NoticeService) Create(ctx context.Context, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	if err := s.ensureNewsExists(ctx, req.NewsID); err != nil {
		return nil, err
	}

	req = normalizedNoticeRequest(req)
	var resp dto.NoticeResponseTo
	if err := s.doJSON(ctx, http.MethodPost, "/api/v1.0/notices", req, &resp); err != nil {
		return nil, err
	}
	return &resp, nil
}

func (s *NoticeService) GetAll(ctx context.Context, country string, limit, offset int) ([]dto.NoticeResponseTo, error) {
	query := url.Values{}
	query.Set("limit", strconv.Itoa(limit))
	query.Set("offset", strconv.Itoa(offset))
	if country != "" {
		query.Set("country", country)
	}

	var resp []dto.NoticeResponseTo
	if err := s.doJSON(ctx, http.MethodGet, "/api/v1.0/notices?"+query.Encode(), nil, &resp); err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *NoticeService) GetByID(ctx context.Context, country string, newsID, id int64) (*dto.NoticeResponseTo, error) {
	var resp dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/by-key/%s/%d/%d", url.PathEscape(country), newsID, id)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}
	return &resp, nil
}

func (s *NoticeService) GetByLegacyID(ctx context.Context, id int64) (*dto.NoticeResponseTo, error) {
	var resp dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/%d", id)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}
	return &resp, nil
}

func (s *NoticeService) Update(ctx context.Context, country string, newsID, id int64, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	if err := s.ensureNewsExists(ctx, req.NewsID); err != nil {
		return nil, err
	}

	req = normalizedNoticeRequest(req)
	var resp dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/by-key/%s/%d/%d", url.PathEscape(country), newsID, id)
	if err := s.doJSON(ctx, http.MethodPut, path, req, &resp); err != nil {
		return nil, err
	}
	return &resp, nil
}

func (s *NoticeService) UpdateByLegacyID(ctx context.Context, id int64, req *dto.NoticeRequestTo) (*dto.NoticeResponseTo, error) {
	if err := s.ensureNewsExists(ctx, req.NewsID); err != nil {
		return nil, err
	}

	req = normalizedNoticeRequest(req)
	var resp dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/%d", id)
	if err := s.doJSON(ctx, http.MethodPut, path, req, &resp); err != nil {
		return nil, err
	}
	return &resp, nil
}

func (s *NoticeService) Delete(ctx context.Context, country string, newsID, id int64) error {
	path := fmt.Sprintf("/api/v1.0/notices/by-key/%s/%d/%d", url.PathEscape(country), newsID, id)
	return s.doJSON(ctx, http.MethodDelete, path, nil, nil)
}

func (s *NoticeService) DeleteByLegacyID(ctx context.Context, id int64) error {
	path := fmt.Sprintf("/api/v1.0/notices/%d", id)
	return s.doJSON(ctx, http.MethodDelete, path, nil, nil)
}

func (s *NoticeService) GetByNewsID(ctx context.Context, country string, newsID int64) ([]dto.NoticeResponseTo, error) {
	var resp []dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/by-country/%s/news/%d", url.PathEscape(country), newsID)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *NoticeService) GetByLegacyNewsID(ctx context.Context, newsID int64) ([]dto.NoticeResponseTo, error) {
	var resp []dto.NoticeResponseTo
	path := fmt.Sprintf("/api/v1.0/notices/by-news/%d", newsID)
	if err := s.doJSON(ctx, http.MethodGet, path, nil, &resp); err != nil {
		return nil, err
	}
	return resp, nil
}

func (s *NoticeService) ensureNewsExists(ctx context.Context, newsID int64) error {
	news, err := s.newsRepo.GetByID(ctx, newsID)
	if err != nil {
		return fmt.Errorf("failed to verify news: %w", err)
	}
	if news == nil {
		return domain.ErrNewsNotFound
	}
	return nil
}

func normalizedNoticeRequest(req *dto.NoticeRequestTo) *dto.NoticeRequestTo {
	if req == nil {
		return nil
	}

	normalized := *req
	normalized.Country = normalizeCountry(normalized.Country)
	return &normalized
}

func normalizeCountry(country string) string {
	country = strings.TrimSpace(country)
	if country == "" {
		return defaultNoticeCountry
	}
	return country
}

func (s *NoticeService) doJSON(ctx context.Context, method, path string, payload any, out any) error {
	var body io.Reader
	if payload != nil {
		raw, err := json.Marshal(payload)
		if err != nil {
			return fmt.Errorf("failed to marshal discussion request: %w", err)
		}
		body = bytes.NewReader(raw)
	}

	req, err := http.NewRequestWithContext(ctx, method, s.baseURL+path, body)
	if err != nil {
		return fmt.Errorf("failed to build discussion request: %w", err)
	}
	if payload != nil {
		req.Header.Set("Content-Type", "application/json")
	}

	resp, err := s.client.Do(req)
	if err != nil {
		return fmt.Errorf("failed to call discussion service: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode >= http.StatusBadRequest {
		return mapRemoteError(resp)
	}

	if out == nil || resp.StatusCode == http.StatusNoContent {
		return nil
	}

	if err := json.NewDecoder(resp.Body).Decode(out); err != nil {
		return fmt.Errorf("failed to decode discussion response: %w", err)
	}
	return nil
}

func mapRemoteError(resp *http.Response) error {
	var apiErr struct {
		ErrorMessage string `json:"errorMessage"`
		ErrorCode    string `json:"errorCode"`
	}

	if err := json.NewDecoder(resp.Body).Decode(&apiErr); err != nil {
		return fmt.Errorf("discussion service returned status %d", resp.StatusCode)
	}

	switch {
	case resp.StatusCode == http.StatusNotFound && strings.HasPrefix(apiErr.ErrorCode, "404"):
		return domain.ErrNoticeNotFound
	case resp.StatusCode == http.StatusBadRequest:
		return errors.New(apiErr.ErrorMessage)
	default:
		return fmt.Errorf("discussion service error: %s", apiErr.ErrorMessage)
	}
}

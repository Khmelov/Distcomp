package service

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"

	"distcomp/internal/dto"
	"distcomp/internal/repository"
)

type commentProxy struct {
	baseURL string
	client  *http.Client
}

func NewCommentProxy(baseURL string) Comment {
	return &commentProxy{
		baseURL: baseURL,
		client:  &http.Client{Timeout: 5 * time.Second},
	}
}

func (p *commentProxy) doRequest(ctx context.Context, method, url string, body interface{}, dest interface{}) error {
	var reqBody io.Reader
	if body != nil {
		jsonData, err := json.Marshal(body)
		if err != nil {
			return err
		}
		reqBody = bytes.NewBuffer(jsonData)
	}

	req, err := http.NewRequestWithContext(ctx, method, url, reqBody)
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := p.client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode >= 400 {
		if resp.StatusCode == http.StatusNotFound {
			return errors.New("entity not found")
		}
		errBody, _ := io.ReadAll(resp.Body)
		return fmt.Errorf("remote error: %s", string(errBody))
	}

	if dest != nil && resp.StatusCode != http.StatusNoContent {
		if err := json.NewDecoder(resp.Body).Decode(dest); err != nil {
			return err
		}
	}
	return nil
}

func (p *commentProxy) Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	var res dto.CommentResponseTo
	err := p.doRequest(ctx, http.MethodPost, p.baseURL, req, &res)
	return res, err
}

func (p *commentProxy) GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error) {
	var res dto.CommentResponseTo
	url := fmt.Sprintf("%s/%d", p.baseURL, id)
	err := p.doRequest(ctx, http.MethodGet, url, nil, &res)
	return res, err
}

func (p *commentProxy) GetAll(ctx context.Context, params repository.ListParams) ([]dto.CommentResponseTo, error) {
	var res []dto.CommentResponseTo
	// Для простоты лабы игнорируем пагинацию при проксировании, 
	// либо можно дописать формирование query-параметров.
	err := p.doRequest(ctx, http.MethodGet, p.baseURL, nil, &res)
	return res, err
}

func (p *commentProxy) Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	var res dto.CommentResponseTo
	url := fmt.Sprintf("%s/%d", p.baseURL, id)
	err := p.doRequest(ctx, http.MethodPut, url, req, &res)
	return res, err
}

func (p *commentProxy) Delete(ctx context.Context, id int64) error {
	url := fmt.Sprintf("%s/%d", p.baseURL, id)
	return p.doRequest(ctx, http.MethodDelete, url, nil, nil)
}
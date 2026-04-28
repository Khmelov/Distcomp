package gateway

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
	"publisher/internal/transport/dto/response"
)

type DiscussionClient struct {
	baseURL string
	client  *http.Client
}

func NewDiscussionClient(baseURL string) *DiscussionClient {
	return &DiscussionClient{
		baseURL: baseURL,
		client:  &http.Client{},
	}
}

func (c *DiscussionClient) FindByID(ctx context.Context, id int64) (*response.ReactionResponseTo, error) {
	resp, err := c.do(ctx, http.MethodGet, fmt.Sprintf("/api/v1.0/reactions/%d", id), nil)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if err := checkStatus(resp); err != nil {
		return nil, err
	}
	var result response.ReactionResponseTo
	return &result, json.NewDecoder(resp.Body).Decode(&result)
}

func (c *DiscussionClient) FindAll(ctx context.Context) ([]*response.ReactionResponseTo, error) {
	resp, err := c.do(ctx, http.MethodGet, "/api/v1.0/reactions", nil)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if err := checkStatus(resp); err != nil {
		return nil, err
	}
	var result []*response.ReactionResponseTo
	return result, json.NewDecoder(resp.Body).Decode(&result)
}

func (c *DiscussionClient) FindByIssueID(ctx context.Context, issueID int64) ([]*response.ReactionResponseTo, error) {
	resp, err := c.do(ctx, http.MethodGet, fmt.Sprintf("/api/v1.0/reactions?issueId=%d", issueID), nil)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if err := checkStatus(resp); err != nil {
		return nil, err
	}
	var result []*response.ReactionResponseTo
	return result, json.NewDecoder(resp.Body).Decode(&result)
}

func (c *DiscussionClient) Create(ctx context.Context, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error) {
	body, _ := json.Marshal(req)
	resp, err := c.do(ctx, http.MethodPost, "/api/v1.0/reactions", bytes.NewReader(body))
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if err := checkStatus(resp); err != nil {
		return nil, err
	}
	var result response.ReactionResponseTo
	return &result, json.NewDecoder(resp.Body).Decode(&result)
}

func (c *DiscussionClient) Update(ctx context.Context, id int64, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error) {
	body, _ := json.Marshal(req)
	resp, err := c.do(ctx, http.MethodPut, fmt.Sprintf("/api/v1.0/reactions/%d", id), bytes.NewReader(body))
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()
	if err := checkStatus(resp); err != nil {
		return nil, err
	}
	var result response.ReactionResponseTo
	return &result, json.NewDecoder(resp.Body).Decode(&result)
}

func (c *DiscussionClient) Delete(ctx context.Context, id int64) error {
	resp, err := c.do(ctx, http.MethodDelete, fmt.Sprintf("/api/v1.0/reactions/%d", id), nil)
	if err != nil {
		return err
	}
	defer resp.Body.Close()
	return checkStatus(resp)
}

func (c *DiscussionClient) do(ctx context.Context, method, path string, body io.Reader) (*http.Response, error) {
	req, err := http.NewRequestWithContext(ctx, method, c.baseURL+path, body)
	if err != nil {
		return nil, apperrors.ErrInternal
	}
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	return c.client.Do(req)
}

func checkStatus(resp *http.Response) error {
	if resp.StatusCode >= 200 && resp.StatusCode < 300 {
		return nil
	}
	var appErr apperrors.AppError
	if err := json.NewDecoder(resp.Body).Decode(&appErr); err == nil && appErr.Code != 0 {
		appErr.HTTPStatus = resp.StatusCode
		return &appErr
	}
	switch resp.StatusCode {
	case http.StatusNotFound:
		return apperrors.ErrNotFound
	case http.StatusForbidden:
		return apperrors.ErrDuplicate
	case http.StatusBadRequest:
		return apperrors.ErrBadRequest
	}
	return apperrors.ErrInternal
}

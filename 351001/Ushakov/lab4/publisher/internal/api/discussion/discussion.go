package discussion

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/model"
)

const (
	baseURL = "http://localhost:24130/api/v1.0"
)

type Client struct {
	httpClient *http.Client
}

type ClientInterface interface {
	GetPosts(ctx context.Context) ([]model.Post, error)
	GetPost(ctx context.Context, id int64) (*model.Post, error)
	CreatePost(ctx context.Context, issueID int64, content string) (*model.Post, error)
	UpdatePost(ctx context.Context, id, issueID int64, content string) (*model.Post, error)
	DeletePost(ctx context.Context, id int64) error
}

func NewClient() ClientInterface {
	return &Client{
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (c *Client) GetPosts(ctx context.Context) ([]model.Post, error) {
	req, err := http.NewRequestWithContext(ctx, "GET", baseURL+"/comments", nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	var messages []model.Post
	if err := json.NewDecoder(resp.Body).Decode(&messages); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return messages, nil
}

func (c *Client) GetPost(ctx context.Context, id int64) (*model.Post, error) {
	req, err := http.NewRequestWithContext(ctx, "GET", fmt.Sprintf("%s/comments/%d", baseURL, id), nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	var message model.Post
	if err := json.NewDecoder(resp.Body).Decode(&message); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &message, nil
}

func (c *Client) CreatePost(ctx context.Context, issueID int64, content string) (*model.Post, error) {
	message := map[string]interface{}{
		"tweetId": issueID,
		"content": content,
	}

	body, err := json.Marshal(message)
	if err != nil {
		return nil, fmt.Errorf("failed to marshal request body: %w", err)
	}

	req, err := http.NewRequestWithContext(ctx, "POST", baseURL+"/comments", bytes.NewBuffer(body))
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated {
		body, _ := io.ReadAll(resp.Body)
		return nil, fmt.Errorf("unexpected status code: %d, body: %s", resp.StatusCode, string(body))
	}

	var createdPost model.Post
	if err := json.NewDecoder(resp.Body).Decode(&createdPost); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &createdPost, nil
}

func (c *Client) UpdatePost(ctx context.Context, id, issueID int64, content string) (*model.Post, error) {
	message := map[string]interface{}{
		"id":      id,
		"tweetId": issueID,
		"content": content,
	}

	body, err := json.Marshal(message)
	if err != nil {
		return nil, fmt.Errorf("failed to marshal request body: %w", err)
	}

	req, err := http.NewRequestWithContext(ctx, "PUT", baseURL+"/comments", bytes.NewBuffer(body))
	if err != nil {
		return nil, fmt.Errorf("failed to create request: %w", err)
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	var updatedPost model.Post
	if err := json.NewDecoder(resp.Body).Decode(&updatedPost); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &updatedPost, nil
}

func (c *Client) DeletePost(ctx context.Context, id int64) error {
	req, err := http.NewRequestWithContext(ctx, "DELETE", fmt.Sprintf("%s/comments/%d", baseURL, id), nil)
	if err != nil {
		return fmt.Errorf("failed to create request: %w", err)
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return fmt.Errorf("request failed: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusNoContent {
		return fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	return nil
}

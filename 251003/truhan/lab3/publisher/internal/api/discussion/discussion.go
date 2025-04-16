package discussion

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
)

const (
	baseURL = "http://localhost:24130/api/v1.0"
)

type Client struct {
	httpClient *http.Client
}

type ClientInterface interface {
	GetMessages(ctx context.Context) ([]model.Message, error)
	GetMessage(ctx context.Context, id int64) (*model.Message, error)
	CreateMessage(ctx context.Context, issueID int64, content string) (*model.Message, error)
	UpdateMessage(ctx context.Context, id, issueID int64, content string) (*model.Message, error)
	DeleteMessage(ctx context.Context, id int64) error
}

func NewClient() ClientInterface {
	return &Client{
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (c *Client) GetMessages(ctx context.Context) ([]model.Message, error) {
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

	var messages []model.Message
	if err := json.NewDecoder(resp.Body).Decode(&messages); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return messages, nil
}

func (c *Client) GetMessage(ctx context.Context, id int64) (*model.Message, error) {
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

	var message model.Message
	if err := json.NewDecoder(resp.Body).Decode(&message); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &message, nil
}

func (c *Client) CreateMessage(ctx context.Context, issueID int64, content string) (*model.Message, error) {
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

	var createdMessage model.Message
	if err := json.NewDecoder(resp.Body).Decode(&createdMessage); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &createdMessage, nil
}

func (c *Client) UpdateMessage(ctx context.Context, id, issueID int64, content string) (*model.Message, error) {
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

	var updatedMessage model.Message
	if err := json.NewDecoder(resp.Body).Decode(&updatedMessage); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &updatedMessage, nil
}

func (c *Client) DeleteMessage(ctx context.Context, id int64) error {
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

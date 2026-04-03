package discussion

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/model"
)

const (
	baseURL = "http://localhost:24130/api/v1.0"
)

type Client struct {
	httpClient *http.Client
}

type ClientInterface interface {
	Getgithub.com
/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Getgithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) (*model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Creategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, issueID int64, content string) (*model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Updategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id, issueID int64, content string) (*model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Deletegithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) error
}

func NewClient() ClientInterface {
	return &Client{
		httpClient: &http.Client{
			Timeout: 10 * time.Second,
		},
	}
}

func (c *Client) Getgithub.com/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
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

	var posts []model.github.com / Khmelov / Distcomp / 351001 / Ushakov
	if err := json.NewDecoder(resp.Body).Decode(&posts); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return posts, nil
}

func (c *Client) Getgithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) (*model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
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

	var post model.github.com / Khmelov / Distcomp / 351001 / Ushakov
	if err := json.NewDecoder(resp.Body).Decode(&post); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &post, nil
}

func (c *Client) Creategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, issueID int64, content string) (*model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	post := map[string]interface{}{
		"tweetId": issueID,
		"content": content,
	}

	body, err := json.Marshal(post)
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

	var createdgithub.com / Khmelov / Distcomp / 351001 / Ushakov
	model.github.com / Khmelov / Distcomp / 351001 / Ushakov
	if err := json.NewDecoder(resp.Body).Decode(&createdgithub.com / Khmelov / Distcomp / 351001 / Ushakov); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &createdgithub.com / Khmelov / Distcomp / 351001 / Ushakov, nil
}

func (c *Client) Updategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id, issueID int64, content string) (*model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	post := map[string]interface{}{
		"id":      id,
		"tweetId": issueID,
		"content": content,
	}

	body, err := json.Marshal(post)
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

	var updatedgithub.com / Khmelov / Distcomp / 351001 / Ushakov
	model.github.com / Khmelov / Distcomp / 351001 / Ushakov
	if err := json.NewDecoder(resp.Body).Decode(&updatedgithub.com / Khmelov / Distcomp / 351001 / Ushakov); err != nil {
		return nil, fmt.Errorf("failed to decode response: %w", err)
	}

	return &updatedgithub.com / Khmelov / Distcomp / 351001 / Ushakov, nil
}

func (c *Client) Deletegithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) error {
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

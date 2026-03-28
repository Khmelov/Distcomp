package client

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"time"

	// Укажите правильный путь до вашего пакета notedto
	notedto "labs/shared/dto/note"
	notemodel "labs/shared/model/note"
)

// DiscussionClient - интерфейс для общения с сервисом Discussion
type DiscussionClient interface {
	SyncCreate(ctx context.Context, note *notemodel.Note) error
	SyncUpdate(ctx context.Context, note *notemodel.Note) error
	SyncDelete(ctx context.Context, id int64) error
	SyncGet(ctx context.Context, id int64) (*notemodel.Note, error)
	SyncList(ctx context.Context) ([]*notemodel.Note, error)
}

type discussionHTTPClient struct {
	baseURL    string
	httpClient *http.Client
}

// NewDiscussionClient создает нового HTTP клиента
func NewDiscussionClient(baseURL string) DiscussionClient {
	return &discussionHTTPClient{
		baseURL: baseURL,
		httpClient: &http.Client{
			Timeout: 5 * time.Second,
		},
	}
}

func (c *discussionHTTPClient) SyncCreate(ctx context.Context, note *notemodel.Note) error {
	// Встраиваем ваш DTO в анонимную структуру, чтобы добавить ID для синхронизации,
	// не меняя при этом саму модель CreateNoteRequest.
	payload := struct {
		ID int64 `json:"id"`
		notedto.CreateNoteRequest
	}{
		ID: note.ID,
		CreateNoteRequest: notedto.CreateNoteRequest{
			IssueID: note.IssueID,
			Content: note.Content,
		},
	}

	jsonData, err := json.Marshal(payload)
	if err != nil {
		return fmt.Errorf("failed to marshal note create request: %w", err)
	}

	url := fmt.Sprintf("%s/api/v1.0/notes", c.baseURL)
	req, err := http.NewRequestWithContext(ctx, http.MethodPost, url, bytes.NewBuffer(jsonData))
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusCreated && resp.StatusCode != http.StatusOK {
		return fmt.Errorf("unexpected status code from discussion service on create: %d", resp.StatusCode)
	}

	return nil
}

func (c *discussionHTTPClient) SyncUpdate(ctx context.Context, note *notemodel.Note) error {
	// Используем ваш UpdateNoteRequest (передаем указатель на строку)
	content := note.Content
	payload := notedto.UpdateNoteRequest{
		Content: &content,
	}

	jsonData, err := json.Marshal(payload)
	if err != nil {
		return fmt.Errorf("failed to marshal note update request: %w", err)
	}

	url := fmt.Sprintf("%s/api/v1.0/notes/%d", c.baseURL, note.ID)
	req, err := http.NewRequestWithContext(ctx, http.MethodPut, url, bytes.NewBuffer(jsonData))
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK && resp.StatusCode != http.StatusNoContent {
		return fmt.Errorf("unexpected status code from discussion service on update: %d", resp.StatusCode)
	}

	return nil
}

func (c *discussionHTTPClient) SyncDelete(ctx context.Context, id int64) error {
	url := fmt.Sprintf("%s/api/v1.0/notes/%d", c.baseURL, id)
	req, err := http.NewRequestWithContext(ctx, http.MethodDelete, url, nil)
	if err != nil {
		return err
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK && resp.StatusCode != http.StatusNoContent {
		return fmt.Errorf("unexpected status code from discussion service on delete: %d", resp.StatusCode)
	}

	return nil
}

func (c *discussionHTTPClient) SyncGet(ctx context.Context, id int64) (*notemodel.Note, error) {
	url := fmt.Sprintf("%s/api/v1.0/notes/%d", c.baseURL, id)
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode == http.StatusNotFound {
		return nil, fmt.Errorf("note not found")
	}
	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unexpected status code on get: %d", resp.StatusCode)
	}

	// Читаем ответ в ваш NoteResponse DTO
	var resDTO notedto.NoteResponse
	if err := json.NewDecoder(resp.Body).Decode(&resDTO); err != nil {
		return nil, err
	}

	// Мапим DTO обратно во внутреннюю модель
	return &notemodel.Note{
		ID:      resDTO.ID,
		IssueID: resDTO.IssueID,
		Content: resDTO.Content,
	}, nil
}

func (c *discussionHTTPClient) SyncList(ctx context.Context) ([]*notemodel.Note, error) {
	url := fmt.Sprintf("%s/api/v1.0/notes", c.baseURL)
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil, err
	}

	resp, err := c.httpClient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("unexpected status code on list: %d", resp.StatusCode)
	}

	// Читаем массив DTO
	var resDTOs []*notedto.NoteResponse
	if err := json.NewDecoder(resp.Body).Decode(&resDTOs); err != nil {
		return nil, err
	}

	// Мапим массив DTO обратно во внутренние модели
	notes := make([]*notemodel.Note, len(resDTOs))
	for i, dto := range resDTOs {
		notes[i] = &notemodel.Note{
			ID:      dto.ID,
			IssueID: dto.IssueID,
			Content: dto.Content,
		}
	}

	return notes, nil
}

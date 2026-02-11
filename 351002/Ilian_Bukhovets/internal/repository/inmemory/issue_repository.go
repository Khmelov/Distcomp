package inmemory

import (
	"errors"
	"strings"
	"sync"

	"github.com/bsuir/rest-api/internal/domain"
)

var (
	ErrIssueNotFound = errors.New("issue not found")
)

// IssueRepositoryInMemory реализует InMemory хранилище для задач
type IssueRepositoryInMemory struct {
	mu     sync.RWMutex
	issues map[int64]*domain.Issue
}

// NewIssueRepositoryInMemory создает новый экземпляр репозитория
func NewIssueRepositoryInMemory() *IssueRepositoryInMemory {
	return &IssueRepositoryInMemory{
		issues: make(map[int64]*domain.Issue),
	}
}

// Create создает новую задачу
func (r *IssueRepositoryInMemory) Create(issue *domain.Issue) (*domain.Issue, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	r.issues[issue.ID] = issue
	return issue, nil
}

// GetByID получает задачу по ID
func (r *IssueRepositoryInMemory) GetByID(id int64) (*domain.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	issue, exists := r.issues[id]
	if !exists {
		return nil, ErrIssueNotFound
	}
	return issue, nil
}

// GetAll получает все задачи
func (r *IssueRepositoryInMemory) GetAll() ([]*domain.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	issues := make([]*domain.Issue, 0, len(r.issues))
	for _, issue := range r.issues {
		issues = append(issues, issue)
	}
	return issues, nil
}

// Update обновляет задачу
func (r *IssueRepositoryInMemory) Update(id int64, issue *domain.Issue) (*domain.Issue, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.issues[id]; !exists {
		return nil, ErrIssueNotFound
	}
	issue.ID = id
	r.issues[id] = issue
	return issue, nil
}

// Delete удаляет задачу
func (r *IssueRepositoryInMemory) Delete(id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.issues[id]; !exists {
		return ErrIssueNotFound
	}
	delete(r.issues, id)
	return nil
}

// GetByAuthorID получает задачи по ID автора
func (r *IssueRepositoryInMemory) GetByAuthorID(authorID int64) ([]*domain.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	var issues []*domain.Issue
	for _, issue := range r.issues {
		if issue.AuthorID == authorID {
			issues = append(issues, issue)
		}
	}
	return issues, nil
}

// GetByMarkerName получает задачи по имени метки
func (r *IssueRepositoryInMemory) GetByMarkerName(name string) ([]*domain.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	var issues []*domain.Issue
	for _, issue := range r.issues {
		// Эта функция будет использоваться через сервис, который получит маркер по ID
		_ = issue.MarkerIDs
	}
	return issues, nil
}

// GetByMarkerIDs получает задачи по ID меток
func (r *IssueRepositoryInMemory) GetByMarkerIDs(markerIDs []int64) ([]*domain.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	markerSet := make(map[int64]bool)
	for _, id := range markerIDs {
		markerSet[id] = true
	}
	var issues []*domain.Issue
	for _, issue := range r.issues {
		for _, markerID := range issue.MarkerIDs {
			if markerSet[markerID] {
				issues = append(issues, issue)
				break
			}
		}
	}
	return issues, nil
}

// GetByAuthorLogin получает задачи по логину автора
func (r *IssueRepositoryInMemory) GetByAuthorLogin(login string) ([]*domain.Issue, error) {
	// Эта функция будет использоваться через сервис, который получит authorID по login
	return nil, nil
}

// GetByFilters получает задачи по фильтрам
func (r *IssueRepositoryInMemory) GetByFilters(markerNames []string, markerIDs []int64, authorLogin, title, content string) ([]*domain.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	var issues []*domain.Issue
	for _, issue := range r.issues {
		matches := true
		if title != "" && !strings.Contains(strings.ToLower(issue.Title), strings.ToLower(title)) {
			matches = false
		}
		if content != "" && !strings.Contains(strings.ToLower(issue.Content), strings.ToLower(content)) {
			matches = false
		}
		if matches {
			issues = append(issues, issue)
		}
	}
	return issues, nil
}


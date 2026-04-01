package inmemory

import (
	"errors"
	"sync"

	"github.com/bsuir/rest-api/internal/domain"
)

var (
	ErrAuthorNotFound = errors.New("author not found")
)

// AuthorRepositoryInMemory реализует InMemory хранилище для авторов
type AuthorRepositoryInMemory struct {
	mu      sync.RWMutex
	authors map[int64]*domain.Author
}

// NewAuthorRepositoryInMemory создает новый экземпляр репозитория
func NewAuthorRepositoryInMemory() *AuthorRepositoryInMemory {
	return &AuthorRepositoryInMemory{
		authors: make(map[int64]*domain.Author),
	}
}

// Create создает нового автора
func (r *AuthorRepositoryInMemory) Create(author *domain.Author) (*domain.Author, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	r.authors[author.ID] = author
	return author, nil
}

// GetByID получает автора по ID
func (r *AuthorRepositoryInMemory) GetByID(id int64) (*domain.Author, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	author, exists := r.authors[id]
	if !exists {
		return nil, ErrAuthorNotFound
	}
	return author, nil
}

// GetAll получает всех авторов
func (r *AuthorRepositoryInMemory) GetAll() ([]*domain.Author, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	authors := make([]*domain.Author, 0, len(r.authors))
	for _, author := range r.authors {
		authors = append(authors, author)
	}
	return authors, nil
}

// Update обновляет автора
func (r *AuthorRepositoryInMemory) Update(id int64, author *domain.Author) (*domain.Author, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.authors[id]; !exists {
		return nil, ErrAuthorNotFound
	}
	author.ID = id
	r.authors[id] = author
	return author, nil
}

// Delete удаляет автора
func (r *AuthorRepositoryInMemory) Delete(id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.authors[id]; !exists {
		return ErrAuthorNotFound
	}
	delete(r.authors, id)
	return nil
}


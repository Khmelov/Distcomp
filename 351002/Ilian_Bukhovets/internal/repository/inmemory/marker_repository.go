package inmemory

import (
	"errors"
	"sync"

	"github.com/bsuir/rest-api/internal/domain"
)

var (
	ErrMarkerNotFound = errors.New("marker not found")
)

// MarkerRepositoryInMemory реализует InMemory хранилище для меток
type MarkerRepositoryInMemory struct {
	mu      sync.RWMutex
	markers map[int64]*domain.Marker
}

// NewMarkerRepositoryInMemory создает новый экземпляр репозитория
func NewMarkerRepositoryInMemory() *MarkerRepositoryInMemory {
	return &MarkerRepositoryInMemory{
		markers: make(map[int64]*domain.Marker),
	}
}

// Create создает новую метку
func (r *MarkerRepositoryInMemory) Create(marker *domain.Marker) (*domain.Marker, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	r.markers[marker.ID] = marker
	return marker, nil
}

// GetByID получает метку по ID
func (r *MarkerRepositoryInMemory) GetByID(id int64) (*domain.Marker, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	marker, exists := r.markers[id]
	if !exists {
		return nil, ErrMarkerNotFound
	}
	return marker, nil
}

// GetAll получает все метки
func (r *MarkerRepositoryInMemory) GetAll() ([]*domain.Marker, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	markers := make([]*domain.Marker, 0, len(r.markers))
	for _, marker := range r.markers {
		markers = append(markers, marker)
	}
	return markers, nil
}

// Update обновляет метку
func (r *MarkerRepositoryInMemory) Update(id int64, marker *domain.Marker) (*domain.Marker, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.markers[id]; !exists {
		return nil, ErrMarkerNotFound
	}
	marker.ID = id
	r.markers[id] = marker
	return marker, nil
}

// Delete удаляет метку
func (r *MarkerRepositoryInMemory) Delete(id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.markers[id]; !exists {
		return ErrMarkerNotFound
	}
	delete(r.markers, id)
	return nil
}

// GetByIssueID получает метки по ID задачи
func (r *MarkerRepositoryInMemory) GetByIssueID(issueID int64) ([]*domain.Marker, error) {
	// Эта функция будет использоваться через сервис, который получит markerIDs из issue
	return nil, nil
}


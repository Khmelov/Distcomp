package storage

import (
	"context"
	"errors"
	"sync"
)

var ErrNotFound = errors.New("entity not found")

type InMemoryRepository struct {
	mu     sync.RWMutex
	data   map[int64]Identifiable
	nextID int64
}

func NewInMemoryRepository() *InMemoryRepository {
	return &InMemoryRepository{
		data:   make(map[int64]Identifiable),
		nextID: 1,
	}
}

func (r *InMemoryRepository) Create(ctx context.Context, entity Identifiable) (Identifiable, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	id := r.nextID
	r.nextID++

	entity.SetID(id)
	r.data[id] = entity

	return entity, nil
}

func (r *InMemoryRepository) FindByID(ctx context.Context, id int64) (Identifiable, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	if v, ok := r.data[id]; ok {
		return v, nil
	}

	return nil, ErrNotFound
}

func (r *InMemoryRepository) FindAll(ctx context.Context) ([]Identifiable, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	result := make([]Identifiable, 0, len(r.data))
	for _, v := range r.data {
		result = append(result, v)
	}
	return result, nil
}

func (r *InMemoryRepository) Update(ctx context.Context, id int64, entity Identifiable) (Identifiable, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, ok := r.data[id]; !ok {
		return nil, ErrNotFound
	}

	entity.SetID(id)
	r.data[id] = entity
	return entity, nil
}

func (r *InMemoryRepository) Delete(ctx context.Context, id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, ok := r.data[id]; !ok {
		return ErrNotFound
	}
	delete(r.data, id)
	return nil
}

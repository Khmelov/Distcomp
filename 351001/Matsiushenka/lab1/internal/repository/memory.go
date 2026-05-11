package repository

import (
	"sync"
	"sync/atomic"
)

type Storage[T any] struct {
	data   map[int64]T
	mu     sync.RWMutex
	nextID int64
}

func NewStorage[T any]() *Storage[T] {
	return &Storage[T]{data: make(map[int64]T), nextID: 1}
}

func (s *Storage[T]) Create(item T, setID func(*T, int64)) T {
	s.mu.Lock()
	defer s.mu.Unlock()
	id := atomic.AddInt64(&s.nextID, 1) - 1
	setID(&item, id)
	s.data[id] = item
	return item
}

func (s *Storage[T]) Get(id int64) (T, bool) {
	s.mu.RLock()
	defer s.mu.RUnlock()
	v, ok := s.data[id]
	return v, ok
}

func (s *Storage[T]) GetAll() []T {
	s.mu.RLock()
	defer s.mu.RUnlock()
	res := make([]T, 0, len(s.data))
	for _, v := range s.data {
		res = append(res, v)
	}
	return res
}

func (s *Storage[T]) Update(id int64, item T) bool {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.data[id]; !ok {
		return false
	}
	s.data[id] = item
	return true
}

func (s *Storage[T]) Delete(id int64) bool {
	s.mu.Lock()
	defer s.mu.Unlock()
	if _, ok := s.data[id]; !ok {
		return false
	}
	delete(s.data, id)
	return true
}

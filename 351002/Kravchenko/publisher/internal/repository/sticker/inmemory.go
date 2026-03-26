package sticker

import (
	"context"
	stickermodel "labs/shared/model/sticker"
	"sort"
	"sync"
)

type stickerInMemoryRepository struct {
	data   map[int64]*stickermodel.Sticker
	byName map[string]int64 // name -> ID
	nextID int64
	mu     sync.RWMutex
}

func NewStickerInMemoryRepository() Repository {
	return &stickerInMemoryRepository{
		data:   make(map[int64]*stickermodel.Sticker),
		byName: make(map[string]int64),
		nextID: 1,
	}
}

func (r *stickerInMemoryRepository) Create(ctx context.Context, sticker *stickermodel.Sticker) (*stickermodel.Sticker, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.byName[sticker.Name]; exists {
		return nil, stickermodel.ErrNameTaken
	}

	sticker.ID = r.nextID
	r.nextID++

	copied := *sticker
	r.data[sticker.ID] = &copied
	r.byName[sticker.Name] = sticker.ID

	return &copied, nil
}

func (r *stickerInMemoryRepository) GetByID(ctx context.Context, id int64) (*stickermodel.Sticker, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	sticker, exists := r.data[id]
	if !exists {
		return nil, stickermodel.ErrNotFound
	}

	copied := *sticker
	return &copied, nil
}

func (r *stickerInMemoryRepository) GetByName(ctx context.Context, name string) (*stickermodel.Sticker, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	id, exists := r.byName[name]
	if !exists {
		return nil, stickermodel.ErrNotFound
	}

	sticker := r.data[id]
	copied := *sticker
	return &copied, nil
}

func (r *stickerInMemoryRepository) Update(ctx context.Context, sticker *stickermodel.Sticker) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[sticker.ID]
	if !exists {
		return stickermodel.ErrNotFound
	}

	if existing.Name != sticker.Name {
		if _, taken := r.byName[sticker.Name]; taken {
			return stickermodel.ErrNameTaken
		}
		delete(r.byName, existing.Name)
		r.byName[sticker.Name] = sticker.ID
	}

	copied := *sticker
	r.data[sticker.ID] = &copied
	return nil
}

func (r *stickerInMemoryRepository) Delete(ctx context.Context, id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[id]
	if !exists {
		return stickermodel.ErrNotFound
	}

	delete(r.byName, existing.Name)
	delete(r.data, id)
	return nil
}

func (r *stickerInMemoryRepository) List(ctx context.Context, limit, offset int) ([]*stickermodel.Sticker, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	keys := make([]int64, 0, len(r.data))
	for k := range r.data {
		keys = append(keys, k)
	}
	sort.Slice(keys, func(i, j int) bool { return keys[i] < keys[j] })

	start := offset
	if start > len(keys) {
		start = len(keys)
	}
	end := start + limit
	if end > len(keys) {
		end = len(keys)
	}

	result := make([]*stickermodel.Sticker, 0, end-start)
	for i := start; i < end; i++ {
		copied := *r.data[keys[i]]
		result = append(result, &copied)
	}

	return result, nil
}

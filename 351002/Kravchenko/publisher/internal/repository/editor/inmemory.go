package editor

import (
	"context"
	editormodel "labs/shared/model/editor"
	"sort"
	"sync"
)

type editorInMemoryRepository struct {
	data    map[int64]*editormodel.Editor
	byLogin map[string]int64
	nextID  int64
	mu      sync.RWMutex
}

func NewEditorInMemoryRepository() Repository {
	return &editorInMemoryRepository{
		data:    make(map[int64]*editormodel.Editor),
		byLogin: make(map[string]int64),
		nextID:  1,
	}
}

func (r *editorInMemoryRepository) Create(ctx context.Context, editor *editormodel.Editor) (*editormodel.Editor, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.byLogin[editor.Login]; exists {
		return nil, editormodel.ErrLoginTaken
	}

	editor.ID = r.nextID
	r.nextID++
	copied := *editor
	r.data[editor.ID] = &copied
	r.byLogin[editor.Login] = editor.ID

	return &copied, nil
}

func (r *editorInMemoryRepository) GetByID(ctx context.Context, id int64) (*editormodel.Editor, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	editor, exists := r.data[id]
	if !exists {
		return nil, editormodel.ErrNotFound
	}

	copied := *editor
	return &copied, nil
}

func (r *editorInMemoryRepository) GetByLogin(ctx context.Context, login string) (*editormodel.Editor, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	id, exists := r.byLogin[login]
	if !exists {
		return nil, editormodel.ErrNotFound
	}

	editor := r.data[id]
	copied := *editor
	return &copied, nil
}

func (r *editorInMemoryRepository) Update(ctx context.Context, editor *editormodel.Editor) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[editor.ID]
	if !exists {
		return editormodel.ErrNotFound
	}

	if existing.Login != editor.Login {
		if _, taken := r.byLogin[editor.Login]; taken {
			return editormodel.ErrLoginTaken
		}
		delete(r.byLogin, existing.Login)
		r.byLogin[editor.Login] = editor.ID
	}

	copied := *editor
	r.data[editor.ID] = &copied
	return nil
}

func (r *editorInMemoryRepository) Delete(ctx context.Context, id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[id]
	if !exists {
		return editormodel.ErrNotFound
	}

	delete(r.byLogin, existing.Login)
	delete(r.data, id)
	return nil
}

func (r *editorInMemoryRepository) List(ctx context.Context, limit, offset int) ([]*editormodel.Editor, error) {
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

	result := make([]*editormodel.Editor, 0, end-start)
	for i := start; i < end; i++ {
		copied := *r.data[keys[i]]
		result = append(result, &copied)
	}

	return result, nil
}

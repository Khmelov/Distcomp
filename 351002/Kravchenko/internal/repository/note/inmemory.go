package note

import (
	"context"
	"sort"
	"sync"

	notemodel "lab1/internal/model/note"
)

type noteInMemoryRepository struct {
	data      map[int64]*notemodel.Note
	byIssueID map[int64]map[int64]struct{}
	nextID    int64
	mu        sync.RWMutex
}

func NewNoteInMemoryRepository() Repository {
	return &noteInMemoryRepository{
		data:      make(map[int64]*notemodel.Note),
		byIssueID: make(map[int64]map[int64]struct{}),
		nextID:    1,
	}
}

func (r *noteInMemoryRepository) Create(ctx context.Context, note *notemodel.Note) (*notemodel.Note, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	note.ID = r.nextID
	r.nextID++

	copied := *note
	r.data[note.ID] = &copied

	if r.byIssueID[note.IssueID] == nil {
		r.byIssueID[note.IssueID] = make(map[int64]struct{})
	}
	r.byIssueID[note.IssueID][note.ID] = struct{}{}

	return &copied, nil
}

func (r *noteInMemoryRepository) GetByID(ctx context.Context, id int64) (*notemodel.Note, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	note, exists := r.data[id]
	if !exists {
		return nil, notemodel.ErrNotFound
	}

	copied := *note
	return &copied, nil
}

func (r *noteInMemoryRepository) Update(ctx context.Context, note *notemodel.Note) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[note.ID]
	if !exists {
		return notemodel.ErrNotFound
	}

	if existing.IssueID != note.IssueID {
		delete(r.byIssueID[existing.IssueID], note.ID)
		if r.byIssueID[note.IssueID] == nil {
			r.byIssueID[note.IssueID] = make(map[int64]struct{})
		}
		r.byIssueID[note.IssueID][note.ID] = struct{}{}
	}

	copied := *note
	r.data[note.ID] = &copied
	return nil
}

func (r *noteInMemoryRepository) Delete(ctx context.Context, id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[id]
	if !exists {
		return notemodel.ErrNotFound
	}

	delete(r.byIssueID[existing.IssueID], id)
	delete(r.data, id)
	return nil
}

func (r *noteInMemoryRepository) List(ctx context.Context, limit, offset int) ([]*notemodel.Note, error) {
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

	result := make([]*notemodel.Note, 0, end-start)
	for i := start; i < end; i++ {
		copied := *r.data[keys[i]]
		result = append(result, &copied)
	}

	return result, nil
}

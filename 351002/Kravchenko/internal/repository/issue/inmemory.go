package issue

import (
	"context"
	"sort"
	"sync"
	"time"

	issuemodel "lab1/internal/model/issue"
	stickermodel "lab1/internal/model/sticker"
	"lab1/internal/repository/sticker"
)

type issueInMemoryRepository struct {
	data          map[int64]*issuemodel.Issue
	byEditorID    map[int64]map[int64]struct{}
	issueStickers map[int64][]int64
	stickerRepo   sticker.Repository
	nextID        int64
	mu            sync.RWMutex
}

func NewIssueInMemoryRepository(stickerRepo sticker.Repository) Repository {
	return &issueInMemoryRepository{
		data:          make(map[int64]*issuemodel.Issue),
		byEditorID:    make(map[int64]map[int64]struct{}),
		issueStickers: make(map[int64][]int64),
		stickerRepo:   stickerRepo,
		nextID:        1,
	}
}

func (r *issueInMemoryRepository) Create(ctx context.Context, issue *issuemodel.Issue) (*issuemodel.Issue, error) {
	r.mu.Lock()
	defer r.mu.Unlock()

	now := time.Now()
	issue.ID = r.nextID
	issue.Created = now
	issue.Modified = now
	r.nextID++

	copied := *issue
	r.data[issue.ID] = &copied

	if r.byEditorID[issue.EditorID] == nil {
		r.byEditorID[issue.EditorID] = make(map[int64]struct{})
	}
	r.byEditorID[issue.EditorID][issue.ID] = struct{}{}

	return &copied, nil
}

func (r *issueInMemoryRepository) GetByID(ctx context.Context, id int64) (*issuemodel.Issue, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	issue, exists := r.data[id]
	if !exists {
		return nil, issuemodel.ErrNotFound
	}

	copied := *issue
	return &copied, nil
}

func (r *issueInMemoryRepository) Update(ctx context.Context, issue *issuemodel.Issue) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[issue.ID]
	if !exists {
		return issuemodel.ErrNotFound
	}

	if existing.EditorID != issue.EditorID {
		delete(r.byEditorID[existing.EditorID], issue.ID)
		if r.byEditorID[issue.EditorID] == nil {
			r.byEditorID[issue.EditorID] = make(map[int64]struct{})
		}
		r.byEditorID[issue.EditorID][issue.ID] = struct{}{}
	}

	issue.Modified = time.Now()
	copied := *issue
	r.data[issue.ID] = &copied
	return nil
}

func (r *issueInMemoryRepository) Delete(ctx context.Context, id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	existing, exists := r.data[id]
	if !exists {
		return issuemodel.ErrNotFound
	}

	delete(r.byEditorID[existing.EditorID], id)
	delete(r.issueStickers, id)
	delete(r.data, id)
	return nil
}

func (r *issueInMemoryRepository) List(ctx context.Context, limit, offset int) ([]*issuemodel.Issue, error) {
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

	result := make([]*issuemodel.Issue, 0, end-start)
	for i := start; i < end; i++ {
		copied := *r.data[keys[i]]
		result = append(result, &copied)
	}

	return result, nil
}

func (r *issueInMemoryRepository) GetStickers(ctx context.Context, issueID int64) ([]*stickermodel.Sticker, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()

	stickerIDs := r.issueStickers[issueID]
	if len(stickerIDs) == 0 {
		return []*stickermodel.Sticker{}, nil
	}

	stickers := make([]*stickermodel.Sticker, 0, len(stickerIDs))
	for _, stickerID := range stickerIDs {
		sticker, err := r.stickerRepo.GetByID(ctx, stickerID)
		if err == nil {
			stickers = append(stickers, sticker)
		}
	}

	return stickers, nil
}

func (r *issueInMemoryRepository) SetStickers(ctx context.Context, issueID int64, stickerIDs []int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()

	if _, exists := r.data[issueID]; !exists {
		return issuemodel.ErrNotFound
	}

	r.issueStickers[issueID] = stickerIDs
	return nil
}

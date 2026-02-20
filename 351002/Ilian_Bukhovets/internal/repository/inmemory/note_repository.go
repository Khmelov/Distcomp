package inmemory

import (
	"errors"
	"sync"

	"github.com/bsuir/rest-api/internal/domain"
)

var (
	ErrNoteNotFound = errors.New("note not found")
)

// NoteRepositoryInMemory реализует InMemory хранилище для заметок
type NoteRepositoryInMemory struct {
	mu    sync.RWMutex
	notes map[int64]*domain.Note
}

// NewNoteRepositoryInMemory создает новый экземпляр репозитория
func NewNoteRepositoryInMemory() *NoteRepositoryInMemory {
	return &NoteRepositoryInMemory{
		notes: make(map[int64]*domain.Note),
	}
}

// Create создает новую заметку
func (r *NoteRepositoryInMemory) Create(note *domain.Note) (*domain.Note, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	r.notes[note.ID] = note
	return note, nil
}

// GetByID получает заметку по ID
func (r *NoteRepositoryInMemory) GetByID(id int64) (*domain.Note, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	note, exists := r.notes[id]
	if !exists {
		return nil, ErrNoteNotFound
	}
	return note, nil
}

// GetAll получает все заметки
func (r *NoteRepositoryInMemory) GetAll() ([]*domain.Note, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	notes := make([]*domain.Note, 0, len(r.notes))
	for _, note := range r.notes {
		notes = append(notes, note)
	}
	return notes, nil
}

// Update обновляет заметку
func (r *NoteRepositoryInMemory) Update(id int64, note *domain.Note) (*domain.Note, error) {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.notes[id]; !exists {
		return nil, ErrNoteNotFound
	}
	note.ID = id
	r.notes[id] = note
	return note, nil
}

// Delete удаляет заметку
func (r *NoteRepositoryInMemory) Delete(id int64) error {
	r.mu.Lock()
	defer r.mu.Unlock()
	if _, exists := r.notes[id]; !exists {
		return ErrNoteNotFound
	}
	delete(r.notes, id)
	return nil
}

// GetByIssueID получает заметки по ID задачи
func (r *NoteRepositoryInMemory) GetByIssueID(issueID int64) ([]*domain.Note, error) {
	r.mu.RLock()
	defer r.mu.RUnlock()
	var notes []*domain.Note
	for _, note := range r.notes {
		if note.IssueID == issueID {
			notes = append(notes, note)
		}
	}
	return notes, nil
}


package note

import (
	"context"
	"labs/publisher/internal/repository"
	notemodel "labs/shared/model/note"
)

type Service interface {
	CreateNote(ctx context.Context, input *notemodel.CreateNoteInput) (*notemodel.Note, error)
	GetNote(ctx context.Context, id int64) (*notemodel.Note, error)
	UpdateNote(ctx context.Context, id int64, input *notemodel.UpdateNoteInput) (*notemodel.Note, error)
	DeleteNote(ctx context.Context, id int64) error
	ListNotes(ctx context.Context, limit, offset int) ([]*notemodel.Note, error)
}
type noteServiceImpl struct {
	repos repository.AppRepository
}

func New(repos repository.AppRepository) Service {
	return &noteServiceImpl{repos: repos}
}

func (s *noteServiceImpl) CreateNote(ctx context.Context, input *notemodel.CreateNoteInput) (*notemodel.Note, error) {
	_, err := s.repos.IssueRepo().GetByID(ctx, input.IssueID)
	if err != nil {
		return nil, err
	}

	note := &notemodel.Note{
		IssueID: input.IssueID,
		Content: input.Content,
	}

	return s.repos.NoteRepo().Create(ctx, note)
}

func (s *noteServiceImpl) GetNote(ctx context.Context, id int64) (*notemodel.Note, error) {
	return s.repos.NoteRepo().GetByID(ctx, id)
}

func (s *noteServiceImpl) UpdateNote(ctx context.Context, id int64, input *notemodel.UpdateNoteInput) (*notemodel.Note, error) {
	note, err := s.repos.NoteRepo().GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	if input.Content != nil {
		note.Content = *input.Content
	}

	err = s.repos.NoteRepo().Update(ctx, note)
	if err != nil {
		return nil, err
	}

	return note, nil
}

func (s *noteServiceImpl) DeleteNote(ctx context.Context, id int64) error {
	return s.repos.NoteRepo().Delete(ctx, id)
}

func (s *noteServiceImpl) ListNotes(ctx context.Context, limit, offset int) ([]*notemodel.Note, error) {
	return s.repos.NoteRepo().List(ctx, limit, offset)
}

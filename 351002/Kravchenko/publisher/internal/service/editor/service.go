package editor

import (
	"context"
	"errors"
	"labs/publisher/internal/repository"
	editormodel "labs/shared/model/editor"
)

type Service interface {
	CreateEditor(ctx context.Context, input *editormodel.CreateEditorInput) (*editormodel.Editor, error)
	GetEditor(ctx context.Context, id int64) (*editormodel.Editor, error)
	UpdateEditor(ctx context.Context, id int64, input *editormodel.UpdateEditorInput) (*editormodel.Editor, error)
	DeleteEditor(ctx context.Context, id int64) error
	ListEditors(ctx context.Context, limit, offset int) ([]*editormodel.Editor, error)
}

type editorServiceImpl struct {
	repos repository.AppRepository
}

func New(repos repository.AppRepository) Service {
	return &editorServiceImpl{repos: repos}
}

func (s *editorServiceImpl) CreateEditor(ctx context.Context, input *editormodel.CreateEditorInput) (*editormodel.Editor, error) {
	_, err := s.repos.EditorRepo().GetByLogin(ctx, input.Login)
	if err == nil {
		return nil, editormodel.ErrLoginTaken
	}
	if !errors.Is(err, editormodel.ErrNotFound) {
		return nil, err
	}

	editor := &editormodel.Editor{
		Login:     input.Login,
		Password:  input.Password,
		Firstname: input.Firstname,
		Lastname:  input.Lastname,
	}

	return s.repos.EditorRepo().Create(ctx, editor)
}

func (s *editorServiceImpl) GetEditor(ctx context.Context, id int64) (*editormodel.Editor, error) {
	return s.repos.EditorRepo().GetByID(ctx, id)
}

func (s *editorServiceImpl) UpdateEditor(ctx context.Context, id int64, input *editormodel.UpdateEditorInput) (*editormodel.Editor, error) {
	editor, err := s.repos.EditorRepo().GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	if input.Login != nil {
		existingEditor, err := s.repos.EditorRepo().GetByLogin(ctx, *input.Login)
		if err == nil {
			if existingEditor.ID != id {
				return nil, editormodel.ErrLoginTaken
			}
		} else if !errors.Is(err, editormodel.ErrNotFound) {
			return nil, err
		}
		editor.Login = *input.Login
	}

	if input.Password != nil {
		editor.Password = *input.Password
	}
	if input.Firstname != nil {
		editor.Firstname = *input.Firstname
	}
	if input.Lastname != nil {
		editor.Lastname = *input.Lastname
	}

	err = s.repos.EditorRepo().Update(ctx, editor)
	if err != nil {
		return nil, err
	}

	return editor, nil
}

func (s *editorServiceImpl) DeleteEditor(ctx context.Context, id int64) error {
	return s.repos.EditorRepo().Delete(ctx, id)
}

func (s *editorServiceImpl) ListEditors(ctx context.Context, limit, offset int) ([]*editormodel.Editor, error) {
	return s.repos.EditorRepo().List(ctx, limit, offset)
}

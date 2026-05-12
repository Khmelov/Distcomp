package editor

import (
	"context"
	editormodel "labs/shared/model/editor"
)

type Repository interface {
	Create(ctx context.Context, editor *editormodel.Editor) (*editormodel.Editor, error)
	GetByID(ctx context.Context, id int64) (*editormodel.Editor, error)
	GetByLogin(ctx context.Context, login string) (*editormodel.Editor, error)
	Update(ctx context.Context, editor *editormodel.Editor) error
	Delete(ctx context.Context, id int64) error
	List(ctx context.Context, limit int, offset int) ([]*editormodel.Editor, error)
}

type Cache interface {
	Get(ctx context.Context, id int64) (*editormodel.Editor, error)
	Set(ctx context.Context, editor *editormodel.Editor) error
	Delete(ctx context.Context, id int64) error
}

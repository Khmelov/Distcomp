package auth

import (
	"context"
	editormodel "labs/shared/model/editorV2"
)

type Repository interface {
	GetByLogin(ctx context.Context, login string) (*editormodel.Editor, error)
	Register(ctx context.Context, editor *editormodel.Editor) (*editormodel.Editor, error)
}

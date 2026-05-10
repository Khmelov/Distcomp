package note

import (
	"context"
	notemodel "labs/shared/model/note"
)

type Repository interface {
	Create(ctx context.Context, note *notemodel.Note) (*notemodel.Note, error)
	GetByID(ctx context.Context, id int64) (*notemodel.Note, error)
	Update(ctx context.Context, note *notemodel.Note) error
	Delete(ctx context.Context, id int64) error
	List(ctx context.Context, limit int, offset int) ([]*notemodel.Note, error)
}

type Cache interface {
	Get(ctx context.Context, id int64) (*notemodel.Note, error)
	Set(ctx context.Context, note *notemodel.Note) error
	Delete(ctx context.Context, noteID int64, issueID int64) error
}

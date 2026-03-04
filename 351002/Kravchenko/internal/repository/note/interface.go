package note

import (
	"context"
	notemodel "lab1/internal/model/note"
)

type Repository interface {
	Create(ctx context.Context, note *notemodel.Note) (*notemodel.Note, error)
	GetByID(ctx context.Context, id int64) (*notemodel.Note, error)
	Update(ctx context.Context, note *notemodel.Note) error
	Delete(ctx context.Context, id int64) error
	List(ctx context.Context, limit int, offset int) ([]*notemodel.Note, error)
}

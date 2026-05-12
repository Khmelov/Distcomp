package repository

import "context"

type NoteRepository[T any] interface {
	Create(ctx context.Context, item T) (T, error)
	GetByID(ctx context.Context, id int64) (T, error)
	Update(ctx context.Context, id int64, item T) (T, error)
	Delete(ctx context.Context, id int64) error
	GetAll(ctx context.Context, limit, offset int) ([]T, error)
}

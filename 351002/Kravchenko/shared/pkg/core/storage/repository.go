package storage

import "context"

type Identifiable interface {
	GetID() int64
	SetID(id int64)
}

type Repository interface {
	Create(ctx context.Context, entity Identifiable) (Identifiable, error)
	FindByID(ctx context.Context, id int64) (Identifiable, error)
	FindAll(ctx context.Context) ([]Identifiable, error)
	Update(ctx context.Context, id int64, entity Identifiable) (Identifiable, error)
	Delete(ctx context.Context, id int64) error
}

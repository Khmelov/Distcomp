package models

import "context"

type Marker struct {
	ID   int64
	Name string
}

type MarkerRepository interface {
	Create(ctx context.Context, marker *Marker) error
	GetByID(ctx context.Context, id int64) (*Marker, error)
	GetByName(ctx context.Context, name string) (*Marker, error)
	GetAll(ctx context.Context, limit, offset int) ([]Marker, error)
	Update(ctx context.Context, marker *Marker) (bool, error)
	Delete(ctx context.Context, id int64) (bool, error)
	GetByNewsID(ctx context.Context, newsID int64) ([]Marker, error)
	CountNewsByMarker(ctx context.Context, markerID int64) (int, error)
}

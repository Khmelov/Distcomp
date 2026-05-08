package models

import (
	"context"
	"time"
)

type News struct {
	ID       int64
	UserID   int64
	Title    string
	Content  string
	Created  time.Time
	Modified time.Time
}

type NewsRepository interface {
	Create(ctx context.Context, news *News) error
	CreateWithMarkers(ctx context.Context, news *News, markerNames []string) error
	GetByID(ctx context.Context, id int64) (*News, error)
	GetAll(ctx context.Context, limit, offset int, filters map[string]interface{}, sort string) ([]News, error)
	Update(ctx context.Context, news *News) (bool, error)
	Delete(ctx context.Context, id int64) (bool, error)
	DeleteWithCleanup(ctx context.Context, id int64) (bool, error)
	AddMarker(ctx context.Context, newsID, markerID int64) error
	RemoveMarker(ctx context.Context, newsID, markerID int64) (bool, error)
	GetMarkers(ctx context.Context, newsID int64) ([]Marker, error)
	Search(ctx context.Context, filters map[string]interface{}, limit, offset int) ([]News, error)
}

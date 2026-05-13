package models

import "context"

type Notice struct {
	ID      int64
	NewsID  int64
	Content string
}

type NoticeRepository interface {
	Create(ctx context.Context, notice *Notice) error
	GetByID(ctx context.Context, id int64) (*Notice, error)
	GetAll(ctx context.Context, limit, offset int) ([]Notice, error)
	Update(ctx context.Context, notice *Notice) (bool, error)
	Delete(ctx context.Context, id int64) (bool, error)
	GetByNewsID(ctx context.Context, newsID int64) ([]Notice, error)
}

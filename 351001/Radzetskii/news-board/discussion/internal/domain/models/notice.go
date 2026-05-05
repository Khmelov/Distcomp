package models

import "context"

type Notice struct {
	NewsID  int64
	ID      int64
	Content string
}

type NoticeRepository interface {
	Create(ctx context.Context, notice *Notice) error
	GetAll(ctx context.Context, limit, offset int) ([]Notice, error)
	GetByID(ctx context.Context, id int64) (*Notice, error)
	Update(ctx context.Context, previousNewsID, id int64, notice *Notice) (bool, error)
	Delete(ctx context.Context, newsID, id int64) (bool, error)
	GetByNewsID(ctx context.Context, newsID int64) ([]Notice, error)
}

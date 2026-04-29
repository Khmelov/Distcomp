package models

import "context"

type Notice struct {
	Country string
	NewsID  int64
	ID      int64
	Content string
}

type NoticeRepository interface {
	Create(ctx context.Context, notice *Notice) error
	GetAll(ctx context.Context, country string, limit, offset int) ([]Notice, error)
	GetAllAny(ctx context.Context, limit, offset int) ([]Notice, error)
	GetByID(ctx context.Context, country string, newsID, id int64) (*Notice, error)
	GetByGlobalID(ctx context.Context, id int64) (*Notice, error)
	Update(ctx context.Context, previousCountry string, previousNewsID, previousID int64, notice *Notice) (bool, error)
	Delete(ctx context.Context, country string, newsID, id int64) (bool, error)
	GetByNewsID(ctx context.Context, country string, newsID int64) ([]Notice, error)
	GetByNewsIDAny(ctx context.Context, newsID int64) ([]Notice, error)
}

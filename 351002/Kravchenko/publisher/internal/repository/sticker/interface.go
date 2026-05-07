package sticker

import (
	"context"
	stickermodel "labs/shared/model/sticker"
)

type Repository interface {
	Create(ctx context.Context, sticker *stickermodel.Sticker) (*stickermodel.Sticker, error)
	GetByID(ctx context.Context, id int64) (*stickermodel.Sticker, error)
	GetByName(ctx context.Context, name string) (*stickermodel.Sticker, error)
	Update(ctx context.Context, sticker *stickermodel.Sticker) error
	Delete(ctx context.Context, id int64) error
	List(ctx context.Context, limit int, offset int) ([]*stickermodel.Sticker, error)
}

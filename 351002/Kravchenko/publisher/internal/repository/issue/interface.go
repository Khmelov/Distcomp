package issue

import (
	"context"
	issuemodel "labs/shared/model/issue"
	stickermodel "labs/shared/model/sticker"
)

type Repository interface {
	Create(ctx context.Context, issue *issuemodel.Issue) (*issuemodel.Issue, error)
	GetByID(ctx context.Context, id int64) (*issuemodel.Issue, error)
	Update(ctx context.Context, issue *issuemodel.Issue) error
	Delete(ctx context.Context, id int64) error
	List(ctx context.Context, limit int, offset int) ([]*issuemodel.Issue, error)
	GetStickers(ctx context.Context, issueID int64) ([]*stickermodel.Sticker, error)
	SetStickers(ctx context.Context, issueID int64, stickerIDs []int64) error
}

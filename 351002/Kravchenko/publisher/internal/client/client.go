package client

import (
	"context"
	notemodel "labs/shared/model/note"
)

type DiscussionClient interface {
	SyncCreate(ctx context.Context, note *notemodel.Note) error
	SyncUpdate(ctx context.Context, note *notemodel.Note) error
	SyncDelete(ctx context.Context, id int64) error
	SyncGet(ctx context.Context, id int64) (*notemodel.Note, error)
	SyncList(ctx context.Context) ([]*notemodel.Note, error)
}

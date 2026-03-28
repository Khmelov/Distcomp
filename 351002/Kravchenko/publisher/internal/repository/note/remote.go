package note

import (
	"context"
	"time"

	"labs/publisher/internal/client"
	notemodel "labs/shared/model/note"
)

type noteRemoteRepository struct {
	discClient client.DiscussionClient
}

func NewNoteRemoteRepository(client client.DiscussionClient) Repository {
	return &noteRemoteRepository{
		discClient: client,
	}
}

func (r *noteRemoteRepository) Create(ctx context.Context, note *notemodel.Note) (*notemodel.Note, error) {
	if note.ID == 0 {
		note.ID = time.Now().Unix()*1000 + int64(time.Now().Nanosecond()%1000)
	}

	err := r.discClient.SyncCreate(ctx, note)
	if err != nil {
		return nil, err
	}

	return note, nil
}

func (r *noteRemoteRepository) Update(ctx context.Context, note *notemodel.Note) error {
	return r.discClient.SyncUpdate(ctx, note)
}

func (r *noteRemoteRepository) Delete(ctx context.Context, id int64) error {
	return r.discClient.SyncDelete(ctx, id)
}

func (r *noteRemoteRepository) GetByID(ctx context.Context, id int64) (*notemodel.Note, error) {
	return r.discClient.SyncGet(ctx, id)
}

func (r *noteRemoteRepository) List(ctx context.Context, limit int, offset int) ([]*notemodel.Note, error) {
	return r.discClient.SyncList(ctx)
}

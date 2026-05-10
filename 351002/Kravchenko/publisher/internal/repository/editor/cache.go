package editor

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
	editormodel "labs/shared/model/editor"
)

type CacheRepository struct {
	redis *redis.Client
	ttl   time.Duration
}

func NewCacheRepository(redisClient *redis.Client, ttl time.Duration) *CacheRepository {
	return &CacheRepository{
		redis: redisClient,
		ttl:   ttl,
	}
}

func (r *CacheRepository) getEditorKey(id int64) string {
	return fmt.Sprintf("editor:%d", id)
}

func (r *CacheRepository) Get(ctx context.Context, id int64) (*editormodel.Editor, error) {
	key := r.getEditorKey(id)

	data, err := r.redis.Get(ctx, key).Bytes()
	if err == redis.Nil {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	var editor editormodel.Editor
	if err := json.Unmarshal(data, &editor); err != nil {
		return nil, err
	}

	return &editor, nil
}

func (r *CacheRepository) Set(ctx context.Context, editor *editormodel.Editor) error {
	key := r.getEditorKey(editor.ID)

	data, err := json.Marshal(editor)
	if err != nil {
		return err
	}

	return r.redis.Set(ctx, key, data, r.ttl).Err()
}

func (r *CacheRepository) Delete(ctx context.Context, id int64) error {
	key := r.getEditorKey(id)
	return r.redis.Del(ctx, key).Err()
}

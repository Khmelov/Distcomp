package sticker

import (
	"context"
	"encoding/json"
	"fmt"
	stickermodel "labs/shared/model/sticker"
	"time"

	"github.com/redis/go-redis/v9"
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

func (r *CacheRepository) getStickerKey(id int64) string {
	return fmt.Sprintf("sticker:%d", id)
}

func (r *CacheRepository) Get(ctx context.Context, id int64) (*stickermodel.Sticker, error) {
	key := r.getStickerKey(id)
	data, err := r.redis.Get(ctx, key).Bytes()
	if err == redis.Nil {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	var sticker stickermodel.Sticker
	if err := json.Unmarshal(data, &sticker); err != nil {
		return nil, err
	}
	return &sticker, nil
}

func (r *CacheRepository) Set(ctx context.Context, sticker *stickermodel.Sticker) error {
	key := r.getStickerKey(sticker.ID)
	data, err := json.Marshal(sticker)
	if err != nil {
		return err
	}
	return r.redis.Set(ctx, key, data, r.ttl).Err()
}

func (r *CacheRepository) Delete(ctx context.Context, id int64) error {
	return r.redis.Del(ctx, r.getStickerKey(id)).Err()
}

package noticecache

import (
	"context"
	"fmt"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/redis/go-redis/v9"
)

const noticePrefix = "notice"

type Cache struct {
	client *redis.Client
}

func New(client *redis.Client) *Cache {
	return &Cache{client: client}
}

func (c *Cache) UpdateNoticeCache(ctx context.Context, notice model.Notice) error {
	key := fmt.Sprintf("%s:%d", noticePrefix, notice.ID)

	result := c.client.HSet(ctx, key, notice)
	if err := result.Err(); err != nil {
		return err
	}

	return nil
}

func (c *Cache) GetNoticeFromCache(ctx context.Context, id int64) (model.Notice, error) {
	key := fmt.Sprintf("%s:%d", noticePrefix, id)

	result := c.client.HGetAll(ctx, key)
	if err := result.Err(); err != nil {
		return model.Notice{}, err
	}

	var notice model.Notice
	if err := result.Scan(&notice); err != nil {
		return model.Notice{}, err
	}

	return notice, nil
}

func (c *Cache) DeleteNoticeFromCache(ctx context.Context, id int64) error {
	key := fmt.Sprintf("%s:%d", noticePrefix, id)

	result := c.client.HDel(ctx, key, "id", "new_id", "content")
	if err := result.Err(); err != nil {
		return err
	}

	return nil
}

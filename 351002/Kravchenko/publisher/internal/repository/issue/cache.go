package issue

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/redis/go-redis/v9"
	issuemodel "labs/shared/model/issue"
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

func (r *CacheRepository) getIssueKey(id int64) string {
	return fmt.Sprintf("issue:%d", id)
}

func (r *CacheRepository) Get(ctx context.Context, id int64) (*issuemodel.Issue, error) {
	key := r.getIssueKey(id)

	data, err := r.redis.Get(ctx, key).Bytes()
	if err == redis.Nil {
		return nil, nil
	} else if err != nil {
		return nil, err
	}

	var issue issuemodel.Issue
	if err := json.Unmarshal(data, &issue); err != nil {
		return nil, err
	}

	return &issue, nil
}

func (r *CacheRepository) Set(ctx context.Context, issue *issuemodel.Issue) error {
	key := r.getIssueKey(issue.ID)

	data, err := json.Marshal(issue)
	if err != nil {
		return err
	}

	return r.redis.Set(ctx, key, data, r.ttl).Err()
}

func (r *CacheRepository) Delete(ctx context.Context, id int64) error {
	key := r.getIssueKey(id)
	return r.redis.Del(ctx, key).Err()
}

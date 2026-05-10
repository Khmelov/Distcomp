package note

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	notemodel "labs/shared/model/note"

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

func (r *CacheRepository) getNoteKey(id int64) string {
	return fmt.Sprintf("note:%d", id)
}

func (r *CacheRepository) getIssueNotesKey(issueID int64) string {
	return fmt.Sprintf("issue_notes:%d", issueID)
}

func (r *CacheRepository) Get(ctx context.Context, id int64) (*notemodel.Note, error) {
	key := r.getNoteKey(id)
	data, err := r.redis.Get(ctx, key).Bytes()
	if err == redis.Nil {
		return nil, nil
	}

	var note notemodel.Note
	if err := json.Unmarshal(data, &note); err != nil {
		return nil, err
	}
	return &note, nil
}

func (r *CacheRepository) Set(ctx context.Context, note *notemodel.Note) error {
	r.redis.Del(ctx, r.getIssueNotesKey(note.IssueID))

	key := r.getNoteKey(note.ID)
	data, err := json.Marshal(note)
	if err != nil {
		return err
	}
	return r.redis.Set(ctx, key, data, r.ttl).Err()
}

func (r *CacheRepository) Delete(ctx context.Context, noteID int64, issueID int64) error {
	r.redis.Del(ctx, r.getIssueNotesKey(issueID))
	return r.redis.Del(ctx, r.getNoteKey(noteID)).Err()
}

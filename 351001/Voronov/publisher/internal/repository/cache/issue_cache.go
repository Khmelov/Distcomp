package cache

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"publisher/internal/model"
	"publisher/internal/repository"

	"github.com/redis/go-redis/v9"
)

const issueTTL = 5 * time.Minute

type cachedIssueRepository struct {
	inner repository.IssueRepository
	rdb   *redis.Client
}

func NewCachedIssueRepository(inner repository.IssueRepository, rdb *redis.Client) repository.IssueRepository {
	return &cachedIssueRepository{inner: inner, rdb: rdb}
}

func issueKey(id int64) string {
	return fmt.Sprintf("issue:%d", id)
}

func (r *cachedIssueRepository) FindByID(ctx context.Context, id int64) (*model.Issue, error) {
	key := issueKey(id)
	val, err := r.rdb.Get(ctx, key).Bytes()
	if err == nil {
		var issue model.Issue
		if jsonErr := json.Unmarshal(val, &issue); jsonErr == nil {
			return &issue, nil
		}
	}
	issue, err := r.inner.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if data, marshalErr := json.Marshal(issue); marshalErr == nil {
		_ = r.rdb.Set(ctx, key, data, issueTTL).Err()
	}
	return issue, nil
}

func (r *cachedIssueRepository) FindAll(ctx context.Context, opts *repository.QueryOptions) ([]*model.Issue, int64, error) {
	return r.inner.FindAll(ctx, opts)
}

func (r *cachedIssueRepository) Create(ctx context.Context, issue *model.Issue) (*model.Issue, error) {
	return r.inner.Create(ctx, issue)
}

func (r *cachedIssueRepository) Update(ctx context.Context, id int64, issue *model.Issue) (*model.Issue, error) {
	result, err := r.inner.Update(ctx, id, issue)
	if err != nil {
		return nil, err
	}
	_ = r.rdb.Del(ctx, issueKey(id)).Err()
	return result, nil
}

func (r *cachedIssueRepository) Delete(ctx context.Context, id int64) error {
	if err := r.inner.Delete(ctx, id); err != nil {
		return err
	}
	_ = r.rdb.Del(ctx, issueKey(id)).Err()
	return nil
}

func (r *cachedIssueRepository) FindByUserID(ctx context.Context, userID int64) ([]*model.Issue, error) {
	return r.inner.FindByUserID(ctx, userID)
}

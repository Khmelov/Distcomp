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

const labelTTL = 5 * time.Minute

type cachedLabelRepository struct {
	inner repository.LabelRepository
	rdb   *redis.Client
}

func NewCachedLabelRepository(inner repository.LabelRepository, rdb *redis.Client) repository.LabelRepository {
	return &cachedLabelRepository{inner: inner, rdb: rdb}
}

func labelKey(id int64) string {
	return fmt.Sprintf("label:%d", id)
}

func (r *cachedLabelRepository) FindByID(ctx context.Context, id int64) (*model.Label, error) {
	key := labelKey(id)
	val, err := r.rdb.Get(ctx, key).Bytes()
	if err == nil {
		var label model.Label
		if jsonErr := json.Unmarshal(val, &label); jsonErr == nil {
			return &label, nil
		}
	}
	label, err := r.inner.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if data, marshalErr := json.Marshal(label); marshalErr == nil {
		_ = r.rdb.Set(ctx, key, data, labelTTL).Err()
	}
	return label, nil
}

func (r *cachedLabelRepository) FindByName(ctx context.Context, name string) (*model.Label, error) {
	return r.inner.FindByName(ctx, name)
}

func (r *cachedLabelRepository) FindAll(ctx context.Context, opts *repository.QueryOptions) ([]*model.Label, int64, error) {
	return r.inner.FindAll(ctx, opts)
}

func (r *cachedLabelRepository) Create(ctx context.Context, label *model.Label) (*model.Label, error) {
	return r.inner.Create(ctx, label)
}

func (r *cachedLabelRepository) Update(ctx context.Context, id int64, label *model.Label) (*model.Label, error) {
	result, err := r.inner.Update(ctx, id, label)
	if err != nil {
		return nil, err
	}
	_ = r.rdb.Del(ctx, labelKey(id)).Err()
	return result, nil
}

func (r *cachedLabelRepository) Delete(ctx context.Context, id int64) error {
	if err := r.inner.Delete(ctx, id); err != nil {
		return err
	}
	_ = r.rdb.Del(ctx, labelKey(id)).Err()
	return nil
}

func (r *cachedLabelRepository) FindByIssueID(ctx context.Context, issueID int64) ([]*model.Label, error) {
	return r.inner.FindByIssueID(ctx, issueID)
}

func (r *cachedLabelRepository) FindIssuesByLabelID(ctx context.Context, labelID int64) ([]int64, error) {
	return r.inner.FindIssuesByLabelID(ctx, labelID)
}

func (r *cachedLabelRepository) AddLabelToIssue(ctx context.Context, issueID, labelID int64) error {
	return r.inner.AddLabelToIssue(ctx, issueID, labelID)
}

func (r *cachedLabelRepository) RemoveLabelFromIssue(ctx context.Context, issueID, labelID int64) error {
	return r.inner.RemoveLabelFromIssue(ctx, issueID, labelID)
}

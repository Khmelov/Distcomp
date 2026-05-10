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

const userTTL = 5 * time.Minute

type cachedUserRepository struct {
	inner repository.UserRepository
	rdb   *redis.Client
}

func NewCachedUserRepository(inner repository.UserRepository, rdb *redis.Client) repository.UserRepository {
	return &cachedUserRepository{inner: inner, rdb: rdb}
}

func userKey(id int64) string {
	return fmt.Sprintf("user:%d", id)
}

func (r *cachedUserRepository) FindByID(ctx context.Context, id int64) (*model.User, error) {
	key := userKey(id)
	val, err := r.rdb.Get(ctx, key).Bytes()
	if err == nil {
		var user model.User
		if jsonErr := json.Unmarshal(val, &user); jsonErr == nil {
			return &user, nil
		}
	}
	user, err := r.inner.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if data, marshalErr := json.Marshal(user); marshalErr == nil {
		_ = r.rdb.Set(ctx, key, data, userTTL).Err()
	}
	return user, nil
}

func (r *cachedUserRepository) FindByLogin(ctx context.Context, login string) (*model.User, error) {
	return r.inner.FindByLogin(ctx, login)
}

func (r *cachedUserRepository) FindAll(ctx context.Context, opts *repository.QueryOptions) ([]*model.User, int64, error) {
	return r.inner.FindAll(ctx, opts)
}

func (r *cachedUserRepository) Create(ctx context.Context, user *model.User) (*model.User, error) {
	return r.inner.Create(ctx, user)
}

func (r *cachedUserRepository) Update(ctx context.Context, id int64, user *model.User) (*model.User, error) {
	result, err := r.inner.Update(ctx, id, user)
	if err != nil {
		return nil, err
	}
	_ = r.rdb.Del(ctx, userKey(id)).Err()
	return result, nil
}

func (r *cachedUserRepository) Delete(ctx context.Context, id int64) error {
	if err := r.inner.Delete(ctx, id); err != nil {
		return err
	}
	_ = r.rdb.Del(ctx, userKey(id)).Err()
	return nil
}

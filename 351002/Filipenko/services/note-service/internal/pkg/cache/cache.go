package cache

import (
	"context"
	"time"
)

type Cache interface {
	Get(ctx context.Context, key string, result interface{}) error
	Set(ctx context.Context, key string, value interface{}, expiration time.Duration) error
	Delete(ctx context.Context, key string) error
	Ping(ctx context.Context) error
	Close() error
}

const (
	DefaultExpiration = 10 * time.Minute
	ShortExpiration   = 5 * time.Minute
	LongExpiration    = 30 * time.Minute
)

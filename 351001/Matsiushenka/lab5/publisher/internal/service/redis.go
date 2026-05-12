package service

import (
	"context"
	"time"

	"github.com/redis/go-redis/v9"
)

var ctx = context.Background()
var Rdb *redis.Client

func InitRedis() {
	Rdb = redis.NewClient(&redis.Options{
		Addr: "localhost:6379",
	})
}

func GetCache(key string) ([]byte, error) {
	if Rdb == nil {
		return nil, redis.Nil
	}
	return Rdb.Get(ctx, key).Bytes()
}

func SetCache(key string, value []byte) {
	if Rdb == nil {
		return
	}
	// Ставим TTL 10 минут, чтобы кеш жил во время тестов
	Rdb.Set(ctx, key, value, 10*time.Minute)
}

func ClearCache(key string) {
	if Rdb == nil {
		return
	}
	Rdb.Del(ctx, key)
}

package storage

import (
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/storage/psql"
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/storage/redis"
)

type Storage struct {
	DB *psql.PSQL
}

type Cache struct {
	DB *redis.Cache
}

func New() (*Storage, error) {
	db, err := psql.New()
	if err != nil {
		return nil, err
	}

	return &Storage{
		DB: db,
	}, nil
}

func NewCache() *Cache {
	return &Cache{
		DB: redis.NewCache(),
	}
}

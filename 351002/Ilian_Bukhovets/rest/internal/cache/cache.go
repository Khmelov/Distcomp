package cache

import (
	"context"
	"encoding/json"
	"fmt"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/storage/author"
	"time"

	"github.com/go-redis/redis/v8"
)

type CacheStorage struct {
	cache      *redis.Client
	repository author.Storage
}

func NewCacheStorage(redisClient *redis.Client, repository author.Storage) *CacheStorage {
	return &CacheStorage{
		cache:      redisClient,
		repository: repository,
	}
}

func (c *CacheStorage) serializeAuthor(author *model.Author) (string, error) {
	data, err := json.Marshal(author)
	if err != nil {
		return "", fmt.Errorf("failed to serialize author: %w", err)
	}
	return string(data), nil
}

func (c *CacheStorage) deserializeAuthor(data string) (*model.Author, error) {
	var author model.Author
	err := json.Unmarshal([]byte(data), &author)
	if err != nil {
		return nil, fmt.Errorf("failed to deserialize author: %w", err)
	}
	return &author, nil
}

func (c *CacheStorage) CreateAuthor(author *model.Author) error {
	ctx := context.Background()
	c.cache.Set(ctx, "authors_list", author, time.Minute)

	return c.repository.CreateAuthor(author)
}

func (c *CacheStorage) GetAuthorByID(id int64) (*model.Author, error) {
	ctx := context.Background()
	cacheKey := fmt.Sprintf("author:%d", id)

	cachedData, err := c.cache.Get(ctx, cacheKey).Result()
	if err == nil {
		author, err := c.deserializeAuthor(cachedData)
		if err == nil {
			return author, nil
		}
	}

	author, err := c.repository.GetAuthorByID(id)
	if err != nil {
		return nil, err
	}

	serialized, err := c.serializeAuthor(author)
	if err != nil {
		return nil, err
	}
	c.cache.Set(ctx, cacheKey, serialized, 0)

	return author, nil
}

func (c *CacheStorage) GetAuthors() ([]*model.Author, error) {
	ctx := context.Background()
	cacheKey := "authors_list"

	cachedData, err := c.cache.Get(ctx, cacheKey).Result()
	if err == nil {
		var authors []*model.Author
		err := json.Unmarshal([]byte(cachedData), &authors)
		if err == nil {
			return authors, nil
		}
	}

	authors, err := c.repository.GetAuthors()
	if err != nil {
		return nil, err
	}

	serialized, err := json.Marshal(authors)
	if err != nil {
		return nil, err
	}
	c.cache.Set(ctx, cacheKey, string(serialized), 0)

	return authors, nil
}

func (c *CacheStorage) UpdateAuthor(author *model.Author) error {
	ctx := context.Background()
	cacheKey := fmt.Sprintf("author:%d", author.ID)
	c.cache.Del(ctx, cacheKey)
	c.cache.Del(ctx, "authors_list")

	return c.repository.UpdateAuthor(author)
}

func (c *CacheStorage) DeleteAuthor(id int64) error {
	ctx := context.Background()
	cacheKey := fmt.Sprintf("author:%d", id)
	c.cache.Del(ctx, cacheKey)
	c.cache.Del(ctx, "authors_list")

	return c.repository.DeleteAuthor(id)
}

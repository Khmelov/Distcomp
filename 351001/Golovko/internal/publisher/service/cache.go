package service

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"distcomp/internal/dto"
	"distcomp/internal/repository"

	"github.com/redis/go-redis/v9"
)

const ttl = 5 * time.Minute

func clearPrefix(ctx context.Context, rdb *redis.Client, prefix string) {
	keys, err := rdb.Keys(ctx, prefix+"*").Result()
	if err == nil && len(keys) > 0 {
		rdb.Del(ctx, keys...)
	}
}

// --- EDITOR CACHE ---
type editorCache struct {
	next Editor
	rdb  *redis.Client
}

func NewEditorCache(next Editor, rdb *redis.Client) Editor {
	return &editorCache{next: next, rdb: rdb}
}

func (c *editorCache) Create(ctx context.Context, req dto.EditorRequestTo) (dto.EditorResponseTo, error) {
	res, err := c.next.Create(ctx, req)
	if err == nil {
		clearPrefix(ctx, c.rdb, "editors:all:")
	}
	return res, err
}

func (c *editorCache) GetByID(ctx context.Context, id int64) (dto.EditorResponseTo, error) {
	key := fmt.Sprintf("editor:%d", id)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res dto.EditorResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}

	res, err := c.next.GetByID(ctx, id)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *editorCache) GetAll(ctx context.Context, p repository.ListParams) ([]dto.EditorResponseTo, error) {
	key := fmt.Sprintf("editors:all:%d:%d:%s:%s", p.Limit, p.Offset, p.SortBy, p.Order)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res []dto.EditorResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}

	res, err := c.next.GetAll(ctx, p)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *editorCache) Update(ctx context.Context, id int64, req dto.EditorRequestTo) (dto.EditorResponseTo, error) {
	res, err := c.next.Update(ctx, id, req)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("editor:%d", id))
		clearPrefix(ctx, c.rdb, "editors:all:")
	}
	return res, err
}

func (c *editorCache) Delete(ctx context.Context, id int64) error {
	err := c.next.Delete(ctx, id)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("editor:%d", id))
		clearPrefix(ctx, c.rdb, "editors:all:")
	}
	return err
}

// --- ARTICLE CACHE ---
type articleCache struct {
	next Article
	rdb  *redis.Client
}

func NewArticleCache(next Article, rdb *redis.Client) Article {
	return &articleCache{next: next, rdb: rdb}
}

func (c *articleCache) Create(ctx context.Context, req dto.ArticleRequestTo) (dto.ArticleResponseTo, error) {
	res, err := c.next.Create(ctx, req)
	if err == nil {
		clearPrefix(ctx, c.rdb, "articles:all:")
	}
	return res, err
}

func (c *articleCache) GetByID(ctx context.Context, id int64) (dto.ArticleResponseTo, error) {
	key := fmt.Sprintf("article:%d", id)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res dto.ArticleResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}
	res, err := c.next.GetByID(ctx, id)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *articleCache) GetAll(ctx context.Context, p repository.ListParams) ([]dto.ArticleResponseTo, error) {
	key := fmt.Sprintf("articles:all:%d:%d:%s:%s", p.Limit, p.Offset, p.SortBy, p.Order)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res []dto.ArticleResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}
	res, err := c.next.GetAll(ctx, p)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *articleCache) Update(ctx context.Context, id int64, req dto.ArticleRequestTo) (dto.ArticleResponseTo, error) {
	res, err := c.next.Update(ctx, id, req)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("article:%d", id))
		clearPrefix(ctx, c.rdb, "articles:all:")
	}
	return res, err
}

func (c *articleCache) Delete(ctx context.Context, id int64) error {
	err := c.next.Delete(ctx, id)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("article:%d", id))
		clearPrefix(ctx, c.rdb, "articles:all:")
	}
	return err
}

// --- TAG CACHE ---
type tagCache struct {
	next Tag
	rdb  *redis.Client
}

func NewTagCache(next Tag, rdb *redis.Client) Tag {
	return &tagCache{next: next, rdb: rdb}
}

func (c *tagCache) Create(ctx context.Context, req dto.TagRequestTo) (dto.TagResponseTo, error) {
	res, err := c.next.Create(ctx, req)
	if err == nil {
		clearPrefix(ctx, c.rdb, "tags:all:")
	}
	return res, err
}

func (c *tagCache) GetByID(ctx context.Context, id int64) (dto.TagResponseTo, error) {
	key := fmt.Sprintf("tag:%d", id)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res dto.TagResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}
	res, err := c.next.GetByID(ctx, id)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *tagCache) GetAll(ctx context.Context, p repository.ListParams) ([]dto.TagResponseTo, error) {
	key := fmt.Sprintf("tags:all:%d:%d:%s:%s", p.Limit, p.Offset, p.SortBy, p.Order)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res []dto.TagResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}
	res, err := c.next.GetAll(ctx, p)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *tagCache) Update(ctx context.Context, id int64, req dto.TagRequestTo) (dto.TagResponseTo, error) {
	res, err := c.next.Update(ctx, id, req)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("tag:%d", id))
		clearPrefix(ctx, c.rdb, "tags:all:")
	}
	return res, err
}

func (c *tagCache) Delete(ctx context.Context, id int64) error {
	err := c.next.Delete(ctx, id)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("tag:%d", id))
		clearPrefix(ctx, c.rdb, "tags:all:")
	}
	return err
}

// --- COMMENT CACHE ---
type commentCache struct {
	next Comment
	rdb  *redis.Client
}

func NewCommentCache(next Comment, rdb *redis.Client) Comment {
	return &commentCache{next: next, rdb: rdb}
}

func (c *commentCache) Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	res, err := c.next.Create(ctx, req)
	if err == nil {
		clearPrefix(ctx, c.rdb, "comments:all:")
	}
	return res, err
}

func (c *commentCache) GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error) {
	key := fmt.Sprintf("comment:%d", id)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res dto.CommentResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}
	res, err := c.next.GetByID(ctx, id)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *commentCache) GetAll(ctx context.Context, p repository.ListParams) ([]dto.CommentResponseTo, error) {
	key := fmt.Sprintf("comments:all:%d:%d:%s:%s", p.Limit, p.Offset, p.SortBy, p.Order)
	val, err := c.rdb.Get(ctx, key).Result()
	if err == nil {
		var res []dto.CommentResponseTo
		_ = json.Unmarshal([]byte(val), &res)
		return res, nil
	}
	res, err := c.next.GetAll(ctx, p)
	if err == nil {
		b, _ := json.Marshal(res)
		c.rdb.Set(ctx, key, b, ttl)
	}
	return res, err
}

func (c *commentCache) Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	res, err := c.next.Update(ctx, id, req)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("comment:%d", id))
		clearPrefix(ctx, c.rdb, "comments:all:")
	}
	return res, err
}

func (c *commentCache) Delete(ctx context.Context, id int64) error {
	err := c.next.Delete(ctx, id)
	if err == nil {
		c.rdb.Del(ctx, fmt.Sprintf("comment:%d", id))
		clearPrefix(ctx, c.rdb, "comments:all:")
	}
	return err
}
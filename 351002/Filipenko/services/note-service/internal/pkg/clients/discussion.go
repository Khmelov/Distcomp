package client

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"note-service/internal/entity"
	"note-service/internal/pkg/cache"
	"note-service/internal/pkg/kafka"
	"strings"
	"sync"
	"time"
)

type DiscussionClient struct {
	baseURL  string
	client   *fiber.Client
	producer *kafka.Producer
	consumer *kafka.Consumer
	cache    *cache.RedisCache
	pending  sync.Map
}

func NewDiscussionClient(producer *kafka.Producer, consumer *kafka.Consumer, cache *cache.RedisCache) *DiscussionClient {
	return &DiscussionClient{
		baseURL:  "http://127.0.0.1:24130/api/v1.0",
		client:   &fiber.Client{},
		producer: producer,
		consumer: consumer,
		cache:    cache,
	}
}

func (c *DiscussionClient) CreateComment(req entity.CommentRequestTo) (entity.CommentResponseTo, error) {
	correlationID := fmt.Sprintf("%d", time.Now().UnixNano())

	msg := entity.KafkaCommentMessage{
		ID:            req.ID,
		TopicID:       req.TopicID,
		Country:       req.Country,
		Content:       req.Content,
		State:         "PENDING",
		Operation:     "CREATE",
		CorrelationID: correlationID,
	}

	resultChan := make(chan entity.CommentResponseTo, 1)
	errorChan := make(chan error, 1)

	c.pending.Store(correlationID, struct {
		ResultChan chan entity.CommentResponseTo
		ErrorChan  chan error
	}{ResultChan: resultChan, ErrorChan: errorChan})

	ctx := context.Background()
	if err := c.producer.SendComment(ctx, msg); err != nil {
		c.pending.Delete(correlationID)
		return entity.CommentResponseTo{}, err
	}

	select {
	case result := <-resultChan:
		_ = c.cache.Set(context.Background(), cache.CacheKey("comment", result.ID), result, cache.DefaultExpiration)
		_ = c.cache.Delete(context.Background(), cache.CacheListKey("comment"))
		return result, nil
	case err := <-errorChan:
		return entity.CommentResponseTo{}, err
	case <-time.After(5 * time.Second):
		c.pending.Delete(correlationID)
		return entity.CommentResponseTo{}, fmt.Errorf("timeout waiting for comment moderation")
	}
}

func (c *DiscussionClient) GetComment(id string) (entity.CommentResponseTo, error) {
	var cached entity.CommentResponseTo
	cacheKey := cache.CacheKey("comment", id)

	if err := c.cache.Get(context.Background(), cacheKey, &cached); err == nil {
		return cached, nil
	}

	correlationID := fmt.Sprintf("get-%s-%d", id, time.Now().UnixNano())

	msg := entity.KafkaCommentMessage{
		ID:            0,
		TopicID:       0,
		Country:       "",
		Content:       id,
		State:         "",
		Operation:     "GET",
		CorrelationID: correlationID,
	}

	resultChan := make(chan entity.CommentResponseTo, 1)
	errorChan := make(chan error, 1)

	c.pending.Store(correlationID, struct {
		ResultChan chan entity.CommentResponseTo
		ErrorChan  chan error
	}{ResultChan: resultChan, ErrorChan: errorChan})

	ctx := context.Background()
	if err := c.producer.SendComment(ctx, msg); err != nil {
		c.pending.Delete(correlationID)
		return entity.CommentResponseTo{}, err
	}

	select {
	case result := <-resultChan:
		_ = c.cache.Set(context.Background(), cacheKey, result, cache.DefaultExpiration)
		return result, nil
	case err := <-errorChan:
		return entity.CommentResponseTo{}, err
	case <-time.After(5 * time.Second):
		c.pending.Delete(correlationID)
		return entity.CommentResponseTo{}, fmt.Errorf("timeout waiting for comment")
	}
}

func (c *DiscussionClient) UpdateComment(id string, req entity.CommentRequestTo) (entity.CommentResponseTo, error) {
	correlationID := fmt.Sprintf("update-%s-%d", id, time.Now().UnixNano())

	msg := entity.KafkaCommentMessage{
		ID:            req.ID,
		TopicID:       req.TopicID,
		Country:       req.Country,
		Content:       req.Content,
		State:         "PENDING",
		Operation:     "UPDATE",
		CorrelationID: correlationID,
	}

	resultChan := make(chan entity.CommentResponseTo, 1)
	errorChan := make(chan error, 1)

	c.pending.Store(correlationID, struct {
		ResultChan chan entity.CommentResponseTo
		ErrorChan  chan error
	}{ResultChan: resultChan, ErrorChan: errorChan})

	ctx := context.Background()
	if err := c.producer.SendComment(ctx, msg); err != nil {
		c.pending.Delete(correlationID)
		return entity.CommentResponseTo{}, err
	}

	select {
	case result := <-resultChan:
		_ = c.cache.Set(context.Background(), cache.CacheKey("comment", result.ID), result, cache.DefaultExpiration)
		_ = c.cache.Delete(context.Background(), cache.CacheListKey("comment"))
		return result, nil
	case err := <-errorChan:
		return entity.CommentResponseTo{}, err
	case <-time.After(5 * time.Second):
		c.pending.Delete(correlationID)
		return entity.CommentResponseTo{}, fmt.Errorf("timeout waiting for comment update")
	}
}

func (c *DiscussionClient) DeleteComment(id string) error {
	correlationID := fmt.Sprintf("delete-%s-%d", id, time.Now().UnixNano())

	msg := entity.KafkaCommentMessage{
		ID:            0,
		TopicID:       0,
		Country:       "",
		Content:       id,
		State:         "",
		Operation:     "DELETE",
		CorrelationID: correlationID,
	}

	resultChan := make(chan entity.CommentResponseTo, 1)
	errorChan := make(chan error, 1)

	c.pending.Store(correlationID, struct {
		ResultChan chan entity.CommentResponseTo
		ErrorChan  chan error
	}{ResultChan: resultChan, ErrorChan: errorChan})

	ctx := context.Background()
	if err := c.producer.SendComment(ctx, msg); err != nil {
		c.pending.Delete(correlationID)
		return err
	}

	select {
	case <-resultChan:
		_ = c.cache.Delete(context.Background(), cache.CacheKey("comment", id))
		_ = c.cache.Delete(context.Background(), cache.CacheListKey("comment"))
		return nil
	case err := <-errorChan:
		return err
	case <-time.After(5 * time.Second):
		c.pending.Delete(correlationID)
		return fmt.Errorf("timeout waiting for comment delete")
	}
}

func (c *DiscussionClient) GetAllComments() ([]entity.CommentResponseTo, error) {
	var cached []entity.CommentResponseTo
	listKey := cache.CacheListKey("comment")

	if err := c.cache.Get(context.Background(), listKey, &cached); err == nil && len(cached) > 0 {
		return cached, nil
	}

	correlationID := fmt.Sprintf("getall-%d", time.Now().UnixNano())

	msg := entity.KafkaCommentMessage{
		ID:            0,
		TopicID:       0,
		Country:       "",
		Content:       "",
		State:         "",
		Operation:     "GETALL",
		CorrelationID: correlationID,
	}

	resultChan := make(chan entity.CommentResponseTo, 1)
	errorChan := make(chan error, 1)
	var results []entity.CommentResponseTo
	var wg sync.WaitGroup
	var mu sync.Mutex

	c.pending.Store(correlationID, struct {
		ResultChan chan entity.CommentResponseTo
		ErrorChan  chan error
	}{ResultChan: resultChan, ErrorChan: errorChan})

	ctx := context.Background()
	if err := c.producer.SendComment(ctx, msg); err != nil {
		c.pending.Delete(correlationID)
		return nil, err
	}

	wg.Add(1)
	go func() {
		defer wg.Done()
		timeout := time.After(5 * time.Second)
		for {
			select {
			case result := <-resultChan:
				mu.Lock()
				results = append(results, result)
				mu.Unlock()
			case <-timeout:
				return
			}
		}
	}()

	wg.Wait()

	if len(results) == 0 {
		select {
		case err := <-errorChan:
			return nil, err
		default:
		}
	}

	_ = c.cache.Set(context.Background(), listKey, results, cache.ShortExpiration)
	c.pending.Delete(correlationID)
	return results, nil
}

func (c *DiscussionClient) HandleKafkaResponse(msg entity.KafkaCommentMessage) {
	if val, ok := c.pending.Load(msg.CorrelationID); ok {
		pending := val.(struct {
			ResultChan chan entity.CommentResponseTo
			ErrorChan  chan error
		})

		resp := entity.CommentResponseTo{
			ID:      msg.ID,
			TopicID: msg.TopicID,
			Country: msg.Country,
			Content: msg.Content,
			State:   msg.State,
		}

		if msg.State == "DECLINE" || msg.State == "ERROR" || msg.Operation == "ERROR" {
			select {
			case pending.ErrorChan <- fmt.Errorf("comment declined or error"):
			default:
			}
		} else {
			select {
			case pending.ResultChan <- resp:
			default:
			}
		}

		c.pending.Delete(msg.CorrelationID)
	}
}

func (c *DiscussionClient) Proxy(ctx *fiber.Ctx) error {
	return c.proxy(ctx, ctx.Body())
}

// ProxyWithBody forwards the request using the given body bytes (e.g. after validation middleware read a copy of the body).
func (c *DiscussionClient) ProxyWithBody(ctx *fiber.Ctx, body []byte) error {
	return c.proxy(ctx, body)
}

func (c *DiscussionClient) proxy(ctx *fiber.Ctx, body []byte) error {
	method := ctx.Method()
	path := ctx.Params("*")
	commentID := ""

	if method != "POST" && path != "" {
		parts := strings.Split(strings.TrimPrefix(path, "/"), "/")
		if len(parts) > 0 && parts[0] != "" {
			commentID = parts[0]
		}
	}

	if method == "GET" && commentID != "" {
		var cached entity.CommentResponseTo
		cacheKey := cache.CacheKey("comment", commentID)

		if err := c.cache.Get(ctx.Context(), cacheKey, &cached); err == nil && cached.ID != 0 {
			return ctx.JSON(cached)
		}
	}

	var url string
	if path == "" {
		url = fmt.Sprintf("%s/comments", c.baseURL)
	} else {
		url = fmt.Sprintf("%s/comments/%s", c.baseURL, strings.TrimPrefix(path, "/"))
	}

	a := fiber.AcquireAgent()
	defer fiber.ReleaseAgent(a)

	req := a.Request()
	req.Header.SetMethod(method)
	req.SetRequestURI(url)
	req.SetBody(body)
	req.Header.SetContentType("application/json")

	if err := a.Parse(); err != nil {
		return entity.NewErr(500, "99", "discussion service error")
	}

	statusCode, body, errs := a.Bytes()
	if len(errs) > 0 {
		return entity.NewErr(500, "99", "connection error")
	}

	if method == "GET" && statusCode == 200 && commentID != "" {
		var resp entity.CommentResponseTo
		if json.Unmarshal(body, &resp) == nil && resp.ID != 0 {
			cacheKey := cache.CacheKey("comment", commentID)
			_ = c.cache.Set(ctx.Context(), cacheKey, resp, cache.DefaultExpiration)
		}
	}

	if (method == "POST" || method == "PUT" || method == "DELETE") && statusCode < 300 {
		_ = c.cache.Delete(ctx.Context(), cache.CacheListKey("comment"))
		if commentID != "" {
			_ = c.cache.Delete(ctx.Context(), cache.CacheKey("comment", commentID))
		}
	}

	return ctx.Status(statusCode).Send(body)
}

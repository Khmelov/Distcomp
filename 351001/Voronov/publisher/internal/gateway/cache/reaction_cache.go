package cache

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"publisher/internal/gateway"
	"publisher/internal/transport/dto/request"
	"publisher/internal/transport/dto/response"

	"github.com/redis/go-redis/v9"
)

const reactionTTL = 5 * time.Minute

type cachedReactionGateway struct {
	inner gateway.ReactionGateway
	rdb   *redis.Client
}

func NewCachedReactionGateway(inner gateway.ReactionGateway, rdb *redis.Client) gateway.ReactionGateway {
	return &cachedReactionGateway{inner: inner, rdb: rdb}
}

func reactionKey(id int64) string {
	return fmt.Sprintf("reaction:%d", id)
}

func (g *cachedReactionGateway) FindByID(ctx context.Context, id int64) (*response.ReactionResponseTo, error) {
	key := reactionKey(id)
	val, err := g.rdb.Get(ctx, key).Bytes()
	if err == nil {
		var reaction response.ReactionResponseTo
		if jsonErr := json.Unmarshal(val, &reaction); jsonErr == nil {
			return &reaction, nil
		}
	}
	reaction, err := g.inner.FindByID(ctx, id)
	if err != nil {
		return nil, err
	}
	if data, marshalErr := json.Marshal(reaction); marshalErr == nil {
		_ = g.rdb.Set(ctx, key, data, reactionTTL).Err()
	}
	return reaction, nil
}

func (g *cachedReactionGateway) FindAll(ctx context.Context) ([]*response.ReactionResponseTo, error) {
	return g.inner.FindAll(ctx)
}

func (g *cachedReactionGateway) FindByIssueID(ctx context.Context, issueID int64) ([]*response.ReactionResponseTo, error) {
	return g.inner.FindByIssueID(ctx, issueID)
}

func (g *cachedReactionGateway) Create(ctx context.Context, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error) {
	return g.inner.Create(ctx, req)
}

func (g *cachedReactionGateway) Update(ctx context.Context, id int64, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error) {
	result, err := g.inner.Update(ctx, id, req)
	if err != nil {
		return nil, err
	}
	_ = g.rdb.Del(ctx, reactionKey(id)).Err()
	return result, nil
}

func (g *cachedReactionGateway) Delete(ctx context.Context, id int64) error {
	if err := g.inner.Delete(ctx, id); err != nil {
		return err
	}
	_ = g.rdb.Del(ctx, reactionKey(id)).Err()
	return nil
}

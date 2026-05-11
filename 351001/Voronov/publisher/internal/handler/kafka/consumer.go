package kafka

import (
	"context"
	"encoding/json"

	"publisher/internal/model"

	kafka "github.com/segmentio/kafka-go"
	"go.uber.org/zap"
)

func (c *ReactionConsumer) Run(ctx context.Context) {
	r := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{c.broker},
		Topic:   c.topic,
		GroupID: c.groupID,
	})
	defer r.Close()

	c.logger.Info("kafka consumer started", zap.String("topic", c.topic))

	for {
		msg, err := r.ReadMessage(ctx)
		if err != nil {
			if ctx.Err() != nil {
				c.logger.Info("kafka consumer stopped")
				return
			}
			c.logger.Error("read message failed", zap.Error(err))
			continue
		}

		var reaction model.Reaction
		if err := json.Unmarshal(msg.Value, &reaction); err != nil {
			c.logger.Error("unmarshal reaction failed",
				zap.Error(err),
				zap.ByteString("raw", msg.Value),
			)
			continue
		}

		if err := c.repo.UpdateState(ctx, reaction.ID, reaction.State); err != nil {
			c.logger.Error("update state failed",
				zap.Error(err),
				zap.Int64("id", reaction.ID),
				zap.String("state", string(reaction.State)),
			)
		}
	}
}

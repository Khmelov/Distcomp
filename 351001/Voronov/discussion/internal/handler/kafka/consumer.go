package kafka

import (
	"context"
	"encoding/json"

	"discussion/internal/model"

	kafka "github.com/segmentio/kafka-go"
	"go.uber.org/zap"
)

// Run polls InTopic in a loop until ctx is cancelled.
// For each message: deserialise → moderate → persist → publish to OutTopic.
func (c *ReactionConsumer) Run(ctx context.Context) {
	r := kafka.NewReader(kafka.ReaderConfig{
		Brokers: []string{c.broker},
		Topic:   c.topic,
		GroupID: c.groupID,
	})
	defer r.Close()

	c.logger.Info("discussion kafka consumer started", zap.String("topic", c.topic))

	for {
		msg, err := r.ReadMessage(ctx)
		if err != nil {
			if ctx.Err() != nil {
				c.logger.Info("discussion kafka consumer stopped")
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

		moderated, err := c.svc.Moderate(ctx, &reaction)
		if err != nil {
			c.logger.Error("moderation failed",
				zap.Error(err),
				zap.Int64("id", reaction.ID),
			)
			continue
		}

		if err := c.producer.Publish(ctx, moderated); err != nil {
			c.logger.Error("publish to OutTopic failed",
				zap.Error(err),
				zap.Int64("id", moderated.ID),
				zap.String("state", string(moderated.State)),
			)
		}
	}
}

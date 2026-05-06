package kafka

import (
	"context"
	"encoding/json"
	"fmt"

	"discussion/internal/model"

	kafka "github.com/segmentio/kafka-go"
	"go.uber.org/zap"
)

type kafkaProducer struct {
	writer         *kafka.Writer
	partitionCount int
	logger         *zap.Logger
}

// NewReactionProducer creates a producer that writes to the given topic.
func NewReactionProducer(broker, topic string, partitionCount int, logger *zap.Logger) ReactionProducer {
	w := &kafka.Writer{
		Addr:  kafka.TCP(broker),
		Topic: topic,
	}
	return &kafkaProducer{writer: w, partitionCount: partitionCount, logger: logger}
}

func computePartition(issueID int64, partitionCount int) int {
	if partitionCount <= 0 {
		return 0
	}
	return int(issueID % int64(partitionCount))
}

func (p *kafkaProducer) Publish(ctx context.Context, r *model.Reaction) error {
	payload, err := json.Marshal(r)
	if err != nil {
		return fmt.Errorf("marshal reaction: %w", err)
	}

	partition := computePartition(r.IssueID, p.partitionCount)
	msg := kafka.Message{
		Partition: partition,
		Value:     payload,
	}

	if err := p.writer.WriteMessages(ctx, msg); err != nil {
		p.logger.Error("kafka publish failed",
			zap.Error(err),
			zap.Int64("reactionId", r.ID),
			zap.Int("partition", partition),
		)
		return err
	}
	return nil
}

func (p *kafkaProducer) Close() error {
	return p.writer.Close()
}

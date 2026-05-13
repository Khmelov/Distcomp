package kafka

import (
	"context"
	"encoding/json"
	"go.uber.org/zap"
	"note-service/internal/entity"
	"time"

	"github.com/segmentio/kafka-go"
)

type Consumer struct {
	reader *kafka.Reader
	log    *zap.Logger
	config Config
}

type CommentResult struct {
	CorrelationID string
	Comment       entity.CommentResponseTo
	Error         error
}

type ConsumerHandlers struct {
	OnCommentResult func(entity.KafkaCommentMessage)
}

func NewConsumer(cfg Config, log *zap.Logger) *Consumer {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers:     cfg.Brokers,
		Topic:       cfg.OutTopic,
		GroupID:     "publisher-group",
		MinBytes:    10e3,
		MaxBytes:    10e6,
		StartOffset: kafka.LastOffset,
	})

	return &Consumer{
		reader: reader,
		log:    log,
		config: cfg,
	}
}

func (c *Consumer) Start(ctx context.Context, handlers ConsumerHandlers) {
	go func() {
		for {
			select {
			case <-ctx.Done():
				return
			default:
				msg, err := c.reader.FetchMessage(ctx)
				if err != nil {
					c.log.Error("Failed to fetch message", zap.Error(err))
					time.Sleep(100 * time.Millisecond)
					continue
				}

				var kafkaMsg entity.KafkaCommentMessage
				if err := json.Unmarshal(msg.Value, &kafkaMsg); err != nil {
					c.log.Error("Failed to unmarshal message", zap.Error(err))
					continue
				}

				if handlers.OnCommentResult != nil {
					handlers.OnCommentResult(kafkaMsg)
				}

				if err := c.reader.CommitMessages(ctx, msg); err != nil {
					c.log.Error("Failed to commit message", zap.Error(err))
				}
			}
		}
	}()
}

func (c *Consumer) Close() error {
	return c.reader.Close()
}

package kafka

import (
	"context"
	"discussion-service/internal/entity"
	"encoding/json"
	"fmt"
	"go.uber.org/zap"
	"time"

	"github.com/segmentio/kafka-go"
)

type Producer struct {
	writer *kafka.Writer
	log    *zap.Logger
	cfg    Config
}

type Config struct {
	Brokers  []string
	InTopic  string
	OutTopic string
}

func NewProducer(cfg Config, log *zap.Logger) *Producer {
	writer := &kafka.Writer{
		Addr:         kafka.TCP(cfg.Brokers...),
		Topic:        cfg.OutTopic,
		Balancer:     &kafka.Hash{},
		BatchTimeout: 10 * time.Millisecond,
	}

	return &Producer{
		writer: writer,
		log:    log,
		cfg:    cfg,
	}
}

func (p *Producer) SendComment(ctx context.Context, msg entity.KafkaCommentMessage) error {
	data, err := json.Marshal(msg)
	if err != nil {
		return err
	}

	kafkaMsg := kafka.Message{
		Key:   []byte(fmt.Sprintf("%d", msg.TopicID)),
		Value: data,
	}

	return p.writer.WriteMessages(ctx, kafkaMsg)
}

func (p *Producer) Close() error {
	return p.writer.Close()
}

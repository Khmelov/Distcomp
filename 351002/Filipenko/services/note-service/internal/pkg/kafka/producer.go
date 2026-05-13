package kafka

import (
	"context"
	"encoding/json"
	"fmt"
	"github.com/segmentio/kafka-go"
	"go.uber.org/zap"
	"note-service/internal/entity"
	_ "sync"
	"time"
)

type Producer struct {
	writer *kafka.Writer
	log    *zap.Logger
	config Config
}

type Config struct {
	Brokers  []string
	InTopic  string
	OutTopic string
}

func NewProducer(cfg Config, log *zap.Logger) *Producer {
	writer := &kafka.Writer{
		Addr:         kafka.TCP(cfg.Brokers...),
		Topic:        cfg.InTopic,
		Balancer:     &kafka.Hash{},
		BatchTimeout: 10 * time.Millisecond,
	}

	return &Producer{
		writer: writer,
		log:    log,
		config: cfg,
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

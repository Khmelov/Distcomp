package kafka

import (
	"context"
	"encoding/json"
	"fmt"
	"log"

	kafkamodel "labs/shared/dto/kafka"

	"github.com/segmentio/kafka-go"
)

type ReplyRepository interface {
	SendReply(ctx context.Context, response *kafkamodel.KafkaResponse) error
}

type replyRepositoryImpl struct {
	writer *kafka.Writer
}

func NewReplyRepository(brokers []string) ReplyRepository {
	writer := &kafka.Writer{
		Addr:     kafka.TCP(brokers...),
		Topic:    "OutTopic",
		Balancer: &kafka.LeastBytes{},
	}
	return &replyRepositoryImpl{writer: writer}
}

func (r *replyRepositoryImpl) SendReply(ctx context.Context, response *kafkamodel.KafkaResponse) error {
	if response.CorrelationID == "" {
		return nil
	}

	payload, err := json.Marshal(response)
	if err != nil {
		return fmt.Errorf("failed to marshal kafka response: %w", err)
	}

	err = r.writer.WriteMessages(ctx, kafka.Message{
		Value: payload,
	})
	if err != nil {
		log.Printf("Failed to send reply to OutTopic: %v", err)
		return err
	}

	return nil
}

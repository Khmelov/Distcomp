package service

import (
	"context"
	"log"
	"strings"

	"github.com/segmentio/kafka-go"
)

type KafkaProducer struct {
	writer *kafka.Writer
}

func NewKafkaProducer(brokers string) (*KafkaProducer, error) {
	w := &kafka.Writer{
		Addr:         kafka.TCP(strings.Split(brokers, ",")...),
		Balancer:     &kafka.LeastBytes{},
		RequiredAcks: kafka.RequireAll,
		Async:        false,
	}

	log.Println("Connected to Kafka at", brokers)
	return &KafkaProducer{writer: w}, nil
}

func (kp *KafkaProducer) PublishNoticeEvent(ctx context.Context, topic string, event *NoticeEvent) error {
	data, err := event.Marshal()
	if err != nil {
		return err
	}

	msg := kafka.Message{
		Topic: topic,
		Key:   []byte(event.CorrelationID),
		Value: data,
	}

	return kp.writer.WriteMessages(ctx, msg)
}

func (kp *KafkaProducer) Close() error {
	return kp.writer.Close()
}

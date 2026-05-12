package service

import (
	"context"
	"log"
	"strings"
	"time"

	"github.com/segmentio/kafka-go"
)

type KafkaProducer struct {
	writer *kafka.Writer
}

func NewKafkaProducer(brokers string) (*KafkaProducer, error) {
	brokerList := strings.Split(brokers, ",")
	w := &kafka.Writer{
		Addr:         kafka.TCP(brokerList...),
		Balancer:     &kafka.LeastBytes{},
		RequiredAcks: kafka.RequireAll,
		Async:        false,
		BatchTimeout: 10 * time.Millisecond,
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
	if kp.writer != nil {
		return kp.writer.Close()
	}
	return nil
}

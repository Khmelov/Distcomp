package service

import (
	"context"
	"log"
	"strings"
	"time"

	"github.com/segmentio/kafka-go"
)

type KafkaConsumer struct {
	reader        *kafka.Reader
	noticeService *NoticeService
	producer      *KafkaProducer
}

func NewKafkaConsumer(brokers, groupID string, svc *NoticeService, prod *KafkaProducer) (*KafkaConsumer, error) {
	return &KafkaConsumer{
		reader: kafka.NewReader(kafka.ReaderConfig{
			Brokers:     strings.Split(brokers, ","),
			Topic:       InTopic,
			GroupID:     groupID,
			StartOffset: kafka.FirstOffset,
			MaxWait:     10 * time.Millisecond,
		}),
		noticeService: svc,
		producer:      prod,
	}, nil
}

func (kc *KafkaConsumer) Start(ctx context.Context) {
	go func() {
		for {
			msg, err := kc.reader.ReadMessage(ctx)
			if err != nil {
				if ctx.Err() != nil {
					return
				}
				continue
			}
			event, err := UnmarshalNoticeEvent(msg.Value)
			if err == nil {
				log.Printf("[DISCUSSION] Received event %s (corrID: %s)", event.Type, event.CorrelationID)
				kc.noticeService.HandleAsyncRequest(ctx, event, kc.producer)
			}
		}
	}()
}

func (kc *KafkaConsumer) Close() error {
	return kc.reader.Close()
}

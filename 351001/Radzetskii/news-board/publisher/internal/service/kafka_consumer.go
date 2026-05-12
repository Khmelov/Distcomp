package service

import (
	"context"
	"strings"
	"time"

	"github.com/segmentio/kafka-go"
)

type KafkaConsumer struct {
	reader        *kafka.Reader
	noticeService *NoticeService
}

func NewKafkaConsumer(brokers string, groupID string, topic string, noticeService *NoticeService) *KafkaConsumer {
	return &KafkaConsumer{
		reader: kafka.NewReader(kafka.ReaderConfig{
			Brokers: strings.Split(brokers, ","),
			Topic:   topic,
			GroupID: groupID,
			MaxWait: 10 * time.Millisecond,
		}),
		noticeService: noticeService,
	}
}

func (kc *KafkaConsumer) StartReplyListener(ctx context.Context) {
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
				kc.noticeService.MatchResponse(event)
			}
		}
	}()
}

func (kc *KafkaConsumer) Close() error {
	return kc.reader.Close()
}

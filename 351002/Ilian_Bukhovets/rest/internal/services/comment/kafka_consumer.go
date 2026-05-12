package comment

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"time"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

type KafkaConsumer struct {
	*kafka.Consumer
}

type Response struct {
	RequestID int    `json:"request_id"`
	Status    string `json:"status"`
	Data      string `json:"data"`
}

func NewKafkaConsumer(topic string) *KafkaConsumer {
	config := &kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
		"group.id":          "publisher-group",
		"auto.offset.reset": "earliest",
	}

	consumer, err := kafka.NewConsumer(config)
	if err != nil {
		log.Fatalf("Failed to create Kafka consumer: %v", err)
	}

	consumer.SubscribeTopics([]string{topic}, nil)
	return &KafkaConsumer{
		consumer,
	}
}

func (c *KafkaConsumer) WaitForResponse(ctx context.Context, requestID int) (*Response, error) {
	for {
		select {
		case <-ctx.Done():
			return nil, fmt.Errorf("timeout waiting for response")
		default:
			msg, err := c.ReadMessage(100 * time.Millisecond)
			if err != nil {
				continue
			}

			var response Response
			err = json.Unmarshal(msg.Value, &response)
			if err != nil {
				log.Printf("Failed to unmarshal response: %v", err)
				continue
			}

			if response.RequestID == requestID {
				return &response, nil
			}
		}
	}
}

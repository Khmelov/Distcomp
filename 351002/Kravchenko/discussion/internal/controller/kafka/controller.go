package kafka

import (
	"context"
	"encoding/json"
	kafkamodel "labs/shared/dto/kafka"
	"log"

	kafkaservice "labs/discussion/internal/service/kafka"

	"github.com/segmentio/kafka-go"
)

type ConsumerController struct {
	reader  *kafka.Reader
	service kafkaservice.Service
}

func NewConsumerController(brokers []string, service kafkaservice.Service) *ConsumerController {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		Topic:   "InTopic",
		GroupID: "discussion-group", // Consumer group обязательна
	})

	return &ConsumerController{
		reader:  reader,
		service: service,
	}
}

func (c *ConsumerController) Start(ctx context.Context) {
	log.Println("Starting Kafka Consumer Controller in discussion service...")
	for {
		select {
		case <-ctx.Done():
			return
		default:
			msg, err := c.reader.ReadMessage(ctx)
			if err != nil {
				log.Printf("Error reading message from InTopic: %v", err)
				continue
			}

			var req kafkamodel.KafkaMessage
			if err := json.Unmarshal(msg.Value, &req); err != nil {
				log.Printf("Error unmarshaling Kafka message: %v", err)
				continue
			}

			go c.service.ProcessMessage(ctx, &req)
		}

	}
}

package comment

import (
	"encoding/json"
	"fmt"
	"log"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

type KafkaProducer interface {
	SendMessage(topic string, key int, comment interface{}) error
}

type kafkaProducer struct {
	producer *kafka.Producer
}

func NewKafkaProducer() KafkaProducer {
	config := &kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
	}

	producer, err := kafka.NewProducer(config)
	if err != nil {
		log.Fatalf("Failed to create Kafka producer: %v", err)
	}

	return &kafkaProducer{
		producer: producer,
	}
}

func (p *kafkaProducer) SendMessage(topic string, key int, msg interface{}) error {
	value, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("failed to marshal comment: %v", err)
	}

	message := &kafka.Message{
		TopicPartition: kafka.TopicPartition{
			Topic:     &topic,
			Partition: kafka.PartitionAny,
		},
		Key:   []byte(fmt.Sprintf("%d", key)),
		Value: value,
	}

	return p.producer.Produce(message, nil)
}

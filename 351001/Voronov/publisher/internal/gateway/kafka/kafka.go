package kafka

import (
	"context"
	"errors"
	"fmt"

	"publisher/internal/model"

	kafka "github.com/segmentio/kafka-go"
)

type ReactionProducer interface {
	Publish(ctx context.Context, r *model.Reaction) error
	Close() error
}

func EnsureTopic(broker, topic string, numPartitions, replicationFactor int) error {
	conn, err := kafka.Dial("tcp", broker)
	if err != nil {
		return fmt.Errorf("dial kafka: %w", err)
	}
	defer conn.Close()

	controller, err := conn.Controller()
	if err != nil {
		return fmt.Errorf("get controller: %w", err)
	}

	controllerConn, err := kafka.Dial("tcp", fmt.Sprintf("%s:%d", controller.Host, controller.Port))
	if err != nil {
		return fmt.Errorf("dial controller: %w", err)
	}
	defer controllerConn.Close()

	err = controllerConn.CreateTopics(kafka.TopicConfig{
		Topic:             topic,
		NumPartitions:     numPartitions,
		ReplicationFactor: replicationFactor,
	})
	if err != nil && !errors.Is(err, kafka.TopicAlreadyExists) {
		return fmt.Errorf("create topic %s: %w", topic, err)
	}
	return nil
}

func LookupPartitionCount(broker, topic string) (int, error) {
	conn, err := kafka.Dial("tcp", broker)
	if err != nil {
		return 0, fmt.Errorf("dial kafka: %w", err)
	}
	defer conn.Close()

	partitions, err := conn.ReadPartitions(topic)
	if err != nil {
		return 0, fmt.Errorf("read partitions: %w", err)
	}
	return len(partitions), nil
}

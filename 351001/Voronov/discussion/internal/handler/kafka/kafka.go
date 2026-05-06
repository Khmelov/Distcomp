package kafka

import (
	kafkagateway "discussion/internal/gateway/kafka"
	"discussion/internal/service"

	"go.uber.org/zap"
)

// ReactionConsumer polls InTopic, moderates reactions and publishes results to OutTopic.
type ReactionConsumer struct {
	svc      service.ReactionService
	producer kafkagateway.ReactionProducer
	broker   string
	topic    string
	groupID  string
	logger   *zap.Logger
}

func NewReactionConsumer(
	svc service.ReactionService,
	producer kafkagateway.ReactionProducer,
	broker, topic, groupID string,
	logger *zap.Logger,
) *ReactionConsumer {
	return &ReactionConsumer{
		svc:      svc,
		producer: producer,
		broker:   broker,
		topic:    topic,
		groupID:  groupID,
		logger:   logger,
	}
}

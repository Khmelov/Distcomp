package kafka

import (
	"publisher/internal/repository"

	"go.uber.org/zap"
)

type ReactionConsumer struct {
	repo    repository.ReactionRepository
	broker  string
	topic   string
	groupID string
	logger  *zap.Logger
}

func NewReactionConsumer(
	repo repository.ReactionRepository,
	broker, topic, groupID string,
	logger *zap.Logger,
) *ReactionConsumer {
	return &ReactionConsumer{
		repo:    repo,
		broker:  broker,
		topic:   topic,
		groupID: groupID,
		logger:  logger,
	}
}

package kafka

import (
	"context"
	"discussion-service/internal/entity"
	"discussion-service/internal/usecase"
	"encoding/json"
	"strconv"

	"github.com/segmentio/kafka-go"
	"go.uber.org/zap"
	"time"
)

type Consumer struct {
	reader   *kafka.Reader
	log      *zap.Logger
	config   Config
	uc       *usecase.CommentUsecase
	producer *Producer
}

func NewConsumer(cfg Config, log *zap.Logger, uc *usecase.CommentUsecase, producer *Producer) *Consumer {
	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers:     cfg.Brokers,
		Topic:       cfg.InTopic,
		GroupID:     "discussion-group",
		MinBytes:    10e3,
		MaxBytes:    10e6,
		StartOffset: kafka.FirstOffset,
	})

	return &Consumer{
		reader:   reader,
		log:      log,
		config:   cfg,
		uc:       uc,
		producer: producer,
	}
}

func (c *Consumer) Start(ctx context.Context) {
	go func() {
		for {
			select {
			case <-ctx.Done():
				return
			default:
				msg, err := c.reader.FetchMessage(ctx)
				if err != nil {
					c.log.Error("Failed to fetch message", zap.Error(err))
					time.Sleep(100 * time.Millisecond)
					continue
				}

				var kafkaMsg entity.KafkaCommentMessage
				if err := json.Unmarshal(msg.Value, &kafkaMsg); err != nil {
					c.log.Error("Failed to unmarshal message", zap.Error(err))
					continue
				}

				c.processMessage(ctx, kafkaMsg)

				if err := c.reader.CommitMessages(ctx, msg); err != nil {
					c.log.Error("Failed to commit message", zap.Error(err))
				}
			}
		}
	}()
}

func (c *Consumer) processMessage(ctx context.Context, msg entity.KafkaCommentMessage) {
	switch msg.Operation {
	case "CREATE":
		c.handleCreate(ctx, msg)
	case "GET":
		c.handleGet(ctx, msg)
	case "UPDATE":
		c.handleUpdate(ctx, msg)
	case "DELETE":
		c.handleDelete(ctx, msg)
	case "GETALL":
		c.handleGetAll(ctx, msg)
	}
}

func (c *Consumer) handleCreate(ctx context.Context, msg entity.KafkaCommentMessage) {
	req := entity.CommentRequestTo{
		ID:      msg.ID,
		TopicID: msg.TopicID,
		Country: msg.Country,
		Content: msg.Content,
	}

	comment, err := c.uc.Create(ctx, req)
	state := "APPROVE"
	if err != nil {
		state = "DECLINE"
	}

	responseMsg := entity.KafkaCommentMessage{
		ID:            comment.ID,
		TopicID:       comment.TopicID,
		Country:       comment.Country,
		Content:       comment.Content,
		State:         state,
		Operation:     "CREATE_RESPONSE",
		CorrelationID: msg.CorrelationID,
	}

	if err := c.producer.SendComment(ctx, responseMsg); err != nil {
		c.log.Error("Failed to send response", zap.Error(err))
	}
}

func (c *Consumer) handleGet(ctx context.Context, msg entity.KafkaCommentMessage) {
	id, parseErr := strconv.ParseInt(msg.Content, 10, 64)
	if parseErr != nil {
		responseMsg := entity.KafkaCommentMessage{
			State:         "ERROR",
			Operation:     "GET_RESPONSE",
			CorrelationID: msg.CorrelationID,
		}
		if err := c.producer.SendComment(ctx, responseMsg); err != nil {
			c.log.Error("Failed to send response", zap.Error(err))
		}
		return
	}
	comment, err := c.uc.GetByID(ctx, id)

	state := "APPROVE"
	if err != nil {
		state = "ERROR"
	}

	responseMsg := entity.KafkaCommentMessage{
		ID:            comment.ID,
		TopicID:       comment.TopicID,
		Country:       comment.Country,
		Content:       comment.Content,
		State:         state,
		Operation:     "GET_RESPONSE",
		CorrelationID: msg.CorrelationID,
	}

	if err := c.producer.SendComment(ctx, responseMsg); err != nil {
		c.log.Error("Failed to send response", zap.Error(err))
	}
}

func (c *Consumer) handleUpdate(ctx context.Context, msg entity.KafkaCommentMessage) {
	req := entity.CommentRequestTo{
		ID:      msg.ID,
		TopicID: msg.TopicID,
		Country: msg.Country,
		Content: msg.Content,
	}

	comment, err := c.uc.Update(ctx, msg.ID, req)
	state := "APPROVE"
	if err != nil {
		state = "DECLINE"
	}

	responseMsg := entity.KafkaCommentMessage{
		ID:            comment.ID,
		TopicID:       comment.TopicID,
		Country:       comment.Country,
		Content:       comment.Content,
		State:         state,
		Operation:     "UPDATE_RESPONSE",
		CorrelationID: msg.CorrelationID,
	}

	if err := c.producer.SendComment(ctx, responseMsg); err != nil {
		c.log.Error("Failed to send response", zap.Error(err))
	}
}

func (c *Consumer) handleDelete(ctx context.Context, msg entity.KafkaCommentMessage) {
	id, parseErr := strconv.ParseInt(msg.Content, 10, 64)
	if parseErr != nil {
		responseMsg := entity.KafkaCommentMessage{
			State:         "ERROR",
			Operation:     "DELETE_RESPONSE",
			CorrelationID: msg.CorrelationID,
		}
		if err := c.producer.SendComment(ctx, responseMsg); err != nil {
			c.log.Error("Failed to send response", zap.Error(err))
		}
		return
	}
	err := c.uc.Delete(ctx, id)
	state := "APPROVE"
	if err != nil {
		state = "ERROR"
	}

	responseMsg := entity.KafkaCommentMessage{
		ID:            0,
		TopicID:       0,
		Country:       "",
		Content:       "",
		State:         state,
		Operation:     "DELETE_RESPONSE",
		CorrelationID: msg.CorrelationID,
	}

	if err := c.producer.SendComment(ctx, responseMsg); err != nil {
		c.log.Error("Failed to send response", zap.Error(err))
	}
}

func (c *Consumer) handleGetAll(ctx context.Context, msg entity.KafkaCommentMessage) {
	comments, err := c.uc.GetAll(ctx)

	for _, comment := range comments {
		responseMsg := entity.KafkaCommentMessage{
			ID:            comment.ID,
			TopicID:       comment.TopicID,
			Country:       comment.Country,
			Content:       comment.Content,
			State:         "APPROVE",
			Operation:     "GETALL_RESPONSE",
			CorrelationID: msg.CorrelationID,
		}

		if err := c.producer.SendComment(ctx, responseMsg); err != nil {
			c.log.Error("Failed to send response", zap.Error(err))
		}
	}

	if err != nil {
		responseMsg := entity.KafkaCommentMessage{
			ID:            0,
			TopicID:       0,
			Country:       "",
			Content:       "",
			State:         "ERROR",
			Operation:     "GETALL_RESPONSE",
			CorrelationID: msg.CorrelationID,
		}

		if err := c.producer.SendComment(ctx, responseMsg); err != nil {
			c.log.Error("Failed to send error response", zap.Error(err))
		}
	}
}

func (c *Consumer) Close() error {
	return c.reader.Close()
}

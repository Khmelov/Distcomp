package message

import (
	"context"
	"log"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/mapper"
	messageModel "github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/model"
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/publisher/internal/storage"
)

type httpClient interface {
	CreateMessage(ctx context.Context, issueID int64, content string) (*messageModel.Message, error)
	GetMessages(ctx context.Context) ([]messageModel.Message, error)
	GetMessage(ctx context.Context, id int64) (*messageModel.Message, error)
	UpdateMessage(ctx context.Context, id, issueID int64, content string) (*messageModel.Message, error)
	DeleteMessage(ctx context.Context, id int64) error
}

type service struct {
	client httpClient
	cache  storage.Cache
}

type MessageService interface {
	CreateMessage(ctx context.Context, message messageModel.Message) (messageModel.Message, error)
	GetMessages(ctx context.Context) ([]messageModel.Message, error)
	GetMessageByID(ctx context.Context, id int64) (messageModel.Message, error)
	UpdateMessageByID(ctx context.Context, message messageModel.Message) (messageModel.Message, error)
	DeleteMessageByID(ctx context.Context, id int64) error
}

func New(client httpClient, cache storage.Cache) MessageService {
	return &service{
		cache:  cache,
		client: client,
	}
}

func (s *service) CreateMessage(ctx context.Context, message messageModel.Message) (messageModel.Message, error) {
	createdMsg, err := s.client.CreateMessage(ctx, int64(message.IssueID), message.Content)
	if err != nil {
		return messageModel.Message{}, err
	}

	log.Println(createdMsg)

	return mapper.MapHTTPMessageToModel(*createdMsg), nil
}

func (s *service) GetMessages(ctx context.Context) ([]messageModel.Message, error) {
	cachedMsgs, err := s.cache.DB.GetAllMessages(ctx)
	if err == nil && len(cachedMsgs) > 0 {
		return cachedMsgs, nil
	}

	msgs, err := s.client.GetMessages(ctx)
	if err != nil {
		return nil, err
	}

	var mappedMessages []messageModel.Message
	for _, msg := range msgs {
		mappedMsg := mapper.MapHTTPMessageToModel(msg)
		mappedMessages = append(mappedMessages, mappedMsg)

		if err := s.cache.DB.SaveMessage(ctx, &mappedMsg, 24*time.Hour); err != nil {
			log.Printf("failed to cache message %d: %v", mappedMsg.ID, err)
		}
	}

	if len(mappedMessages) == 0 {
		return []messageModel.Message{}, nil
	}

	return mappedMessages, nil
}

func (s *service) GetMessageByID(ctx context.Context, id int64) (messageModel.Message, error) {
	cachedMsg, err := s.cache.DB.GetMessage(ctx, int(id))
	if err == nil && cachedMsg != nil {
		return *cachedMsg, nil
	}

	msg, err := s.client.GetMessage(ctx, id)
	if err != nil {
		return messageModel.Message{}, err
	}

	mappedMsg := mapper.MapHTTPMessageToModel(*msg)

	if err := s.cache.DB.SaveMessage(ctx, &mappedMsg, 24*time.Hour); err != nil {
		log.Printf("failed to cache message %d: %v", id, err)
	}

	return mappedMsg, nil
}

func (s *service) UpdateMessageByID(ctx context.Context, message messageModel.Message) (messageModel.Message, error) {
	updatedMsg, err := s.client.UpdateMessage(ctx, int64(message.ID), int64(message.IssueID), message.Content)
	if err != nil {
		return messageModel.Message{}, err
	}

	mappedMsg := mapper.MapHTTPMessageToModel(*updatedMsg)

	if err := s.cache.DB.SaveMessage(ctx, &mappedMsg, 24*time.Hour); err != nil {
		log.Printf("failed to update cached message %d: %v", mappedMsg.ID, err)
	}

	return mappedMsg, nil
}

func (s *service) DeleteMessageByID(ctx context.Context, id int64) error {
	if err := s.client.DeleteMessage(ctx, id); err != nil {
		return err
	}

	if err := s.cache.DB.DeleteMessage(ctx, int(id)); err != nil {
		log.Printf("failed to delete cached message %d: %v", id, err)
	}

	return nil
}

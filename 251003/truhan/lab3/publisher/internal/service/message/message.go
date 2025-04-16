package message

import (
	"context"
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/mapper"
	messageModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
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
}

type MessageService interface {
	CreateMessage(ctx context.Context, message messageModel.Message) (messageModel.Message, error)
	GetMessages(ctx context.Context) ([]messageModel.Message, error)
	GetMessageByID(ctx context.Context, id int64) (messageModel.Message, error)
	UpdateMessageByID(ctx context.Context, message messageModel.Message) (messageModel.Message, error)
	DeleteMessageByID(ctx context.Context, id int64) error
}

func New(client httpClient) MessageService {
	return &service{
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
	var mappedMessages []messageModel.Message

	msgs, err := s.client.GetMessages(ctx)
	if err != nil {
		return mappedMessages, err
	}

	for _, msg := range msgs {
		mappedMessages = append(mappedMessages, mapper.MapHTTPMessageToModel(msg))
	}

	if len(mappedMessages) == 0 {
		return []messageModel.Message{}, nil
	}

	return mappedMessages, nil
}

func (s *service) GetMessageByID(ctx context.Context, id int64) (messageModel.Message, error) {
	msg, err := s.client.GetMessage(ctx, id)
	if err != nil {
		return messageModel.Message{}, err
	}

	return mapper.MapHTTPMessageToModel(*msg), nil
}

func (s *service) UpdateMessageByID(ctx context.Context, message messageModel.Message) (messageModel.Message, error) {
	updatedMsg, err := s.client.UpdateMessage(ctx, int64(message.ID), int64(message.IssueID), message.Content)
	if err != nil {
		return messageModel.Message{}, err
	}

	return mapper.MapHTTPMessageToModel(*updatedMsg), nil
}

func (s *service) DeleteMessageByID(ctx context.Context, id int64) error {
	return s.client.DeleteMessage(ctx, id)
}

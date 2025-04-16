package service

import (
	"context"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/internal/model"
)

type Service interface {
	MessageService
}

type MessageService interface {
	GetMessage(ctx context.Context, id int64) (model.Message, error)
	GetMessages(ctx context.Context) ([]model.Message, error)
	CreateMessage(ctx context.Context, args model.Message) (model.Message, error)
	UpdateMessage(ctx context.Context, args model.Message) (model.Message, error)
	DeleteMessage(ctx context.Context, id int64) error
}

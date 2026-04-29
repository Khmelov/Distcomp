package storage

import (
	"context"

	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/discussion/internal/model"
)

type Repository interface {
	NoticeRepo

	Close()
}

type NoticeRepo interface {
	GetMessage(ctx context.Context, id int64) (model.Message, error)
	GetMessages(ctx context.Context) ([]model.Message, error)
	CreateMessage(ctx context.Context, args model.Message) (model.Message, error)
	UpdateMessage(ctx context.Context, args model.Message) (model.Message, error)
	DeleteMessage(ctx context.Context, id int64) error
}

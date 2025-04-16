package service

import (
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/api/discussion"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service/creator"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service/issue"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service/mark"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service/message"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage"
)

type Service struct {
	db storage.Storage

	Creator CreatorService
	Issue   IssueService
	Mark    MarkService
	Message MessageService
}

func New(db storage.Storage) Service {
	return Service{
		db: db,

		Creator: creator.New(db.DB.CreatorInst),
		Issue:   issue.New(db.DB.IssueInst),
		Mark:    mark.New(db.DB.MarkInst),
		Message: message.New(discussion.NewClient()),
	}
}

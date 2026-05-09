package service

import (
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/api/discussion"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/service/creator"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/service/issue"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/service/mark"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/service/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/storage"
)

type Service struct {
	db storage.Storage

	Creator CreatorService
	Issue   IssueService
	Mark    MarkService
	github.com/Khmelov/Distcomp/351001/Ushakov    github.com/Khmelov/Distcomp/351001/UshakovService
}

func New(db storage.Storage) Service {
	return Service{
		db: db,

		Creator: creator.New(db.DB.CreatorInst),
		Issue:   issue.New(db.DB.IssueInst),
		Mark:    mark.New(db.DB.MarkInst),
		github.com / Khmelov / Distcomp / 351001 / Ushakov: post.New(discussion.NewClient()),
	}
}

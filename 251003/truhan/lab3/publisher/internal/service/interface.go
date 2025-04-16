package service

import (
	"context"

	m "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
)

type CreatorService interface {
	CreateCreator(ctx context.Context, cr m.Creator) (m.Creator, error)
	GetCreators(ctx context.Context) ([]m.Creator, error)
	GetCreatorByID(ctx context.Context, id int) (m.Creator, error)
	UpdateCreatorByID(ctx context.Context, cr m.Creator) (m.Creator, error)
	DeleteCreatorByID(ctx context.Context, id int) error
}

type IssueService interface {
	CreateIssue(ctx context.Context, issue m.Issue) (m.Issue, error)
	GetIssues(ctx context.Context) ([]m.Issue, error)
	GetIssueByID(ctx context.Context, id int64) (m.Issue, error)
	UpdateIssueByID(ctx context.Context, issue m.Issue) (m.Issue, error)
	DeleteIssueByID(ctx context.Context, id int64) error
}

type MessageService interface {
	CreateMessage(ctx context.Context, message m.Message) (m.Message, error)
	GetMessages(ctx context.Context) ([]m.Message, error)
	GetMessageByID(ctx context.Context, id int64) (m.Message, error)
	UpdateMessageByID(ctx context.Context, message m.Message) (m.Message, error)
	DeleteMessageByID(ctx context.Context, id int64) error
}

type MarkService interface {
	CreateMark(ctx context.Context, mark m.Mark) (m.Mark, error)
	GetMarks(ctx context.Context) ([]m.Mark, error)
	GetMarkByID(ctx context.Context, id int64) (m.Mark, error)
	UpdateMarkByID(ctx context.Context, mark m.Mark) (m.Mark, error)
	DeleteMarkByID(ctx context.Context, id int64) error
}

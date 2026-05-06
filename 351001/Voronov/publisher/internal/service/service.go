package service

import (
	"publisher/internal/model"
	"publisher/internal/transport/dto/request"
	"publisher/internal/transport/dto/response"
	"context"
)

type UserService interface {
	FindByID(ctx context.Context, id int64) (*response.UserResponseTo, error)
	FindAll(ctx context.Context) ([]*response.UserResponseTo, error)
	Create(ctx context.Context, req *request.UserRequestTo) (*response.UserResponseTo, error)
	Update(ctx context.Context, id int64, req *request.UserRequestTo) (*response.UserResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

type IssueService interface {
	FindByID(ctx context.Context, id int64) (*response.IssueResponseTo, error)
	FindAll(ctx context.Context) ([]*response.IssueResponseTo, error)
	Create(ctx context.Context, req *request.IssueRequestTo) (*response.IssueResponseTo, error)
	Update(ctx context.Context, id int64, req *request.IssueRequestTo) (*response.IssueResponseTo, error)
	Delete(ctx context.Context, id int64) error
	FindByUserID(ctx context.Context, userID int64) (*response.UserResponseTo, error)
	FindByIssueID(ctx context.Context, issueID int64) ([]*response.LabelResponseTo, []*response.ReactionResponseTo, error)
	SearchIssues(ctx context.Context, labelNames []string, labelIDs []int64, userLogin, title, content string) ([]*response.IssueResponseTo, error)
}

type LabelService interface {
	FindByID(ctx context.Context, id int64) (*response.LabelResponseTo, error)
	FindAll(ctx context.Context) ([]*response.LabelResponseTo, error)
	Create(ctx context.Context, req *request.LabelRequestTo) (*response.LabelResponseTo, error)
	Update(ctx context.Context, id int64, req *request.LabelRequestTo) (*response.LabelResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

type ReactionService interface {
	FindByID(ctx context.Context, id int64) (*response.ReactionResponseTo, error)
	FindAll(ctx context.Context) ([]*response.ReactionResponseTo, error)
	FindByIssueID(ctx context.Context, issueID int64) ([]*response.ReactionResponseTo, error)
	Create(ctx context.Context, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error)
	Update(ctx context.Context, id int64, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

type Mapper interface {
	ToUserResponse(m *model.User) *response.UserResponseTo
	ToUserModel(req *request.UserRequestTo) *model.User
	ToIssueResponse(m *model.Issue) *response.IssueResponseTo
	ToIssueModel(req *request.IssueRequestTo) *model.Issue
	ToLabelResponse(m *model.Label) *response.LabelResponseTo
	ToLabelModel(req *request.LabelRequestTo) *model.Label
	ToReactionResponse(m *model.Reaction) *response.ReactionResponseTo
	ToReactionModel(req *request.ReactionRequestTo) *model.Reaction
}

package gateway

import (
	"context"

	"publisher/internal/transport/dto/request"
	"publisher/internal/transport/dto/response"
)

type ReactionGateway interface {
	FindByID(ctx context.Context, id int64) (*response.ReactionResponseTo, error)
	FindAll(ctx context.Context) ([]*response.ReactionResponseTo, error)
	FindByIssueID(ctx context.Context, issueID int64) ([]*response.ReactionResponseTo, error)
	Create(ctx context.Context, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error)
	Update(ctx context.Context, id int64, req *request.ReactionRequestTo) (*response.ReactionResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

package repository

import (
	"context"

	"discussion/internal/model"
)

type ReactionRepository interface {
	FindByID(ctx context.Context, id int64) (*model.Reaction, error)
	FindAll(ctx context.Context) ([]*model.Reaction, error)
	FindByIssueID(ctx context.Context, issueID int64) ([]*model.Reaction, error)
	Create(ctx context.Context, r *model.Reaction) (*model.Reaction, error)
	Update(ctx context.Context, id int64, r *model.Reaction) (*model.Reaction, error)
	Delete(ctx context.Context, id int64) error
}

package repository

import (
	"context"

	"publisher/internal/model"
)

type UserRepository interface {
	FindByID(ctx context.Context, id int64) (*model.User, error)
	FindByLogin(ctx context.Context, login string) (*model.User, error)
	FindAll(ctx context.Context, opts *QueryOptions) ([]*model.User, int64, error)
	Create(ctx context.Context, user *model.User) (*model.User, error)
	Update(ctx context.Context, id int64, user *model.User) (*model.User, error)
	Delete(ctx context.Context, id int64) error
}

type IssueRepository interface {
	FindByID(ctx context.Context, id int64) (*model.Issue, error)
	FindAll(ctx context.Context, opts *QueryOptions) ([]*model.Issue, int64, error)
	Create(ctx context.Context, issue *model.Issue) (*model.Issue, error)
	Update(ctx context.Context, id int64, issue *model.Issue) (*model.Issue, error)
	Delete(ctx context.Context, id int64) error
	FindByUserID(ctx context.Context, userID int64) ([]*model.Issue, error)
}

type LabelRepository interface {
	FindByID(ctx context.Context, id int64) (*model.Label, error)
	FindByName(ctx context.Context, name string) (*model.Label, error)
	FindAll(ctx context.Context, opts *QueryOptions) ([]*model.Label, int64, error)
	Create(ctx context.Context, label *model.Label) (*model.Label, error)
	Update(ctx context.Context, id int64, label *model.Label) (*model.Label, error)
	Delete(ctx context.Context, id int64) error
	FindByIssueID(ctx context.Context, issueID int64) ([]*model.Label, error)
	FindIssuesByLabelID(ctx context.Context, labelID int64) ([]int64, error)
	AddLabelToIssue(ctx context.Context, issueID, labelID int64) error
	RemoveLabelFromIssue(ctx context.Context, issueID, labelID int64) error
}

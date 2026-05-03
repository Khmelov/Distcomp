package service

import (
	"context"

	apperrors "discussion/internal/errors"
	"discussion/internal/model"
	"discussion/internal/repository"
)

type ReactionService interface {
	FindByID(ctx context.Context, id int64) (*model.Reaction, error)
	FindAll(ctx context.Context) ([]*model.Reaction, error)
	FindByIssueID(ctx context.Context, issueID int64) ([]*model.Reaction, error)
	Create(ctx context.Context, r *model.Reaction) (*model.Reaction, error)
	Update(ctx context.Context, id int64, r *model.Reaction) (*model.Reaction, error)
	Delete(ctx context.Context, id int64) error
}

type reactionService struct {
	repo repository.ReactionRepository
}

func NewReactionService(repo repository.ReactionRepository) ReactionService {
	return &reactionService{repo: repo}
}

func (s *reactionService) FindByID(ctx context.Context, id int64) (*model.Reaction, error) {
	return s.repo.FindByID(ctx, id)
}

func (s *reactionService) FindAll(ctx context.Context) ([]*model.Reaction, error) {
	return s.repo.FindAll(ctx)
}

func (s *reactionService) FindByIssueID(ctx context.Context, issueID int64) ([]*model.Reaction, error) {
	return s.repo.FindByIssueID(ctx, issueID)
}

func (s *reactionService) Create(ctx context.Context, r *model.Reaction) (*model.Reaction, error) {
	if r.IssueID == 0 {
		return nil, apperrors.ErrBadRequest
	}
	if len(r.Content) < 2 || len(r.Content) > 2048 {
		return nil, apperrors.ErrBadRequest
	}
	return s.repo.Create(ctx, r)
}

func (s *reactionService) Update(ctx context.Context, id int64, r *model.Reaction) (*model.Reaction, error) {
	if r.IssueID == 0 {
		return nil, apperrors.ErrBadRequest
	}
	if len(r.Content) < 2 || len(r.Content) > 2048 {
		return nil, apperrors.ErrBadRequest
	}
	return s.repo.Update(ctx, id, r)
}

func (s *reactionService) Delete(ctx context.Context, id int64) error {
	return s.repo.Delete(ctx, id)
}

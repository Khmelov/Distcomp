package service

import (
	"context"
	"strings"

	apperrors "discussion/internal/errors"
	"discussion/internal/model"
	"discussion/internal/repository"
)

// stopWords is the hardcoded list used for automatic moderation.
var stopWords = []string{"badword", "offensive", "spam", "hate", "abuse"}

type ReactionService interface {
	FindByID(ctx context.Context, id int64) (*model.Reaction, error)
	FindAll(ctx context.Context) ([]*model.Reaction, error)
	FindByIssueID(ctx context.Context, issueID int64) ([]*model.Reaction, error)
	Create(ctx context.Context, r *model.Reaction) (*model.Reaction, error)
	Update(ctx context.Context, id int64, r *model.Reaction) (*model.Reaction, error)
	Delete(ctx context.Context, id int64) error
	Moderate(ctx context.Context, r *model.Reaction) (*model.Reaction, error)
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
	if r.State == "" {
		r.State = model.ReactionStatePending
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

// Moderate runs stop-word moderation and persists the result.
// Sets state to APPROVE or DECLINE based on content.
func (s *reactionService) Moderate(ctx context.Context, r *model.Reaction) (*model.Reaction, error) {
	lower := strings.ToLower(r.Content)
	for _, word := range stopWords {
		if strings.Contains(lower, strings.ToLower(word)) {
			r.State = model.ReactionStateDecline
			return s.repo.Create(ctx, r)
		}
	}
	r.State = model.ReactionStateApprove
	return s.repo.Create(ctx, r)
}

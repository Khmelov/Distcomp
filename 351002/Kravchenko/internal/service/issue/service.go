package issue

import (
	"context"

	issuemodel "lab1/internal/model/issue"
	stickermodel "lab1/internal/model/sticker"
	"lab1/internal/repository"
)

type Service interface {
	CreateIssue(ctx context.Context, input *issuemodel.CreateIssueInput) (*issuemodel.Issue, error)
	GetIssue(ctx context.Context, id int64) (*issuemodel.Issue, error)
	UpdateIssue(ctx context.Context, id int64, input *issuemodel.UpdateIssueInput) (*issuemodel.Issue, error)
	DeleteIssue(ctx context.Context, id int64) error
	ListIssues(ctx context.Context, limit, offset int) ([]*issuemodel.Issue, error)
}
type issueServiceImpl struct {
	repos repository.AppRepository
}

func New(repos repository.AppRepository) Service {
	return &issueServiceImpl{repos: repos}
}

func (s *issueServiceImpl) CreateIssue(ctx context.Context, input *issuemodel.CreateIssueInput) (*issuemodel.Issue, error) {
	_, err := s.repos.EditorRepo().GetByID(ctx, input.EditorID)
	if err != nil {
		return nil, err
	}

	issue := &issuemodel.Issue{
		EditorID: input.EditorID,
		Title:    input.Title,
		Content:  input.Content,
	}

	createdIssue, err := s.repos.IssueRepo().Create(ctx, issue)
	if err != nil {
		return nil, err
	}

	if len(input.Stickers) > 0 {
		err = s.repos.IssueRepo().SetStickers(ctx, createdIssue.ID, input.Stickers)
		if err != nil {
			return nil, err
		}
	}

	return s.GetIssue(ctx, createdIssue.ID)
}

func (s *issueServiceImpl) GetIssue(ctx context.Context, id int64) (*issuemodel.Issue, error) {
	issue, err := s.repos.IssueRepo().GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	stickers, err := s.repos.IssueRepo().GetStickers(ctx, id)
	if err == nil && len(stickers) > 0 {
		issue.Stickers = make([]stickermodel.Sticker, len(stickers))
		for i, st := range stickers {
			issue.Stickers[i] = *st
		}
	}

	return issue, nil
}

func (s *issueServiceImpl) UpdateIssue(ctx context.Context, id int64, input *issuemodel.UpdateIssueInput) (*issuemodel.Issue, error) {
	issue, err := s.repos.IssueRepo().GetByID(ctx, id)
	if err != nil {
		return nil, err
	}

	if input.Title != nil {
		issue.Title = *input.Title
	}
	if input.Content != nil {
		issue.Content = *input.Content
	}

	err = s.repos.IssueRepo().Update(ctx, issue)
	if err != nil {
		return nil, err
	}

	if input.Stickers != nil {
		err = s.repos.IssueRepo().SetStickers(ctx, id, input.Stickers)
		if err != nil {
			return nil, err
		}
	}

	return s.GetIssue(ctx, id)
}

func (s *issueServiceImpl) DeleteIssue(ctx context.Context, id int64) error {
	return s.repos.IssueRepo().Delete(ctx, id)
}

func (s *issueServiceImpl) ListIssues(ctx context.Context, limit, offset int) ([]*issuemodel.Issue, error) {
	return s.repos.IssueRepo().List(ctx, limit, offset)
}

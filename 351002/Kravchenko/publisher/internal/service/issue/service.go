package issue

import (
	"context"
	"errors"
	"labs/publisher/internal/repository"
	issuemodel "labs/shared/model/issue"
	stickermodel "labs/shared/model/sticker"
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
		stickerIDs, err := s.getOrCreateStickerIDs(ctx, input.Stickers)
		if err != nil {
			return nil, err
		}
		err = s.repos.IssueRepo().SetStickers(ctx, createdIssue.ID, stickerIDs)
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
		stickerIDs, err := s.getOrCreateStickerIDs(ctx, input.Stickers)
		if err != nil {
			return nil, err
		}
		err = s.repos.IssueRepo().SetStickers(ctx, id, stickerIDs)
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

func (s *issueServiceImpl) getOrCreateStickerIDs(ctx context.Context, names []string) ([]int64, error) {
	var ids []int64
	for _, name := range names {
		st, err := s.repos.StickerRepo().GetByName(ctx, name)
		if err != nil {
			if errors.Is(err, stickermodel.ErrNotFound) {
				// Если стикера нет, создаем его на лету
				st, err = s.repos.StickerRepo().Create(ctx, &stickermodel.Sticker{Name: name})
				if err != nil {
					return nil, err
				}
			} else {
				return nil, err
			}
		}
		ids = append(ids, st.ID)
	}
	return ids, nil
}

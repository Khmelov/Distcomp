package usecase

import (
	"context"
	"discussion-service/internal/entity"
	"discussion-service/internal/repository"
	"strings"
	"unicode/utf8"
)

type CommentUsecase struct {
	repo *repository.CommentRepository
}

func NewCommentUsecase(repo *repository.CommentRepository) *CommentUsecase {
	return &CommentUsecase{repo: repo}
}

func (uc *CommentUsecase) toDTO(c entity.Comment) entity.CommentResponseTo {
	return entity.CommentResponseTo{
		ID: c.ID, TopicID: c.TopicID, Country: c.Country, Content: c.Content, State: c.State,
	}
}

func (uc *CommentUsecase) moderate(content string) string {
	stopWords := []string{"spam", "bad", "evil"}
	contentLower := strings.ToLower(content)
	for _, word := range stopWords {
		if strings.Contains(contentLower, word) {
			return "DECLINE"
		}
	}
	return "APPROVE"
}

func (uc *CommentUsecase) Create(ctx context.Context, req entity.CommentRequestTo) (entity.CommentResponseTo, error) {
	if l := utf8.RuneCountInString(req.Content); l < 2 || l > 2048 {
		return entity.CommentResponseTo{}, entity.NewErr(400, "08", "content must be 2..2048 chars")
	}

	state := uc.moderate(req.Content)
	comment := entity.Comment{TopicID: req.TopicID, Country: req.Country, Content: req.Content, State: state}
	saved, err := uc.repo.Create(ctx, comment)
	if err != nil {
		return entity.CommentResponseTo{}, err
	}
	return uc.toDTO(saved), nil
}

func (uc *CommentUsecase) GetByTopicID(ctx context.Context, topicID int64) ([]entity.CommentResponseTo, error) {
	comments, err := uc.repo.GetByTopicID(ctx, topicID)
	if err != nil {
		return nil, err
	}
	res := make([]entity.CommentResponseTo, 0, len(comments))
	for _, c := range comments {
		res = append(res, uc.toDTO(c))
	}
	return res, nil
}

func (uc *CommentUsecase) GetAll(ctx context.Context) ([]entity.CommentResponseTo, error) {
	comments, err := uc.repo.GetAll(ctx)
	if err != nil {
		return nil, err
	}
	res := make([]entity.CommentResponseTo, 0)
	for _, c := range comments {
		res = append(res, uc.toDTO(c))
	}
	return res, nil
}

func (uc *CommentUsecase) GetByID(ctx context.Context, id int64) (entity.CommentResponseTo, error) {
	c, err := uc.repo.GetByIDOnly(ctx, id)
	if err != nil {
		return entity.CommentResponseTo{}, err
	}
	return uc.toDTO(c), nil
}

func (uc *CommentUsecase) Update(ctx context.Context, id int64, req entity.CommentRequestTo) (entity.CommentResponseTo, error) {
	c, err := uc.repo.GetByIDOnly(ctx, id)
	if err != nil {
		return entity.CommentResponseTo{}, err
	}
	c.Content = req.Content
	c.Country = req.Country
	c.TopicID = req.TopicID
	c.State = uc.moderate(req.Content)
	err = uc.repo.Update(ctx, c)
	return uc.toDTO(c), err
}

func (uc *CommentUsecase) Delete(ctx context.Context, id int64) error {
	if _, err := uc.repo.GetByIDOnly(ctx, id); err != nil {
		return err
	}
	return uc.repo.DeleteByIDOnly(ctx, id)
}

package usecase

import (
	"context"
	"note-service/internal/entity"
	"note-service/internal/repository"
	"unicode/utf8"
)

type CommentUsecase struct {
	repo repository.NoteRepository[entity.Comment]
}

func NewCommentUsecase(repo repository.NoteRepository[entity.Comment]) *CommentUsecase {
	return &CommentUsecase{repo: repo}
}

func (uc *CommentUsecase) validate(req entity.CommentRequestTo) error {
	if l := utf8.RuneCountInString(req.Content); l < 2 || l > 2048 {
		return entity.NewErr(400, "08", "content must be 2..2048 chars")
	}
	return nil
}

func (uc *CommentUsecase) toDTO(c entity.Comment) entity.CommentResponseTo {
	return entity.CommentResponseTo{ID: c.ID, TopicID: c.TopicID, Content: c.Content}
}

func (uc *CommentUsecase) Create(ctx context.Context, req entity.CommentRequestTo) (entity.CommentResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.CommentResponseTo{}, err
	}
	comment := entity.Comment{TopicID: req.TopicID, Content: req.Content}
	saved, err := uc.repo.Create(ctx, comment)
	if err != nil {
		return entity.CommentResponseTo{}, err
	}
	return uc.toDTO(saved), nil
}

func (uc *CommentUsecase) GetByID(ctx context.Context, id int64) (entity.CommentResponseTo, error) {
	c, err := uc.repo.GetByID(ctx, id)
	if err != nil {
		return entity.CommentResponseTo{}, err
	}
	return uc.toDTO(c), nil
}

func (uc *CommentUsecase) GetAll(ctx context.Context, limit, offset int) ([]entity.CommentResponseTo, error) {
	comments, err := uc.repo.GetAll(ctx, limit, offset)
	if err != nil {
		return nil, err
	}
	res := make([]entity.CommentResponseTo, 0)
	for _, c := range comments {
		res = append(res, uc.toDTO(c))
	}
	return res, nil
}

func (uc *CommentUsecase) Update(ctx context.Context, id int64, req entity.CommentRequestTo) (entity.CommentResponseTo, error) {
	if err := uc.validate(req); err != nil {
		return entity.CommentResponseTo{}, err
	}
	if _, err := uc.repo.GetByID(ctx, id); err != nil {
		return entity.CommentResponseTo{}, err
	}
	comment := entity.Comment{ID: id, TopicID: req.TopicID, Content: req.Content}
	updated, err := uc.repo.Update(ctx, id, comment)
	if err != nil {
		return entity.CommentResponseTo{}, err
	}
	return uc.toDTO(updated), nil
}

func (uc *CommentUsecase) Delete(ctx context.Context, id int64) error {
	return uc.repo.Delete(ctx, id)
}

package service

import (
	"context"

	"distcomp/internal/domain"
	"distcomp/internal/dto"
	"distcomp/internal/discussion/repository/cassandra"
)

type CommentService struct {
	repo *cassandra.CommentStorage
}

func NewCommentService(repo *cassandra.CommentStorage) *CommentService {
	return &CommentService{repo: repo}
}

func (s *CommentService) Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	c := &domain.Comment{ArticleID: req.ArticleID, Content: req.Content}
	if err := s.repo.Create(ctx, c); err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}, nil
}

func (s *CommentService) GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error) {
	c, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}, nil
}

func (s *CommentService) GetAll(ctx context.Context) ([]dto.CommentResponseTo, error) {
	comments, err := s.repo.GetAll(ctx)
	if err != nil {
		return nil, err
	}
	res := make([]dto.CommentResponseTo, len(comments))
	for i, c := range comments {
		res[i] = dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}
	}
	return res, nil
}

func (s *CommentService) Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	c := &domain.Comment{ID: id, ArticleID: req.ArticleID, Content: req.Content}
	if err := s.repo.Update(ctx, c); err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content}, nil
}

func (s *CommentService) Delete(ctx context.Context, id int64) error {
	return s.repo.Delete(ctx, id)
}
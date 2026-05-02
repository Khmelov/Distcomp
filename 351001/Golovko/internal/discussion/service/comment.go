package service

import (
	"context"

	"distcomp/internal/discussion/repository/cassandra"
	"distcomp/internal/domain"
	"distcomp/internal/dto"
)

type CommentService struct {
	Repo *cassandra.CommentStorage
}

func NewCommentService(repo *cassandra.CommentStorage) *CommentService {
	return &CommentService{Repo: repo}
}

func (s *CommentService) Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	c := &domain.Comment{ArticleID: req.ArticleID, Content: req.Content}
	if err := s.Repo.Create(ctx, c); err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content, State: c.State}, nil
}

func (s *CommentService) GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error) {
	c, err := s.Repo.GetByID(ctx, id)
	if err != nil {
		return dto.CommentResponseTo{}, err
	}
	return dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content, State: c.State}, nil
}

func (s *CommentService) GetAll(ctx context.Context) ([]dto.CommentResponseTo, error) {
	comments, err := s.Repo.GetAll(ctx)
	if err != nil {
		return nil, err
	}
	res := make([]dto.CommentResponseTo, len(comments))
	for i, c := range comments {
		res[i] = dto.CommentResponseTo{ID: c.ID, ArticleID: c.ArticleID, Content: c.Content, State: c.State}
	}
	return res, nil
}

func (s *CommentService) Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	c := &domain.Comment{ID: id, ArticleID: req.ArticleID, Content: req.Content}
	if err := s.Repo.Update(ctx, c); err != nil {
		return dto.CommentResponseTo{}, err
	}
	cFromDB, _ := s.Repo.GetByID(ctx, id)
	return dto.CommentResponseTo{ID: cFromDB.ID, ArticleID: cFromDB.ArticleID, Content: cFromDB.Content, State: cFromDB.State}, nil
}

func (s *CommentService) Delete(ctx context.Context, id int64) error {
	return s.Repo.Delete(ctx, id)
}
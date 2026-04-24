package service

import (
	"context"
	"distcomp/internal/dto"
)

type Editor interface {
	Create(ctx context.Context, req dto.EditorRequestTo) (dto.EditorResponseTo, error)
	GetByID(ctx context.Context, id int64) (dto.EditorResponseTo, error)
	GetAll(ctx context.Context) ([]dto.EditorResponseTo, error)
	Update(ctx context.Context, id int64, req dto.EditorRequestTo) (dto.EditorResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

type Article interface {
	Create(ctx context.Context, req dto.ArticleRequestTo) (dto.ArticleResponseTo, error)
	GetByID(ctx context.Context, id int64) (dto.ArticleResponseTo, error)
	GetAll(ctx context.Context) ([]dto.ArticleResponseTo, error)
	Update(ctx context.Context, id int64, req dto.ArticleRequestTo) (dto.ArticleResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

type Tag interface {
	Create(ctx context.Context, req dto.TagRequestTo) (dto.TagResponseTo, error)
	GetByID(ctx context.Context, id int64) (dto.TagResponseTo, error)
	GetAll(ctx context.Context) ([]dto.TagResponseTo, error)
	Update(ctx context.Context, id int64, req dto.TagRequestTo) (dto.TagResponseTo, error)
	Delete(ctx context.Context, id int64) error
}

type Comment interface {
	Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error)
	GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error)
	GetAll(ctx context.Context) ([]dto.CommentResponseTo, error)
	Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error)
	Delete(ctx context.Context, id int64) error
}
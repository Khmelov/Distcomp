package repository

import (
	"context"

	"distcomp/internal/domain"
)

type ListParams struct {
	Limit  int
	Offset int
	SortBy string
	Order  string
}

type Storage interface {
	CreateEditor(ctx context.Context, editor *domain.Editor) error
	GetEditorByID(ctx context.Context, id int64) (*domain.Editor, error)
	GetAllEditors(ctx context.Context, params ListParams) ([]*domain.Editor, error)
	UpdateEditor(ctx context.Context, editor *domain.Editor) error
	DeleteEditor(ctx context.Context, id int64) error

	CreateArticle(ctx context.Context, article *domain.Article) error
	GetArticleByID(ctx context.Context, id int64) (*domain.Article, error)
	GetAllArticles(ctx context.Context, params ListParams) ([]*domain.Article, error)
	UpdateArticle(ctx context.Context, article *domain.Article) error
	DeleteArticle(ctx context.Context, id int64) error

	CreateTag(ctx context.Context, tag *domain.Tag) error
	GetTagByID(ctx context.Context, id int64) (*domain.Tag, error)
	GetAllTags(ctx context.Context, params ListParams) ([]*domain.Tag, error)
	UpdateTag(ctx context.Context, tag *domain.Tag) error
	DeleteTag(ctx context.Context, id int64) error

	CreateComment(ctx context.Context, comment *domain.Comment) error
	GetCommentByID(ctx context.Context, id int64) (*domain.Comment, error)
	GetAllComments(ctx context.Context, params ListParams) ([]*domain.Comment, error)
	UpdateComment(ctx context.Context, comment *domain.Comment) error
	DeleteComment(ctx context.Context, id int64) error
}
package repository

import (
	"context"

	"distcomp/internal/domain"
)

type Storage interface {
	// Editor
	CreateEditor(ctx context.Context, editor *domain.Editor) error
	GetEditorByID(ctx context.Context, id int64) (*domain.Editor, error)
	GetAllEditors(ctx context.Context) ([]*domain.Editor, error)
	UpdateEditor(ctx context.Context, editor *domain.Editor) error
	DeleteEditor(ctx context.Context, id int64) error

	// Article
	CreateArticle(ctx context.Context, article *domain.Article) error
	GetArticleByID(ctx context.Context, id int64) (*domain.Article, error)
	GetAllArticles(ctx context.Context) ([]*domain.Article, error)
	UpdateArticle(ctx context.Context, article *domain.Article) error
	DeleteArticle(ctx context.Context, id int64) error

	// Tag
	CreateTag(ctx context.Context, tag *domain.Tag) error
	GetTagByID(ctx context.Context, id int64) (*domain.Tag, error)
	GetAllTags(ctx context.Context) ([]*domain.Tag, error)
	UpdateTag(ctx context.Context, tag *domain.Tag) error
	DeleteTag(ctx context.Context, id int64) error

	// Comment
	CreateComment(ctx context.Context, comment *domain.Comment) error
	GetCommentByID(ctx context.Context, id int64) (*domain.Comment, error)
	GetAllComments(ctx context.Context) ([]*domain.Comment, error)
	UpdateComment(ctx context.Context, comment *domain.Comment) error
	DeleteComment(ctx context.Context, id int64) error
}
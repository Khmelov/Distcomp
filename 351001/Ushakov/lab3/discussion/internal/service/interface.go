package service

import (
	"context"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/model"
)

type Service interface {
	PostService
}

type PostService interface {
	GetPost(ctx context.Context, id int64) (model.Post, error)
	GetPosts(ctx context.Context) ([]model.Post, error)
	CreatePost(ctx context.Context, args model.Post) (model.Post, error)
	UpdatePost(ctx context.Context, args model.Post) (model.Post, error)
	DeletePost(ctx context.Context, id int64) error
}

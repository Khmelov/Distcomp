package post

import (
	"context"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/model"
	"github.com/gin-gonic/gin"
)

type postService interface {
	GetPost(ctx context.Context, id int64) (model.Post, error)
	GetPosts(ctx context.Context) ([]model.Post, error)
	CreatePost(ctx context.Context, args model.Post) (model.Post, error)
	UpdatePost(ctx context.Context, args model.Post) (model.Post, error)
	DeletePost(ctx context.Context, id int64) error
}

type noticeHandler struct {
	notice postService
}

func New(noticeSvc postService) *noticeHandler {
	return &noticeHandler{
		notice: noticeSvc,
	}
}

func (h *noticeHandler) InitRoutes(router gin.IRouter) {
	v1 := router.Group("/v1.0")
	{
		v1.GET("/comments", h.List())
		v1.GET("/comments/:id", h.Get())
		v1.POST("/comments", h.Create())
		v1.DELETE("/comments/:id", h.Delete())
		v1.PUT("/comments", h.Update())
	}
}

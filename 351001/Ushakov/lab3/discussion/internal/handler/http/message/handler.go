package post

import (
	"context"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/model"
	"github.com/gin-gonic/gin"
)

type postService interface {
	Getgithub.com
/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Getgithub.com/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Creategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, args model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Updategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, args model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
Deletegithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) error
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

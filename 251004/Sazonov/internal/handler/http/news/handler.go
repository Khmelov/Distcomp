package news

import (
	"context"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/gin-gonic/gin"
)

type newsService interface {
	GetNews(ctx context.Context, id int64) (model.News, error)
	ListNews(ctx context.Context) ([]model.News, error)
	CreateNews(ctx context.Context, news model.News) (model.News, error)
	UpdateNews(ctx context.Context, news model.News) (model.News, error)
	DeleteNews(ctx context.Context, id int64) error
}

type newsHandler struct {
	news newsService
}

func New(newsSvc newsService) *newsHandler {
	return &newsHandler{
		news: newsSvc,
	}
}

func (h *newsHandler) InitRoutes(router gin.IRouter) {
	v1 := router.Group("/v1.0")
	{
		v1.GET("/news", h.List())
		v1.GET("/news/:id", h.Get())
		v1.POST("/news", h.Create())
		v1.DELETE("/news/:id", h.Delete())
		v1.PUT("/news", h.Update())
	}
}

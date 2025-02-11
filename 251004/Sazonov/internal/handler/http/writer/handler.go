package writer

import (
	"context"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/gin-gonic/gin"
)

type writerService interface {
	GetWriter(ctx context.Context, id int64) (model.Writer, error)
	ListWriters(ctx context.Context) ([]model.Writer, error)
	CreateWriter(ctx context.Context, writer model.Writer) (model.Writer, error)
	UpdateWriter(ctx context.Context, writer model.Writer) (model.Writer, error)
	DeleteWriter(ctx context.Context, id int64) error
}

type writerHandler struct {
	writer writerService
}

func New(writerSvc writerService) *writerHandler {
	return &writerHandler{
		writer: writerSvc,
	}
}

func (h *writerHandler) InitRoutes(router gin.IRouter) {
	v1 := router.Group("/v1.0")
	{
		v1.GET("/writers", h.List())
		v1.POST("/writers", h.Create())
		v1.GET("/writers/:id", h.Get())
		v1.DELETE("/writers/:id", h.Delete())
		v1.PUT("/writers", h.Update())
	}
}

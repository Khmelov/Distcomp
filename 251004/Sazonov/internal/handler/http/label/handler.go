package label

import (
	"context"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/gin-gonic/gin"
)

type labelService interface {
	GetLabel(ctx context.Context, id int64) (model.Label, error)
	ListLabels(ctx context.Context) ([]model.Label, error)
	CreateLabel(ctx context.Context, args model.Label) (model.Label, error)
	UpdateLabel(ctx context.Context, args model.Label) (model.Label, error)
	DeleteLabel(ctx context.Context, id int64) error
}

type labelHandler struct {
	label labelService
}

func New(labelSvc labelService) *labelHandler {
	return &labelHandler{
		label: labelSvc,
	}
}

func (h *labelHandler) InitRoutes(router gin.IRouter) {
	v1 := router.Group("/v1.0")
	{
		v1.GET("/labels", h.List())
		v1.GET("/labels/:id", h.Get())
		v1.POST("/labels", h.Create())
		v1.DELETE("/labels/:id", h.Delete())
		v1.PUT("/labels", h.Update())
	}
}

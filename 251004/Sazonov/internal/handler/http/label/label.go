package label

import (
	"net/http"
	"strconv"

	httperrors "github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/errors"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/validator"
	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
)

func (h *labelHandler) List() gin.HandlerFunc {
	type request struct{}

	type resposne struct{}

	return func(c *gin.Context) {
		labelList, err := h.label.ListLabels(c)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, labelList)
	}
}

func (h *labelHandler) Get() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		label, err := h.label.GetLabel(c, id)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, label)
	}
}

func (h *labelHandler) Create() gin.HandlerFunc {
	type request struct {
		Name string `json:"name" validate:"required,min=2,max=32"`
	}

	type response struct{}

	return func(c *gin.Context) {
		var req request

		if err := c.BindJSON(&req); err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := validator.Validtor().Struct(req); err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		label, err := h.label.CreateLabel(
			c,
			model.Label{
				Name: req.Name,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusCreated, label)
	}
}

func (h *labelHandler) Update() gin.HandlerFunc {
	type request struct {
		ID   int64  `json:"id"   validate:"required,gte=1"`
		Name string `json:"name" validate:"omitempty,required,min=2,max=32"`
	}

	type response struct{}

	return func(c *gin.Context) {
		var req request

		if err := c.BindJSON(&req); err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := validator.Validtor().Struct(req); err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		label, err := h.label.UpdateLabel(
			c,
			model.Label{
				ID:   req.ID,
				Name: req.Name,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, label)
	}
}

func (h *labelHandler) Delete() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := h.label.DeleteLabel(c, id); err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusNoContent, response{})
	}
}

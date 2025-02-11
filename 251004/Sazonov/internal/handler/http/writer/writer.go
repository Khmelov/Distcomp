package writer

import (
	"net/http"
	"strconv"

	httperrors "github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/errors"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/validator"
	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
)

func (h *writerHandler) List() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		writers, err := h.writer.ListWriters(c)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, writers)
	}
}

func (h *writerHandler) Get() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if id < 1 {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, "id bad format"))
			return
		}

		writer, err := h.writer.GetWriter(c, id)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, writer)
	}
}

func (h *writerHandler) Create() gin.HandlerFunc {
	type request struct {
		Login     string `json:"login"     validate:"required,min=2,max=64"`
		Password  string `json:"password"  validate:"required,min=8,max=128"`
		FirstName string `json:"firstname" validate:"required,min=2,max=64"`
		LastName  string `json:"lastname"  validate:"required,min=2,max=64"`
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

		writer, err := h.writer.CreateWriter(
			c,
			model.Writer{
				Login:     req.Login,
				Password:  req.Password,
				FirstName: req.FirstName,
				LastName:  req.LastName,
			})
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusCreated, writer)
	}
}

func (h *writerHandler) Update() gin.HandlerFunc {
	type request struct {
		ID        int64  `json:"id"        validate:"required,gte=1"`
		Login     string `json:"login"     validate:"omitempty,required,min=2,max=64"`
		Password  string `json:"password"  validate:"omitempty,required,min=8,max=128"`
		FirstName string `json:"firstname" validate:"omitempty,required,min=2,max=64"`
		LastName  string `json:"lastname"  validate:"omitempty,required,min=2,max=64"`
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

		writer, err := h.writer.UpdateWriter(
			c,
			model.Writer{
				ID:        req.ID,
				Login:     req.Login,
				Password:  req.Password,
				FirstName: req.FirstName,
				LastName:  req.LastName,
			})
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, writer)
	}
}

func (h *writerHandler) Delete() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := h.writer.DeleteWriter(c, id); err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusNoContent, response{})
	}
}

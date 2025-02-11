package news

import (
	"net/http"
	"strconv"

	httperrors "github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/errors"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/logger"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/validator"
	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
	"go.uber.org/zap"
)

func (h *newsHandler) List() gin.HandlerFunc {
	type request struct{}

	type resposne struct{}

	return func(c *gin.Context) {
		newsList, err := h.news.ListNews(c)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, newsList)
	}
}

func (h *newsHandler) Get() gin.HandlerFunc {
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

		writer, err := h.news.GetNews(c, id)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, writer)
	}
}

func (h *newsHandler) Create() gin.HandlerFunc {
	type request struct {
		WriterID int64  `json:"writerId" validate:"required,gte=1"`
		Title    string `json:"title"    validate:"required,min=2,max=64"`
		Content  string `json:"content"  validate:"required,min=4,max=2048"`
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

		news, err := h.news.CreateNews(
			c,
			model.News{
				WriterID: req.WriterID,
				Title:    req.Title,
				Content:  req.Content,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusCreated, news)
	}
}

func (h *newsHandler) Update() gin.HandlerFunc {
	type request struct {
		ID       int64  `json:"id"       validate:"required,gte=1"`
		WriterID int64  `json:"writerId" validate:"omitempty,required,gte=1"`
		Title    string `json:"title"    validate:"omitempty,required,min=2,max=64"`
		Content  string `json:"content"  validate:"omitempty,required,min=4,max=2048"`
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

		news, err := h.news.UpdateNews(
			c,
			model.News{
				ID:       req.ID,
				WriterID: req.WriterID,
				Title:    req.Title,
				Content:  req.Content,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		logger.Info(c, "new update response", zap.Any("news", news))

		c.JSON(http.StatusOK, news)
	}
}

func (h *newsHandler) Delete() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := h.news.DeleteNews(c, id); err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusNoContent, response{})
	}
}

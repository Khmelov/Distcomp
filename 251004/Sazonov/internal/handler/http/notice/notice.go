package notice

import (
	"net/http"
	"strconv"

	httperrors "github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/errors"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/model"
	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/validator"
	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
)

func (h *noticeHandler) List() gin.HandlerFunc {
	type request struct{}

	type resposne struct{}

	return func(c *gin.Context) {
		noticeList, err := h.notice.ListNotices(c)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, noticeList)
	}
}

func (h *noticeHandler) Get() gin.HandlerFunc {
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

		writer, err := h.notice.GetNotice(c, id)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, writer)
	}
}

func (h *noticeHandler) Create() gin.HandlerFunc {
	type request struct {
		NewsID  int64  `json:"newsId"  validate:"required,gte=1"`
		Content string `json:"content" validate:"required,min=4,max=2048"`
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

		notice, err := h.notice.CreateNotice(
			c,
			model.Notice{
				NewsID:  req.NewsID,
				Content: req.Content,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusCreated, notice)
	}
}

func (h *noticeHandler) Update() gin.HandlerFunc {
	type request struct {
		ID      int64  `json:"id"      validate:"required,gte=1"`
		NewsID  int64  `json:"newsId"  validate:"omitempty,required,gte=1"`
		Content string `json:"content" validate:"omitempty,required,min=4,max=2048"`
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

		notice, err := h.notice.UpdateNotice(
			c,
			model.Notice{
				ID:      req.ID,
				NewsID:  req.NewsID,
				Content: req.Content,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, notice)
	}
}

func (h *noticeHandler) Delete() gin.HandlerFunc {
	type request struct{}

	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := h.notice.DeleteNotice(c, id); err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusNoContent, response{})
	}
}

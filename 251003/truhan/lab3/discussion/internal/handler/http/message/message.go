package message

import (
	"fmt"
	"net/http"
	"strconv"

	httperrors "github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/internal/handler/http/errors"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/internal/model"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/pkg/validator"
	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
)

func (h *noticeHandler) List() gin.HandlerFunc {
	return func(c *gin.Context) {
		noticeList, err := h.notice.GetMessages(c)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, noticeList)
	}
}

func (h *noticeHandler) Get() gin.HandlerFunc {
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

		writer, err := h.notice.GetMessage(c, id)
		if err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusOK, writer)
	}
}

func (h *noticeHandler) Create() gin.HandlerFunc {
	type request struct {
		NewsID  int64  `json:"tweetId"  validate:"required,gte=1"`
		Content string `json:"content" validate:"required,min=4,max=2048"`
	}

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

		notice, err := h.notice.CreateMessage(
			c,
			model.Message{
				IssueID: req.NewsID,
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
		NewsID  int64  `json:"tweetId"  validate:"omitempty,required,gte=1"`
		Content string `json:"content" validate:"omitempty,required,min=4,max=2048"`
	}

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

		notice, err := h.notice.UpdateMessage(
			c,
			model.Message{
				ID:      req.ID,
				IssueID: req.NewsID,
				Content: req.Content,
			},
		)
		if err != nil {
			httperrors.Error(c, err)
			fmt.Println(err.Error())
			return
		}

		c.JSON(http.StatusOK, notice)
	}
}

func (h *noticeHandler) Delete() gin.HandlerFunc {
	type response struct{}

	return func(c *gin.Context) {
		id, err := strconv.ParseInt(c.Param("id"), 10, 64)
		if err != nil {
			httperrors.Error(c, errors.Wrap(errors.ErrBadRequest, err.Error()))
			return
		}

		if err := h.notice.DeleteMessage(c, id); err != nil {
			httperrors.Error(c, err)
			return
		}

		c.JSON(http.StatusNoContent, response{})
	}
}

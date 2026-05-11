package handlers

import (
	"fmt"
	"lab-rest/internal/models"
	"lab-rest/internal/repository"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

type BaseHandler[Req any, Resp any] struct {
	Repo  *repository.Storage[Resp]
	MapTo func(Req, int64) Resp
}

func SendErr(c *gin.Context, status int, suffix int, msg string) {
	code, _ := strconv.Atoi(fmt.Sprintf("%d%02d", status, suffix))
	c.JSON(status, models.ErrorResponse{ErrorMessage: msg, ErrorCode: code})
}

func (h *BaseHandler[Req, Resp]) GetAll(c *gin.Context) {
	c.JSON(http.StatusOK, h.Repo.GetAll())
}

func (h *BaseHandler[Req, Resp]) GetByID(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if v, ok := h.Repo.Get(id); ok {
		c.JSON(http.StatusOK, v)
	} else {
		SendErr(c, 404, 01, "Not found")
	}
}

func (h *BaseHandler[Req, Resp]) Create(c *gin.Context) {
	var req Req
	if err := c.ShouldBindJSON(&req); err != nil {
		SendErr(c, 400, 01, "Validation failed")
		return
	}
	res := h.Repo.Create(*new(Resp), func(i *Resp, id int64) {
		*i = h.MapTo(req, id)
	})
	c.JSON(http.StatusCreated, res)
}

func (h *BaseHandler[Req, Resp]) Delete(c *gin.Context) {
	id, _ := strconv.ParseInt(c.Param("id"), 10, 64)
	if ok := h.Repo.Delete(id); ok {
		c.Status(http.StatusNoContent)
	} else {
		SendErr(c, 404, 01, "Not found for delete")
	}
}

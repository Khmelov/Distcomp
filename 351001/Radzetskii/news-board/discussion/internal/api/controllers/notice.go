package controllers

import (
	"net/http"
	"news-board/discussion/internal/dto"
	"news-board/discussion/internal/service"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type NoticeHandler struct {
	service  *service.NoticeService
	validate *validator.Validate
}

func NewNoticeHandler(service *service.NoticeService) *NoticeHandler {
	return &NoticeHandler{
		service:  service,
		validate: validator.New(),
	}
}

func (h *NoticeHandler) RegisterRoutes(rg *gin.RouterGroup) {
	v1 := rg.Group("/v1.0")
	{
		v1.POST("/notices", h.Create)
		v1.GET("/notices", h.GetAll)

		v1.GET("/notices/:id", h.GetByID)
		v1.PUT("/notices/:id", h.Update)
		v1.DELETE("/notices/:id", h.Delete)

		v1.GET("/notices/by-news/:newsId", h.GetByNewsID)

		v1.GET("/notices/by-key/:country/:newsId/:id", h.GetByIDFromPath)
		v1.PUT("/notices/by-key/:country/:newsId/:id", h.UpdateFromPath)
		v1.DELETE("/notices/by-key/:country/:newsId/:id", h.DeleteFromPath)

		v1.GET("/notices/by-country/:country/news/:newsId", h.GetByNewsIDFromPath)
	}
}

func (h *NoticeHandler) Create(c *gin.Context) {
	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format")
		return
	}
	if err := h.validate.Struct(req); err != nil {
		respondBadRequest(c, "Validation failed: "+err.Error())
		return
	}

	resp, err := h.service.Create(c.Request.Context(), &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusCreated, resp)
}

func (h *NoticeHandler) GetAll(c *gin.Context) {
	limit, offset, ok := parsePagination(c)
	if !ok {
		return
	}

	resp, err := h.service.GetAll(c.Request.Context(), limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) GetByID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	resp, err := h.service.GetByID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) Update(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format")
		return
	}

	resp, err := h.service.Update(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) Delete(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	if err := h.service.Delete(c.Request.Context(), id); err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *NoticeHandler) GetByNewsID(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}

	resp, err := h.service.GetByNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) GetByIDFromPath(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	resp, err := h.service.GetByID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) UpdateFromPath(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format")
		return
	}

	resp, err := h.service.Update(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) DeleteFromPath(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	if err := h.service.Delete(c.Request.Context(), id); err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *NoticeHandler) GetByNewsIDFromPath(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}

	resp, err := h.service.GetByNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

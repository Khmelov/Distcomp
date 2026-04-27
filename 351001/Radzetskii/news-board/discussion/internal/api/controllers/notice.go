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
		v1.GET("/notices/:id", h.GetByLegacyID)
		v1.PUT("/notices/:id", h.UpdateByLegacyID)
		v1.DELETE("/notices/:id", h.DeleteByLegacyID)
		v1.GET("/notices/by-news/:newsId", h.GetByLegacyNewsID)
		v1.GET("/notices/by-key/:country/:newsId/:id", h.GetByID)
		v1.PUT("/notices/by-key/:country/:newsId/:id", h.Update)
		v1.DELETE("/notices/by-key/:country/:newsId/:id", h.Delete)
		v1.GET("/notices/by-country/:country/news/:newsId", h.GetByNewsID)
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
	country := c.Query("country")

	resp, err := h.service.GetAll(c.Request.Context(), country, limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) GetByLegacyID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	resp, err := h.service.GetByID(c.Request.Context(), "", 0, id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) GetByID(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format")
		return
	}
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	resp, err := h.service.GetByID(c.Request.Context(), country, newsID, id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) Update(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format")
		return
	}
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format")
		return
	}
	if err := h.validate.Struct(req); err != nil {
		respondBadRequest(c, "Validation failed: "+err.Error())
		return
	}

	resp, err := h.service.Update(c.Request.Context(), country, newsID, id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) UpdateByLegacyID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format")
		return
	}
	if err := h.validate.Struct(req); err != nil {
		respondBadRequest(c, "Validation failed: "+err.Error())
		return
	}

	resp, err := h.service.Update(c.Request.Context(), "", 0, id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) Delete(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format")
		return
	}
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	if err := h.service.Delete(c.Request.Context(), country, newsID, id); err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *NoticeHandler) DeleteByLegacyID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	if err := h.service.Delete(c.Request.Context(), "", 0, id); err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

func (h *NoticeHandler) GetByNewsID(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format")
		return
	}
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}

	resp, err := h.service.GetByNewsID(c.Request.Context(), country, newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

func (h *NoticeHandler) GetByLegacyNewsID(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}

	resp, err := h.service.GetByNewsID(c.Request.Context(), "", newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, resp)
}

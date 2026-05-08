package controllers

import (
	"net/http"
	"news-board/publisher/internal/auth"
	"news-board/publisher/internal/dto"
	"news-board/publisher/internal/service"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type NoticeHandler struct {
	noticeService *service.NoticeService
	validate      *validator.Validate
	jwtSecret     string
}

func NewNoticeHandler(svc *service.NoticeService, jwtSecret string) *NoticeHandler {
	return &NoticeHandler{
		noticeService: svc,
		validate:      validator.New(),
		jwtSecret:     jwtSecret,
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

		v1.GET("/notices/by-key/:country/:newsId/:id", h.GetByID)
		v1.PUT("/notices/by-key/:country/:newsId/:id", h.Update)
		v1.DELETE("/notices/by-key/:country/:newsId/:id", h.Delete)

		v1.GET("/notices/by-country/:country/news/:newsId", h.GetByNewsID)
	}

	v2 := rg.Group("/v2.0")
	{
		v2Auth := v2.Group("")
		v2Auth.Use(auth.RequireAuth(h.jwtSecret))
		v2Auth.POST("/notices", h.Create)
		v2Auth.GET("/notices", h.GetAll)

		v2Auth.GET("/notices/:id", h.GetByID)
		v2Auth.PUT("/notices/:id", h.Update)
		v2Auth.DELETE("/notices/:id", h.Delete)

		v2Auth.GET("/notices/by-news/:newsId", h.GetByNewsID)

		v2Auth.GET("/notices/by-key/:country/:newsId/:id", h.GetByID)
		v2Auth.PUT("/notices/by-key/:country/:newsId/:id", h.Update)
		v2Auth.DELETE("/notices/by-key/:country/:newsId/:id", h.Delete)

		v2Auth.GET("/notices/by-country/:country/news/:newsId", h.GetByNewsID)
	}
}

func (h *NoticeHandler) Create(c *gin.Context) {
	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format", "40000")
		return
	}
	if err := h.validate.Struct(req); err != nil {
		respondBadRequest(c, "Validation failed: "+err.Error(), "40004")
		return
	}
	resp, err := h.noticeService.Create(c.Request.Context(), &req)
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

	notices, err := h.noticeService.GetAll(c.Request.Context(), limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notices)
}

func (h *NoticeHandler) GetByID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	notice, err := h.noticeService.GetByID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notice)
}

func (h *NoticeHandler) Update(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		respondBadRequest(c, "Invalid JSON format", "40000")
		return
	}
	if err := h.validate.Struct(req); err != nil {
		respondBadRequest(c, "Validation failed: "+err.Error(), "40004")
		return
	}

	notice, err := h.noticeService.Update(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notice)
}

func (h *NoticeHandler) Delete(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	err := h.noticeService.Delete(c.Request.Context(), id)
	if err != nil {
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

	notices, err := h.noticeService.GetByNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notices)
}

func (h *NoticeHandler) GetByLegacyID(c *gin.Context)     { h.GetByID(c) }
func (h *NoticeHandler) UpdateByLegacyID(c *gin.Context)  { h.Update(c) }
func (h *NoticeHandler) DeleteByLegacyID(c *gin.Context)  { h.Delete(c) }
func (h *NoticeHandler) GetByLegacyNewsID(c *gin.Context) { h.GetByNewsID(c) }

package controllers

import (
	"net/http"
	"news-board/publisher/internal/dto"
	"news-board/publisher/internal/service"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type NoticeHandler struct {
	noticeService *service.NoticeService
	validate      *validator.Validate
}

func NewNoticeHandler(svc *service.NoticeService) *NoticeHandler {
	return &NoticeHandler{
		noticeService: svc,
		validate:      validator.New(),
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

// Create создает новое уведомление
// @Summary Создать уведомление
// @Tags Notices
// @Accept json
// @Produce json
// @Param notice body dto.NoticeRequestTo true "Данные уведомления"
// @Success 201 {object} dto.NoticeResponseTo
// @Failure 400 {object} dto.ErrorResponse "Неверный JSON или ошибка валидации"
// @Failure 404 {object} dto.ErrorResponse "Новость не найдена"
// @Failure 500 {object} dto.ErrorResponse
// @Router /notices [post]
func (h *NoticeHandler) Create(c *gin.Context) {
	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"errorMessage": "Invalid JSON format",
			"errorCode":    "40000",
		})
		return
	}
	if err := h.validate.Struct(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"errorMessage": "Validation failed: " + err.Error(),
			"errorCode":    "40004",
		})
		return
	}
	resp, err := h.noticeService.Create(c.Request.Context(), &req)
	if err != nil {
		c.Error(err) // передаём в middleware
		return
	}
	c.JSON(http.StatusCreated, resp)
}

// GetAll возвращает список уведомлений
// @Summary Получить все уведомления
// @Tags Notices
// @Accept json
// @Produce json
// @Param limit query int false "Лимит" default(20)
// @Param offset query int false "Смещение" default(0)
// @Success 200 {array} dto.NoticeResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /notices [get]
func (h *NoticeHandler) GetAll(c *gin.Context) {
	limit, offset, ok := parsePagination(c)
	if !ok {
		return
	}
	country := c.Query("country")

	notices, err := h.noticeService.GetAll(c.Request.Context(), country, limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notices)
}

func (h *NoticeHandler) GetByLegacyID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}

	notice, err := h.noticeService.GetByLegacyID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notice)
}

// GetByID возвращает уведомление по ID
// @Summary Получить уведомление по ID
// @Tags Notices
// @Accept json
// @Produce json
// @Param id path int true "ID уведомления"
// @Success 200 {object} dto.NoticeResponseTo
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /notices/{id} [get]
func (h *NoticeHandler) GetByID(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format", "40000")
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
	notice, err := h.noticeService.GetByID(c.Request.Context(), country, newsID, id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notice)
}

// Update обновляет существующее уведомление
// @Summary Обновить уведомление
// @Tags Notices
// @Accept json
// @Produce json
// @Param id path int true "ID уведомления"
// @Param notice body dto.NoticeRequestTo true "Новые данные"
// @Success 200 {object} dto.NoticeResponseTo
// @Failure 400 {object} dto.ErrorResponse
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /notices/{id} [put]
func (h *NoticeHandler) Update(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format", "40000")
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
		c.JSON(http.StatusBadRequest, gin.H{
			"errorMessage": "Invalid JSON format",
			"errorCode":    "40000",
		})
		return
	}
	if err := h.validate.Struct(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"errorMessage": "Validation failed: " + err.Error(),
			"errorCode":    "40004",
		})
		return
	}
	notice, err := h.noticeService.Update(c.Request.Context(), country, newsID, id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notice)
}

func (h *NoticeHandler) UpdateByLegacyID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	var req dto.NoticeRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"errorMessage": "Invalid JSON format",
			"errorCode":    "40000",
		})
		return
	}
	if err := h.validate.Struct(req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"errorMessage": "Validation failed: " + err.Error(),
			"errorCode":    "40004",
		})
		return
	}
	notice, err := h.noticeService.UpdateByLegacyID(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notice)
}

// Delete удаляет уведомление по ID
// @Summary Удалить уведомление
// @Tags Notices
// @Param id path int true "ID уведомления"
// @Success 204 "Успешно удалено"
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /notices/{id} [delete]
func (h *NoticeHandler) Delete(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format", "40000")
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
	err := h.noticeService.Delete(c.Request.Context(), country, newsID, id)
	if err != nil {
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
	err := h.noticeService.DeleteByLegacyID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

// GetByNewsID возвращает уведомления по ID новости
// @Summary Получить уведомления новости
// @Tags Notices
// @Accept json
// @Produce json
// @Param newsId path int true "ID новости"
// @Success 200 {array} dto.NoticeResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /notices/by-news/{newsId} [get]
func (h *NoticeHandler) GetByNewsID(c *gin.Context) {
	country := c.Param("country")
	if country == "" {
		respondBadRequest(c, "Invalid country format", "40000")
		return
	}
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	notices, err := h.noticeService.GetByNewsID(c.Request.Context(), country, newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notices)
}

func (h *NoticeHandler) GetByLegacyNewsID(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	notices, err := h.noticeService.GetByLegacyNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, notices)
}

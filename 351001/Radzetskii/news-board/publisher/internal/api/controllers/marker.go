package controllers

import (
	"net/http"
	"news-board/publisher/internal/dto"
	"news-board/publisher/internal/service"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type MarkerHandler struct {
	markerService *service.MarkerService
	validate      *validator.Validate
}

func NewMarkerHandler(svc *service.MarkerService) *MarkerHandler {
	return &MarkerHandler{
		markerService: svc,
		validate:      validator.New(),
	}
}

func (h *MarkerHandler) RegisterRoutes(rg *gin.RouterGroup) {
	v1 := rg.Group("/v1.0")
	{
		v1.POST("/markers", h.Create)
		v1.GET("/markers", h.GetAll)
		v1.GET("/markers/:id", h.GetByID)
		v1.PUT("/markers/:id", h.Update)
		v1.DELETE("/markers/:id", h.Delete)
		v1.GET("/markers/by-news/:newsId", h.GetByNewsID)
	}
}

// Create создает новый маркер
// @Summary Создать маркер
// @Tags Markers
// @Accept json
// @Produce json
// @Param marker body dto.MarkerRequestTo true "Данные маркера"
// @Success 201 {object} dto.MarkerResponseTo
// @Failure 400 {object} dto.ErrorResponse "Неверный JSON или ошибка валидации"
// @Failure 500 {object} dto.ErrorResponse
// @Router /markers [post]
func (h *MarkerHandler) Create(c *gin.Context) {
	var req dto.MarkerRequestTo
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
			"errorCode":    "40003",
		})
		return
	}
	resp, err := h.markerService.Create(c.Request.Context(), &req)
	if err != nil {
		// внутренняя ошибка — добавляем в контекст, middleware обработает
		c.Error(err)
		return
	}
	c.JSON(http.StatusCreated, resp)
}

// GetAll возвращает список маркеров
// @Summary Получить все маркеры
// @Tags Markers
// @Accept json
// @Produce json
// @Param limit query int false "Лимит" default(20)
// @Param offset query int false "Смещение" default(0)
// @Success 200 {array} dto.MarkerResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /markers [get]
func (h *MarkerHandler) GetAll(c *gin.Context) {
	limit, offset, ok := parsePagination(c)
	if !ok {
		return
	}
	markers, err := h.markerService.GetAll(c.Request.Context(), limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, markers)
}

// GetByID возвращает маркер по ID
// @Summary Получить маркер по ID
// @Tags Markers
// @Accept json
// @Produce json
// @Param id path int true "ID маркера"
// @Success 200 {object} dto.MarkerResponseTo
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /markers/{id} [get]
func (h *MarkerHandler) GetByID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	marker, err := h.markerService.GetByID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, marker)
}

// Update обновляет существующий маркер
// @Summary Обновить маркер
// @Tags Markers
// @Accept json
// @Produce json
// @Param id path int true "ID маркера"
// @Param marker body dto.MarkerRequestTo true "Новые данные"
// @Success 200 {object} dto.MarkerResponseTo
// @Failure 400 {object} dto.ErrorResponse
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /markers/{id} [put]
func (h *MarkerHandler) Update(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	var req dto.MarkerRequestTo
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
			"errorCode":    "40003",
		})
		return
	}
	marker, err := h.markerService.Update(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, marker)
}

// Delete удаляет маркер по ID
// @Summary Удалить маркер
// @Tags Markers
// @Param id path int true "ID маркера"
// @Success 204 "Успешно удалено"
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /markers/{id} [delete]
func (h *MarkerHandler) Delete(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	err := h.markerService.Delete(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

// GetByNewsID возвращает маркеры по ID новости
// @Summary Получить маркеры новости
// @Tags Markers
// @Accept json
// @Produce json
// @Param newsId path int true "ID новости"
// @Success 200 {array} dto.MarkerResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /markers/by-news/{newsId} [get]
func (h *MarkerHandler) GetByNewsID(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	markers, err := h.markerService.GetByNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, markers)
}

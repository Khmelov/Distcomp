package controllers

import (
	"net/http"
	"strconv"

	"news-board/publisher/internal/dto"
	"news-board/publisher/internal/service"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type NewsHandler struct {
	newsService *service.NewsService
	validate    *validator.Validate
}

func NewNewsHandler(svc *service.NewsService) *NewsHandler {
	return &NewsHandler{
		newsService: svc,
		validate:    validator.New(),
	}
}

func (h *NewsHandler) RegisterRoutes(rg *gin.RouterGroup) {
	v1 := rg.Group("/v1.0")
	{
		v1.POST("/news", h.Create)
		v1.GET("/news", h.GetAll)
		v1.GET("/news/:id", h.GetByID)
		v1.PUT("/news/:id", h.Update)
		v1.DELETE("/news/:id", h.Delete)
		v1.POST("/news/:id/markers/:markerId", h.AddMarker)
		v1.DELETE("/news/:id/markers/:markerId", h.RemoveMarker)
		v1.GET("/news/:id/markers", h.GetMarkers)
		v1.GET("/news/search", h.Search)
	}
}

// Create создает новую новость
// @Summary Создать новость
// @Tags News
// @Accept json
// @Produce json
// @Param news body dto.NewsRequestTo true "Данные новости"
// @Success 201 {object} dto.NewsResponseTo
// @Failure 400 {object} dto.ErrorResponse "Неверный JSON или ошибка валидации"
// @Failure 403 {object} dto.ErrorResponse "Дубликат новости"
// @Failure 404 {object} dto.ErrorResponse "Пользователь не найден"
// @Failure 500 {object} dto.ErrorResponse
// @Router /news [post]
func (h *NewsHandler) Create(c *gin.Context) {
	var req dto.NewsRequestTo
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
			"errorCode":    "40002",
		})
		return
	}

	resp, err := h.newsService.Create(c.Request.Context(), &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusCreated, resp)
}

// GetAll возвращает список новостей
// @Summary Получить все новости
// @Tags News
// @Accept json
// @Produce json
// @Param limit query int false "Лимит" default(20)
// @Param offset query int false "Смещение" default(0)
// @Success 200 {array} dto.NewsResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /news [get]
func (h *NewsHandler) GetAll(c *gin.Context) {
	limit, offset, ok := parsePagination(c)
	if !ok {
		return
	}
	news, err := h.newsService.GetAll(c.Request.Context(), limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, news)
}

// GetByID возвращает новость по ID
// @Summary Получить новость по ID
// @Tags News
// @Accept json
// @Produce json
// @Param id path int true "ID новости"
// @Success 200 {object} dto.NewsResponseTo
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/{id} [get]
func (h *NewsHandler) GetByID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	news, err := h.newsService.GetByID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, news)
}

// Update обновляет существующую новость
// @Summary Обновить новость
// @Tags News
// @Accept json
// @Produce json
// @Param id path int true "ID новости"
// @Param news body dto.NewsRequestTo true "Новые данные"
// @Success 200 {object} dto.NewsResponseTo
// @Failure 400 {object} dto.ErrorResponse
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/{id} [put]
func (h *NewsHandler) Update(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	var req dto.NewsRequestTo
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
			"errorCode":    "40002",
		})
		return
	}
	news, err := h.newsService.Update(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, news)
}

// Delete удаляет новость по ID
// @Summary Удалить новость
// @Tags News
// @Param id path int true "ID новости"
// @Success 204 "Успешно удалено"
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/{id} [delete]
func (h *NewsHandler) Delete(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	err := h.newsService.Delete(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

// AddMarker добавляет маркер к новости
// @Summary Добавить маркер к новости
// @Tags News
// @Param id path int true "ID новости"
// @Param markerId path int true "ID маркера"
// @Success 200 "Маркер добавлен"
// @Failure 404 {object} dto.ErrorResponse "Новость или маркер не найдены"
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/{id}/marker/{markerId} [post]
func (h *NewsHandler) AddMarker(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "id", "news id")
	if !ok {
		return
	}
	markerID, ok := parseInt64Param(c, "markerId", "marker id")
	if !ok {
		return
	}
	err := h.newsService.AddMarker(c.Request.Context(), newsID, markerID)
	if err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusOK)
}

// RemoveMarker удаляет маркер из новости
// @Summary Удалить маркер из новости
// @Tags News
// @Param id path int true "ID новости"
// @Param markerId path int true "ID маркера"
// @Success 204 "Маркер удален"
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/{id}/marker/{markerId} [delete]
func (h *NewsHandler) RemoveMarker(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "id", "news id")
	if !ok {
		return
	}
	markerID, ok := parseInt64Param(c, "markerId", "marker id")
	if !ok {
		return
	}
	err := h.newsService.RemoveMarker(c.Request.Context(), newsID, markerID)
	if err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

// GetMarkers возвращает маркеры новости
// @Summary Получить маркеры новости
// @Tags News
// @Accept json
// @Produce json
// @Param id path int true "ID новости"
// @Success 200 {array} dto.MarkerResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/{id}/marker [get]
func (h *NewsHandler) GetMarkers(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "id", "news id")
	if !ok {
		return
	}
	markers, err := h.newsService.GetMarkersByNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, markers)
}

// Search выполняет поиск новостей по фильтрам
// @Summary Поиск новостей
// @Tags News
// @Accept json
// @Produce json
// @Param markerId query []int false "ID маркеров" collectionFormat(multi)
// @Param markerName query []string false "Имена маркеров" collectionFormat(multi)
// @Param userLogin query string false "Логин пользователя"
// @Param title query string false "Заголовок"
// @Param content query string false "Содержимое"
// @Param limit query int false "Лимит" default(20)
// @Param offset query int false "Смещение" default(0)
// @Success 200 {array} dto.NewsResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /news/search [get]
func (h *NewsHandler) Search(c *gin.Context) {
	filters := make(map[string]interface{})
	if markerIDs := c.QueryArray("markerId"); len(markerIDs) > 0 {
		var ids []int64
		for _, s := range markerIDs {
			id, err := strconv.ParseInt(s, 10, 64)
			if err != nil || id < 1 {
				respondBadRequest(c, "Invalid markerId query parameter", "40000")
				return
			}
			ids = append(ids, id)
		}
		if len(ids) > 0 {
			filters["marker_ids"] = ids
		}
	}
	if markerNames := c.QueryArray("markerName"); len(markerNames) > 0 {
		filters["marker_names"] = markerNames
	}
	if userLogin := c.Query("userLogin"); userLogin != "" {
		filters["user_login"] = userLogin
	}
	if title := c.Query("title"); title != "" {
		filters["title"] = title
	}
	if content := c.Query("content"); content != "" {
		filters["content"] = content
	}
	limit, offset, ok := parsePagination(c)
	if !ok {
		return
	}
	news, err := h.newsService.Search(c.Request.Context(), filters, limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, news)
}

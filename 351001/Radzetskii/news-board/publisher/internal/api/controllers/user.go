package controllers

import (
	"net/http"
	"news-board/publisher/internal/dto"
	"news-board/publisher/internal/service"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

type UserHandler struct {
	userService *service.UserService
	validate    *validator.Validate
}

func NewUserHandler(svc *service.UserService) *UserHandler {
	return &UserHandler{
		userService: svc,
		validate:    validator.New(),
	}
}

func (h *UserHandler) RegisterRoutes(rg *gin.RouterGroup) {
	v1 := rg.Group("/v1.0")
	{
		v1.POST("/users", h.Create)
		v1.GET("/users", h.GetAll)
		v1.GET("/users/:id", h.GetByID)
		v1.PUT("/users/:id", h.Update)
		v1.PUT("/users", h.UpdateWithIDFromBody)
		v1.DELETE("/users/:id", h.Delete)
		v1.GET("/users/by-news/:newsId", h.GetByNewsID)
	}
}

func (h *UserHandler) UpdateWithIDFromBody(c *gin.Context) {
	var req struct {
		ID int64 `json:"id" validate:"required,min=1"`
		dto.UserRequestTo
	}
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
			"errorCode":    "40001",
		})
		return
	}
	user, err := h.userService.Update(c.Request.Context(), req.ID, &req.UserRequestTo)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, user)
}

// Create создает нового пользователя
// @Summary Создать пользователя
// @Tags Users
// @Accept json
// @Produce json
// @Param user body dto.UserRequestTo true "Данные пользователя"
// @Success 201 {object} dto.UserResponseTo
// @Failure 400 {object} dto.ErrorResponse "Неверный JSON или ошибка валидации"
// @Failure 403 {object} dto.ErrorResponse "Логин уже существует"
// @Failure 500 {object} dto.ErrorResponse "Внутренняя ошибка"
// @Router /users [post]
func (h *UserHandler) Create(c *gin.Context) {
	var req dto.UserRequestTo
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
			"errorCode":    "40001",
		})
		return
	}
	resp, err := h.userService.Create(c.Request.Context(), &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusCreated, resp)
}

// GetAll возвращает список пользователей
// @Summary Получить всех пользователей
// @Tags Users
// @Accept json
// @Produce json
// @Param limit query int false "Лимит" default(20)
// @Param offset query int false "Смещение" default(0)
// @Success 200 {array} dto.UserResponseTo
// @Failure 500 {object} dto.ErrorResponse
// @Router /users [get]
func (h *UserHandler) GetAll(c *gin.Context) {
	limit, offset, ok := parsePagination(c)
	if !ok {
		return
	}
	users, err := h.userService.GetAll(c.Request.Context(), limit, offset)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, users)
}

// GetByID возвращает пользователя по ID
// @Summary Получить пользователя по ID
// @Tags Users
// @Accept json
// @Produce json
// @Param id path int true "ID пользователя"
// @Success 200 {object} dto.UserResponseTo
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /users/{id} [get]
func (h *UserHandler) GetByID(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	user, err := h.userService.GetByID(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, user)
}

// Update обновляет существующего пользователя
// @Summary Обновить пользователя
// @Tags Users
// @Accept json
// @Produce json
// @Param id path int true "ID пользователя"
// @Param user body dto.UserRequestTo true "Новые данные"
// @Success 200 {object} dto.UserResponseTo
// @Failure 400 {object} dto.ErrorResponse
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /users/{id} [put]
func (h *UserHandler) Update(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	var req dto.UserRequestTo
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
			"errorCode":    "40001",
		})
		return
	}
	user, err := h.userService.Update(c.Request.Context(), id, &req)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, user)
}

// Delete удаляет пользователя по ID
// @Summary Удалить пользователя
// @Tags Users
// @Param id path int true "ID пользователя"
// @Success 204 "Успешно удалено"
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /users/{id} [delete]
func (h *UserHandler) Delete(c *gin.Context) {
	id, ok := parseInt64Param(c, "id", "id")
	if !ok {
		return
	}
	err := h.userService.Delete(c.Request.Context(), id)
	if err != nil {
		c.Error(err)
		return
	}
	c.Status(http.StatusNoContent)
}

// GetByNewsID возвращает пользователя, создавшего новость
// @Summary Получить пользователя по ID новости
// @Tags Users
// @Accept json
// @Produce json
// @Param newsId path int true "ID новости"
// @Success 200 {object} dto.UserResponseTo
// @Failure 404 {object} dto.ErrorResponse
// @Failure 500 {object} dto.ErrorResponse
// @Router /users/by-news/{newsId} [get]
func (h *UserHandler) GetByNewsID(c *gin.Context) {
	newsID, ok := parseInt64Param(c, "newsId", "news id")
	if !ok {
		return
	}
	user, err := h.userService.GetByNewsID(c.Request.Context(), newsID)
	if err != nil {
		c.Error(err)
		return
	}
	c.JSON(http.StatusOK, user)
}

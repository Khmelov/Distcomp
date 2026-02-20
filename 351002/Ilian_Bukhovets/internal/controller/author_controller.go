package controller

import (
	"net/http"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
)

// AuthorController обрабатывает HTTP запросы для авторов
type AuthorController struct {
	service *service.AuthorService
	handler *ErrorHandler
}

// NewAuthorController создает новый экземпляр контроллера
func NewAuthorController(service *service.AuthorService) *AuthorController {
	return &AuthorController{
		service: service,
		handler: &ErrorHandler{},
	}
}

// Create создает нового автора
// @Summary Create author
// @Tags authors
// @Accept json
// @Produce json
// @Param author body request.AuthorRequestTo true "Author data"
// @Success 201 {object} response.AuthorResponseTo
// @Failure 400 {object} response.ErrorResponse
// @Router /api/v1.0/authors [post]
func (ctrl *AuthorController) Create(c *gin.Context) {
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.AuthorRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	author, err := ctrl.service.Create(&req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusCreated, author)
}

// GetByID получает автора по ID
// @Summary Get author by ID
// @Tags authors
// @Produce json
// @Param id path int true "Author ID"
// @Success 200 {object} response.AuthorResponseTo
// @Failure 404 {object} response.ErrorResponse
// @Router /api/v1.0/authors/{id} [get]
func (ctrl *AuthorController) GetByID(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	author, err := ctrl.service.GetByID(id)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, author)
}

// GetAll получает всех авторов
// @Summary Get all authors
// @Tags authors
// @Produce json
// @Success 200 {array} response.AuthorResponseTo
// @Router /api/v1.0/authors [get]
func (ctrl *AuthorController) GetAll(c *gin.Context) {
	authors, err := ctrl.service.GetAll()
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, authors)
}

// Update обновляет автора
// @Summary Update author
// @Tags authors
// @Accept json
// @Produce json
// @Param id path int true "Author ID"
// @Param author body request.AuthorRequestTo true "Author data"
// @Success 200 {object} response.AuthorResponseTo
// @Failure 400 {object} response.ErrorResponse
// @Failure 404 {object} response.ErrorResponse
// @Router /api/v1.0/authors/{id} [put]
func (ctrl *AuthorController) Update(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.AuthorRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	author, err := ctrl.service.Update(id, &req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, author)
}

// Delete удаляет автора
// @Summary Delete author
// @Tags authors
// @Param id path int true "Author ID"
// @Success 204
// @Failure 404 {object} response.ErrorResponse
// @Router /api/v1.0/authors/{id} [delete]
func (ctrl *AuthorController) Delete(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	err = ctrl.service.Delete(id)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.Status(http.StatusNoContent)
}


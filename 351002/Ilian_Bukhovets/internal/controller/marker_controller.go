package controller

import (
	"net/http"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
)

// MarkerController обрабатывает HTTP запросы для меток
type MarkerController struct {
	service *service.MarkerService
	handler *ErrorHandler
}

// NewMarkerController создает новый экземпляр контроллера
func NewMarkerController(service *service.MarkerService) *MarkerController {
	return &MarkerController{
		service: service,
		handler: &ErrorHandler{},
	}
}

// Create создает новую метку
func (ctrl *MarkerController) Create(c *gin.Context) {
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.MarkerRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	marker, err := ctrl.service.Create(&req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusCreated, marker)
}

// GetByID получает метку по ID
func (ctrl *MarkerController) GetByID(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	marker, err := ctrl.service.GetByID(id)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, marker)
}

// GetAll получает все метки
func (ctrl *MarkerController) GetAll(c *gin.Context) {
	markers, err := ctrl.service.GetAll()
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, markers)
}

// Update обновляет метку
func (ctrl *MarkerController) Update(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.MarkerRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	marker, err := ctrl.service.Update(id, &req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, marker)
}

// Delete удаляет метку
func (ctrl *MarkerController) Delete(c *gin.Context) {
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


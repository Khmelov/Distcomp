package controller

import (
	"net/http"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
)

// NoteController обрабатывает HTTP запросы для заметок
type NoteController struct {
	service *service.NoteService
	handler *ErrorHandler
}

// NewNoteController создает новый экземпляр контроллера
func NewNoteController(service *service.NoteService) *NoteController {
	return &NoteController{
		service: service,
		handler: &ErrorHandler{},
	}
}

// Create создает новую заметку
func (ctrl *NoteController) Create(c *gin.Context) {
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.NoteRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	note, err := ctrl.service.Create(&req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusCreated, note)
}

// GetByID получает заметку по ID
func (ctrl *NoteController) GetByID(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	note, err := ctrl.service.GetByID(id)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, note)
}

// GetAll получает все заметки
func (ctrl *NoteController) GetAll(c *gin.Context) {
	notes, err := ctrl.service.GetAll()
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, notes)
}

// Update обновляет заметку
func (ctrl *NoteController) Update(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.NoteRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	note, err := ctrl.service.Update(id, &req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, note)
}

// Delete удаляет заметку
func (ctrl *NoteController) Delete(c *gin.Context) {
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

// GetByIssueID получает заметки по ID задачи
func (ctrl *NoteController) GetByIssueID(c *gin.Context) {
	issueID, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	notes, err := ctrl.service.GetByIssueID(issueID)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, notes)
}


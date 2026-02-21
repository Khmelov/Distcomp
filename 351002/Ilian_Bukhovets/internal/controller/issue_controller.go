package controller

import (
	"net/http"

	"github.com/bsuir/rest-api/internal/dto/request"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
)

// IssueController обрабатывает HTTP запросы для задач
type IssueController struct {
	service *service.IssueService
	handler *ErrorHandler
}

// NewIssueController создает новый экземпляр контроллера
func NewIssueController(service *service.IssueService) *IssueController {
	return &IssueController{
		service: service,
		handler: &ErrorHandler{},
	}
}

// Create создает новую задачу
func (ctrl *IssueController) Create(c *gin.Context) {
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.IssueRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	issue, err := ctrl.service.Create(&req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusCreated, issue)
}

// GetByID получает задачу по ID
func (ctrl *IssueController) GetByID(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	issue, err := ctrl.service.GetByID(id)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, issue)
}

// GetAll получает все задачи
func (ctrl *IssueController) GetAll(c *gin.Context) {
	issues, err := ctrl.service.GetAll()
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, issues)
}

// Update обновляет задачу
func (ctrl *IssueController) Update(c *gin.Context) {
	id, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	if err := ValidateNoIDInBody(c); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	var req request.IssueRequestTo
	if err := c.ShouldBindJSON(&req); err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	issue, err := ctrl.service.Update(id, &req)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, issue)
}

// Delete удаляет задачу
func (ctrl *IssueController) Delete(c *gin.Context) {
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

// GetAuthorByIssueID получает автора по ID задачи
func (ctrl *IssueController) GetAuthorByIssueID(c *gin.Context) {
	issueID, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	author, err := ctrl.service.GetAuthorByIssueID(issueID)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, author)
}

// GetMarkersByIssueID получает метки по ID задачи
func (ctrl *IssueController) GetMarkersByIssueID(c *gin.Context) {
	issueID, err := ParseID(c)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}
	markers, err := ctrl.service.GetMarkersByIssueID(issueID)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, markers)
}

// GetByFilters получает задачи по фильтрам
func (ctrl *IssueController) GetByFilters(c *gin.Context) {
	markerNames := c.QueryArray("markerName")
	markerIDStrs := c.QueryArray("markerId")
	authorLogin := c.Query("authorLogin")
	title := c.Query("title")
	content := c.Query("content")

	markerIDs, err := ParseInt64Array(markerIDStrs)
	if err != nil {
		ctrl.handler.HandleValidationError(c, err)
		return
	}

	issues, err := ctrl.service.GetByFilters(markerNames, markerIDs, authorLogin, title, content)
	if err != nil {
		ctrl.handler.HandleError(c, err)
		return
	}

	c.JSON(http.StatusOK, issues)
}


package controller

import (
	"net/http"
	"strings"

	"github.com/bsuir/rest-api/internal/dto/response"
	"github.com/bsuir/rest-api/internal/repository/inmemory"
	"github.com/bsuir/rest-api/internal/service"
	"github.com/gin-gonic/gin"
)

// ErrorHandler обрабатывает ошибки приложения
type ErrorHandler struct{}

// HandleError обрабатывает ошибки и возвращает соответствующий HTTP ответ
func (h *ErrorHandler) HandleError(c *gin.Context, err error) {
	if err == nil {
		return
	}

	var statusCode int
	var errorCode string
	var errorMessage string

	errorMessage = err.Error()

	// Проверяем ошибки по тексту сообщения, так как ошибки могут быть из разных пакетов
	if err == inmemory.ErrAuthorNotFound || err == service.ErrAuthorNotFound || strings.Contains(errorMessage, "author not found") {
		statusCode = http.StatusNotFound
		errorCode = "40401"
		errorMessage = "Author not found"
	} else if err == inmemory.ErrIssueNotFound || err == service.ErrIssueNotFound || strings.Contains(errorMessage, "issue not found") {
		statusCode = http.StatusNotFound
		errorCode = "40402"
		errorMessage = "Issue not found"
	} else if err == inmemory.ErrMarkerNotFound || err == service.ErrMarkerNotFound || strings.Contains(errorMessage, "marker not found") {
		statusCode = http.StatusNotFound
		errorCode = "40403"
		errorMessage = "Marker not found"
	} else if err == inmemory.ErrNoteNotFound || err == service.ErrNoteNotFound || strings.Contains(errorMessage, "note not found") {
		statusCode = http.StatusNotFound
		errorCode = "40404"
		errorMessage = "Note not found"
	} else if strings.Contains(errorMessage, "required") || strings.Contains(errorMessage, "must be between") {
		statusCode = http.StatusBadRequest
		errorCode = "40001"
	} else if strings.Contains(errorMessage, "invalid") {
		statusCode = http.StatusBadRequest
		errorCode = "40002"
	} else {
		statusCode = http.StatusInternalServerError
		errorCode = "50001"
	}

	c.JSON(statusCode, response.ErrorResponse{
		ErrorMessage: errorMessage,
		ErrorCode:    errorCode,
	})
}

// HandleValidationError обрабатывает ошибки валидации
func (h *ErrorHandler) HandleValidationError(c *gin.Context, err error) {
	c.JSON(http.StatusBadRequest, response.ErrorResponse{
		ErrorMessage: err.Error(),
		ErrorCode:    "40003",
	})
}


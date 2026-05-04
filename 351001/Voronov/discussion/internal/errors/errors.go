package errors

import (
	"fmt"
	"net/http"
)

type AppError struct {
	Code       int    `json:"errorCode"`
	Message    string `json:"errorMessage"`
	HTTPStatus int    `json:"-"`
}

func (e *AppError) Error() string {
	return fmt.Sprintf("%d - %s", e.Code, e.Message)
}

func NewAppError(httpStatus, code int, message string) *AppError {
	return &AppError{Code: code, Message: message, HTTPStatus: httpStatus}
}

var (
	ErrNotFound   = NewAppError(http.StatusNotFound, 40401, "Resource not found")
	ErrBadRequest = NewAppError(http.StatusBadRequest, 40001, "Invalid request")
	ErrDuplicate  = NewAppError(http.StatusForbidden, 40301, "Duplicate value")
	ErrInternal   = NewAppError(http.StatusInternalServerError, 50001, "Internal server error")
)

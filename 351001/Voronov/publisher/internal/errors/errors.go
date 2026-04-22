package errors

import (
	stderrors "errors"
	"fmt"
	"net/http"

	"github.com/jackc/pgx/v5/pgconn"
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

func FromDBError(err error) *AppError {
	var appErr *AppError
	if stderrors.As(err, &appErr) {
		return appErr
	}
	var pgErr *pgconn.PgError
	if stderrors.As(err, &pgErr) {
		switch pgErr.Code {
		case "23505":
			return ErrDuplicate
		case "23503":
			return ErrDuplicate
		case "23502":
			return ErrBadRequest
		}
	}
	return ErrInternal
}

var (
	ErrNotFound   = NewAppError(http.StatusNotFound, 40401, "Resource not found")
	ErrBadRequest = NewAppError(http.StatusBadRequest, 40001, "Invalid request")
	ErrForbidden  = NewAppError(http.StatusForbidden, 40301, "Forbidden")
	ErrDuplicate  = NewAppError(http.StatusForbidden, 40301, "Duplicate value")
	ErrInternal   = NewAppError(http.StatusInternalServerError, 50001, "Internal server error")
)

package httperrors

import (
	"net/http"

	"github.com/Khmelov/Distcomp/251004/Sazonov/pkg/logger"
	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
	"go.uber.org/zap"
)

type HTTPError struct {
	Message string `json:"message,omitempty"`
}

func Error(c *gin.Context, err error) {
	logger.Error(c, "handle error", zap.Error(err))

	switch {
	case errors.Is(err, errors.ErrNotFound):
		c.JSON(http.StatusNotFound, HTTPError{Message: err.Error()})

	case errors.Is(err, errors.ErrAlreadyExists):
		c.JSON(http.StatusConflict, HTTPError{Message: err.Error()})

	case errors.Is(err, errors.ErrBadRequest):
		c.JSON(http.StatusBadRequest, HTTPError{Message: err.Error()})

	case errors.Is(err, errors.ErrForbidden):
		c.JSON(http.StatusForbidden, HTTPError{Message: err.Error()})

	case errors.Is(err, errors.ErrAlreadyExists):
		c.JSON(http.StatusConflict, HTTPError{Message: err.Error()})

	case errors.Is(err, errors.ErrInternalServerError):
		c.JSON(http.StatusInternalServerError, HTTPError{Message: err.Error()})

	default:
		c.JSON(http.StatusInternalServerError, HTTPError{Message: err.Error()})
	}
}

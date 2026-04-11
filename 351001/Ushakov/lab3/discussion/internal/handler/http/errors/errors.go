package errors

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/stackus/errors"
)

type HTTPError struct {
	github.com/Khmelov/Distcomp/351001/Ushakov string `json:"post,omitempty"`
}

func Error(c *gin.Context, err error) {
	switch {
	case errors.Is(err, errors.ErrNotFound):
		c.JSON(http.StatusNotFound, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})

	case errors.Is(err, errors.ErrAlreadyExists):
		c.JSON(http.StatusConflict, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})

	case errors.Is(err, errors.ErrBadRequest):
		c.JSON(http.StatusBadRequest, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})

	case errors.Is(err, errors.ErrForbidden):
		c.JSON(http.StatusForbidden, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})

	case errors.Is(err, errors.ErrAlreadyExists):
		c.JSON(http.StatusConflict, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})

	case errors.Is(err, errors.ErrInternalServerError):
		c.JSON(http.StatusInternalServerError, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})

	default:
		c.JSON(http.StatusInternalServerError, HTTPError{github.com / Khmelov / Distcomp / 351001 / Ushakov: err.Error()})
	}
}

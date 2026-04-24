package errors

import (
	"errors"
	"log"
	"net/http"
	"news-board/publisher/internal/domain"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

var Validate = validator.New()

type APIError struct {
	ErrorMessage string `json:"errorMessage"`
	ErrorCode    string `json:"errorCode"`
}

func ErrorHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()
		if len(c.Errors) > 0 {
			err := c.Errors.Last().Err
			var status int
			var code string
			var message string

			switch {
			case errors.Is(err, domain.ErrUserNotFound):
				status = http.StatusNotFound
				code = "40401"
				message = "User not found"
			case errors.Is(err, domain.ErrUserLoginNotUnique):
				status = http.StatusForbidden
				code = "40301"
				message = "User login already exists"
			case errors.Is(err, domain.ErrNewsNotFound):
				status = http.StatusNotFound
				code = "40402"
				message = "News not found"
			case errors.Is(err, domain.ErrNewsUserNotFound):
				status = http.StatusNotFound
				code = "40402"
				message = "User for news not found"
			case errors.Is(err, domain.ErrNewsDuplicate):
				status = http.StatusForbidden
				code = "40302"
				message = "News duplicate"
			case errors.Is(err, domain.ErrMarkerNotFound):
				status = http.StatusNotFound
				code = "40403"
				message = "Marker not found"
			case errors.Is(err, domain.ErrMarkerAlreadyExists):
				status = http.StatusForbidden
				code = "40303"
				message = "Marker already exists"
			case errors.Is(err, domain.ErrNewsMarkerDuplicate):
				status = http.StatusForbidden
				code = "40304"
				message = "Marker is already attached to news"
			case errors.Is(err, domain.ErrNoticeNotFound):
				status = http.StatusNotFound
				code = "40404"
				message = "Notice not found"
			default:
				log.Printf("Unhandled error: %v", err)
				status = http.StatusInternalServerError
				code = "50000"
				message = "Internal server error"
			}

			c.AbortWithStatusJSON(status, gin.H{
				"errorMessage": message,
				"errorCode":    code,
			})
		}
	}
}

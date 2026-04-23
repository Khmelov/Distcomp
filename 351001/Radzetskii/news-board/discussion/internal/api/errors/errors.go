package errors

import (
	"errors"
	"log"
	"net/http"
	"news-board/discussion/internal/domain"

	"github.com/gin-gonic/gin"
)

func ErrorHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()
		if len(c.Errors) == 0 {
			return
		}

		err := c.Errors.Last().Err
		switch {
		case errors.Is(err, domain.ErrNoticeNotFound):
			c.AbortWithStatusJSON(http.StatusNotFound, gin.H{
				"errorMessage": "Notice not found",
				"errorCode":    "40404",
			})
		default:
			log.Printf("Unhandled discussion error: %v", err)
			c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
				"errorMessage": "Internal server error",
				"errorCode":    "50000",
			})
		}
	}
}

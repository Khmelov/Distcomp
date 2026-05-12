package response

import "github.com/gin-gonic/gin"

type ApiError struct {
	ErrorCode    string `json:"errorCode"`
	ErrorMessage string `json:"errorMessage"`
}

func SendError(c *gin.Context, status int, customCode string, message string) {
	c.JSON(status, ApiError{
		ErrorCode:    customCode,
		ErrorMessage: message,
	})
	c.Abort()
}

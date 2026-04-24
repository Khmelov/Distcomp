package middleware

import (
	"bytes"
	"io"
	"log"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
)

func RequestLogger() gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		path := c.Request.URL.Path
		raw := c.Request.URL.RawQuery
		method := c.Request.Method
		clientIP := c.ClientIP()

		var requestBody string
		if method == "POST" || method == "PUT" {
			if c.Request.ContentLength > 0 && c.Request.ContentLength < 1024 {
				body, err := io.ReadAll(c.Request.Body)
				if err == nil {
					requestBody = string(body)
					c.Request.Body = io.NopCloser(bytes.NewBuffer(body))
				}
			}
		}

		c.Next()

		latency := time.Since(start)
		statusCode := c.Writer.Status()

		if raw != "" {
			path = path + "?" + raw
		}

		log.Printf("API Request | %s | %s | %d | %v | %s",
			method,
			path,
			statusCode,
			latency,
			clientIP,
		)

		if requestBody != "" {
			log.Printf("Request Body: %s", strings.TrimSpace(requestBody))
		}
	}
}

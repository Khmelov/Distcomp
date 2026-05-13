package middleware

import (
	"publisher/internal/service"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		header := c.GetHeader("Authorization")
		if header == "" || !strings.HasPrefix(header, "Bearer ") {
			c.JSON(401, gin.H{"errorCode": "40101", "errorMessage": "Unauthorized"})
			c.Abort()
			return
		}

		tokenStr := strings.TrimPrefix(header, "Bearer ")
		token, err := jwt.Parse(tokenStr, func(t *jwt.Token) (interface{}, error) {
			return service.JwtSecret, nil
		})

		if err != nil || !token.Valid {
			c.JSON(401, gin.H{"errorCode": "40102", "errorMessage": "Invalid token"})
			c.Abort()
			return
		}

		claims := token.Claims.(jwt.MapClaims)
		c.Set("user_login", claims["sub"])
		c.Set("user_role", claims["role"])
		c.Next()
	}
}

func RoleMiddleware(requiredRole string) gin.HandlerFunc {
	return func(c *gin.Context) {
		role, _ := c.Get("user_role")
		if role != requiredRole && role != "ADMIN" {
			c.JSON(403, gin.H{"errorCode": "40302", "errorMessage": "Forbidden: insufficient permissions"})
			c.Abort()
			return
		}
		c.Next()
	}
}

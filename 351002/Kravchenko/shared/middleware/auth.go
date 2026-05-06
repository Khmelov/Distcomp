package middleware

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

const (
	authorizationHeader = "Authorization"
	userContextKey      = "userLogin"
	roleContextKey      = "userRole"
)

func AuthMiddleware(jwtSecret string) gin.HandlerFunc {
	return func(c *gin.Context) {
		header := c.GetHeader(authorizationHeader)
		if header == "" {
			abortWithJSON(c, http.StatusUnauthorized, "Authorization header is empty", "40101")
			return
		}

		headerParts := strings.Split(header, " ")
		if len(headerParts) != 2 || headerParts[0] != "Bearer" {
			abortWithJSON(c, http.StatusUnauthorized, "Invalid auth header format", "40102")
			return
		}

		accessToken := headerParts[1]
		claims := jwt.MapClaims{}

		token, err := jwt.ParseWithClaims(accessToken, claims, func(token *jwt.Token) (interface{}, error) {
			return []byte(jwtSecret), nil
		})

		if err != nil || !token.Valid {
			abortWithJSON(c, http.StatusUnauthorized, "Invalid or expired token", "40103")
			return
		}

		login, okLogin := claims["sub"].(string)
		role, okRole := claims["role"].(string)

		if !okLogin || !okRole {
			abortWithJSON(c, http.StatusUnauthorized, "Token payload is invalid", "40104")
			return
		}

		c.Set(userContextKey, login)
		c.Set(roleContextKey, role)

		c.Next()
	}
}

func RoleMiddleware(requiredRole string) gin.HandlerFunc {
	return func(c *gin.Context) {
		role, exists := c.Get(roleContextKey)
		if !exists || role.(string) != requiredRole {
			abortWithJSON(c, http.StatusForbidden, "Access denied: insufficient permissions", "40301")
			return
		}
		c.Next()
	}
}

func abortWithJSON(c *gin.Context, status int, message, code string) {
	c.AbortWithStatusJSON(status, gin.H{
		"errorMessage": message,
		"errorCode":    code,
	})
}

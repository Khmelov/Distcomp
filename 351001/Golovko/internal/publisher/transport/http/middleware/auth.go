package middleware

import (
	"net/http"
	"strings"

	"distcomp/internal/apperrors"
	"distcomp/pkg/auth"

	"github.com/gin-gonic/gin"
)

const (
	HeaderAuthorization = "Authorization"
	CtxEditorID         = "editorId"
	CtxEditorRole       = "editorRole"
)

func Auth() gin.HandlerFunc {
	return func(c *gin.Context) {
		header := c.GetHeader(HeaderAuthorization)
		if header == "" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, apperrors.New("empty auth header", 40101))
			return
		}

		headerParts := strings.Split(header, " ")
		if len(headerParts) != 2 || headerParts[0] != "Bearer" {
			c.AbortWithStatusJSON(http.StatusUnauthorized, apperrors.New("invalid auth header", 40102))
			return
		}

		token := headerParts[1]
		claims, err := auth.ValidateToken(token)
		if err != nil {
			c.AbortWithStatusJSON(http.StatusUnauthorized, apperrors.New("invalid or expired token", 40103))
			return
		}

		c.Set(CtxEditorID, claims.EditorID)
		c.Set(CtxEditorRole, claims.Role)

		c.Next()
	}
}
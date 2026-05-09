package auth

import (
	"fmt"
	"net/http"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

const (
	RoleAdmin    = "ADMIN"
	RoleCustomer = "CUSTOMER"

	ContextUserLoginKey = "userLogin"
	ContextUserRoleKey  = "userRole"
)

type Claims struct {
	Role string `json:"role"`
	jwt.RegisteredClaims
}

func GenerateToken(secret, login, role string, ttl time.Duration) (string, error) {
	claims := Claims{
		Role: role,
		RegisteredClaims: jwt.RegisteredClaims{
			Subject:   login,
			IssuedAt:  jwt.NewNumericDate(time.Now().UTC()),
			ExpiresAt: jwt.NewNumericDate(time.Now().UTC().Add(ttl)),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString([]byte(secret))
}

func ParseToken(secret, tokenString string) (*Claims, error) {
	if tokenString == "" {
		return nil, fmt.Errorf("token is required")
	}
	parser := jwt.NewParser()
	claims := &Claims{}
	_, err := parser.ParseWithClaims(tokenString, claims, func(token *jwt.Token) (interface{}, error) {
		if token.Method != jwt.SigningMethodHS256 {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(secret), nil
	})
	if err != nil {
		return nil, err
	}
	if claims.ExpiresAt == nil || time.Now().UTC().After(claims.ExpiresAt.Time) {
		return nil, fmt.Errorf("token is expired")
	}
	if claims.Subject == "" || claims.Role == "" {
		return nil, fmt.Errorf("token claims are invalid")
	}
	return claims, nil
}

func RequireAuth(secret string) gin.HandlerFunc {
	return func(c *gin.Context) {
		authorization := c.GetHeader("Authorization")
		if authorization == "" || !strings.HasPrefix(strings.TrimSpace(authorization), "Bearer ") {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{
				"errorMessage": "Authorization header missing or invalid",
				"errorCode":    "40101",
			})
			return
		}

		tokenString := strings.TrimSpace(strings.TrimPrefix(authorization, "Bearer "))
		claims, err := ParseToken(secret, tokenString)
		if err != nil {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{
				"errorMessage": "Invalid or expired token",
				"errorCode":    "40102",
			})
			return
		}

		c.Set(ContextUserLoginKey, claims.Subject)
		c.Set(ContextUserRoleKey, claims.Role)
		c.Next()
	}
}

func GetCurrentUserLogin(c *gin.Context) (string, bool) {
	login, ok := c.Get(ContextUserLoginKey)
	if !ok {
		return "", false
	}
	loginStr, ok := login.(string)
	return loginStr, ok
}

func GetCurrentUserRole(c *gin.Context) (string, bool) {
	role, ok := c.Get(ContextUserRoleKey)
	if !ok {
		return "", false
	}
	roleStr, ok := role.(string)
	return roleStr, ok
}

func RequireRole(role string) gin.HandlerFunc {
	return func(c *gin.Context) {
		currentRole, ok := GetCurrentUserRole(c)
		if !ok || currentRole != role {
			c.AbortWithStatusJSON(http.StatusForbidden, gin.H{
				"errorMessage": "Forbidden",
				"errorCode":    "40301",
			})
			return
		}
		c.Next()
	}
}

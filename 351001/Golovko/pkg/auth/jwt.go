package auth

import (
	"errors"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

var secretKey = []byte("super-secret-key-for-distcomp-lab")

type Claims struct {
	EditorID int64  `json:"editorId"`
	Role     string `json:"role"`
	jwt.RegisteredClaims
}

func GenerateToken(editorID int64, login string, role string) (string, error) {
	if role == "" {
		role = "CUSTOMER"
	}

	claims := Claims{
		EditorID: editorID,
		Role:     role,
		RegisteredClaims: jwt.RegisteredClaims{
			Subject:   login,
			IssuedAt:  jwt.NewNumericDate(time.Now()),
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(24 * time.Hour)),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(secretKey)
}

func ValidateToken(tokenString string) (*Claims, error) {
	token, err := jwt.ParseWithClaims(tokenString, &Claims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, errors.New("unexpected signing method")
		}
		return secretKey, nil
	})

	if err != nil {
		return nil, err
	}

	if claims, ok := token.Claims.(*Claims); ok && token.Valid {
		return claims, nil
	}

	return nil, errors.New("invalid token")
}

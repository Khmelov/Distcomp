package auth

import (
	"fmt"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

// AccessClaims — JWT по Task361: sub (login), iat, exp, role.
type AccessClaims struct {
	Role string `json:"role"`
	jwt.RegisteredClaims
}

type JWTService struct {
	secret []byte
	ttl    time.Duration
}

func NewJWTService(secret string, expiryMinutes int) *JWTService {
	if expiryMinutes <= 0 {
		expiryMinutes = 120
	}
	return &JWTService{
		secret: []byte(secret),
		ttl:    time.Duration(expiryMinutes) * time.Minute,
	}
}

func (s *JWTService) Mint(login, role string) (string, error) {
	now := time.Now()
	claims := AccessClaims{
		Role: role,
		RegisteredClaims: jwt.RegisteredClaims{
			Subject:   login,
			IssuedAt:  jwt.NewNumericDate(now),
			ExpiresAt: jwt.NewNumericDate(now.Add(s.ttl)),
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, &claims)
	return token.SignedString(s.secret)
}

func (s *JWTService) Parse(raw string) (*AccessClaims, error) {
	token, err := jwt.ParseWithClaims(raw, &AccessClaims{}, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method")
		}
		return s.secret, nil
	})
	if err != nil {
		return nil, err
	}
	claims, ok := token.Claims.(*AccessClaims)
	if !ok || !token.Valid {
		return nil, fmt.Errorf("invalid token claims")
	}
	return claims, nil
}

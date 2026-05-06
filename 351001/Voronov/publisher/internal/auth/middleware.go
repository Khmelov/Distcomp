package auth

import (
	"context"
	"encoding/json"
	"net/http"
	"strings"

	"publisher/internal/model"
)

type contextKey string

const (
	ContextKeyLogin contextKey = "login"
	ContextKeyRole  contextKey = "role"
)

type authErrorBody struct {
	ErrorMessage string `json:"errorMessage"`
	ErrorCode    int    `json:"errorCode"`
}

func RequireAuth(jwtSvc *JWTService, next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		authHeader := r.Header.Get("Authorization")
		if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
			writeAuthError(w, http.StatusUnauthorized, "missing or invalid Authorization header", 40101)
			return
		}
		tokenStr := strings.TrimPrefix(authHeader, "Bearer ")
		claims, err := jwtSvc.ValidateToken(tokenStr)
		if err != nil {
			writeAuthError(w, http.StatusUnauthorized, "invalid or expired token", 40102)
			return
		}
		ctx := context.WithValue(r.Context(), ContextKeyLogin, claims.Subject)
		ctx = context.WithValue(ctx, ContextKeyRole, claims.Role)
		next.ServeHTTP(w, r.WithContext(ctx))
	})
}

func RequireRole(roles ...model.UserRole) func(http.Handler) http.Handler {
	return func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			role, ok := r.Context().Value(ContextKeyRole).(model.UserRole)
			if !ok {
				writeAuthError(w, http.StatusForbidden, "forbidden", 40301)
				return
			}
			for _, allowed := range roles {
				if role == allowed {
					next.ServeHTTP(w, r)
					return
				}
			}
			writeAuthError(w, http.StatusForbidden, "insufficient permissions", 40302)
		})
	}
}

func LoginFromContext(ctx context.Context) string {
	v, _ := ctx.Value(ContextKeyLogin).(string)
	return v
}

func RoleFromContext(ctx context.Context) model.UserRole {
	v, _ := ctx.Value(ContextKeyRole).(model.UserRole)
	return v
}

func writeAuthError(w http.ResponseWriter, status int, msg string, code int) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(authErrorBody{ErrorMessage: msg, ErrorCode: code})
}

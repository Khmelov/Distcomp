package resolver

import (
	"encoding/json"
	"gridusko_rest/internal/services/comment"
	"net/http"
)

type Handler struct {
	commentService *comment.Service
}

func NewHandler(
	commentService *comment.Service,
) *Handler {
	return &Handler{
		commentService: commentService,
	}
}

func writeJSONResponse(w http.ResponseWriter, statusCode int, data interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(statusCode)
	json.NewEncoder(w).Encode(data)
}

func writeErrorResponse(w http.ResponseWriter, statusCode int, message string) {
	writeJSONResponse(w, statusCode, map[string]string{"error": message})
}

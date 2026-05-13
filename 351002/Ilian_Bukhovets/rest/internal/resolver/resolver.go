package resolver

import (
	"encoding/json"
	"gridusko_rest/internal/services/author"
	"gridusko_rest/internal/services/comment"
	"gridusko_rest/internal/services/issue"
	"gridusko_rest/internal/services/marker"
	"net/http"
)

type Handler struct {
	authorService  *author.Service
	commentService *comment.Service
	issueService   *issue.Service
	markerService  *marker.Service
}

func NewHandler(
	authorService *author.Service,
	commentService *comment.Service,
	issueService *issue.Service,
	markerService *marker.Service,
) *Handler {
	return &Handler{
		authorService:  authorService,
		commentService: commentService,
		issueService:   issueService,
		markerService:  markerService,
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

package handler

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/service"
)

type Handler struct {
	userService     service.UserService
	issueService    service.IssueService
	labelService    service.LabelService
	reactionService service.ReactionService
}

func NewHandler(
	userService service.UserService,
	issueService service.IssueService,
	labelService service.LabelService,
	reactionService service.ReactionService,
) *Handler {
	return &Handler{
		userService:     userService,
		issueService:    issueService,
		labelService:    labelService,
		reactionService: reactionService,
	}
}

func (h *Handler) RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/api/v1.0/", h.handleAll)
}

func (h *Handler) handleAll(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path
	switch {
	case strings.HasPrefix(path, "/api/v1.0/users"):
		h.handleUsers(w, r, path)
	case strings.HasPrefix(path, "/api/v1.0/issues"):
		h.handleIssues(w, r, path)
	case strings.HasPrefix(path, "/api/v1.0/labels"):
		h.handleLabels(w, r, path)
	case strings.HasPrefix(path, "/api/v1.0/reactions"):
		h.handleReactions(w, r, path)
	default:
		h.writeError(w, apperrors.ErrNotFound)
	}
}

func (h *Handler) writeJSON(w http.ResponseWriter, status int, data interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(data)
}

func (h *Handler) writeError(w http.ResponseWriter, err error) {
	var appErr *apperrors.AppError
	if errors.As(err, &appErr) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(appErr.HTTPStatus)
		json.NewEncoder(w).Encode(appErr)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusInternalServerError)
	json.NewEncoder(w).Encode(apperrors.ErrInternal)
}

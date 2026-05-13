package v2

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"

	"publisher/internal/auth"
	apperrors "publisher/internal/errors"
	"publisher/internal/model"
	"publisher/internal/service"
)

type HandlerV2 struct {
	userService     service.UserService
	issueService    service.IssueService
	labelService    service.LabelService
	reactionService service.ReactionService
	authService     *auth.AuthService
	jwtSvc          *auth.JWTService
}

func NewHandlerV2(
	userService service.UserService,
	issueService service.IssueService,
	labelService service.LabelService,
	reactionService service.ReactionService,
	authService *auth.AuthService,
	jwtSvc *auth.JWTService,
) *HandlerV2 {
	return &HandlerV2{
		userService:     userService,
		issueService:    issueService,
		labelService:    labelService,
		reactionService: reactionService,
		authService:     authService,
		jwtSvc:          jwtSvc,
	}
}

func (h *HandlerV2) RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/api/v2.0/login", h.handleLogin)
	mux.HandleFunc("/api/v2.0/users", h.handleUsersPublicOrProtected)
	mux.HandleFunc("/api/v2.0/users/", h.handleUsersProtected)
	protected := auth.RequireAuth(h.jwtSvc, http.HandlerFunc(h.handleProtected))
	mux.Handle("/api/v2.0/", protected)
}

func (h *HandlerV2) handleUsersPublicOrProtected(w http.ResponseWriter, r *http.Request) {
	if r.Method == http.MethodPost {
		h.registerUser(w, r)
		return
	}
	auth.RequireAuth(h.jwtSvc, http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		h.handleUsersAuth(w, r, r.URL.Path)
	})).ServeHTTP(w, r)
}

func (h *HandlerV2) handleUsersProtected(w http.ResponseWriter, r *http.Request) {
	auth.RequireAuth(h.jwtSvc, http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		h.handleUsersAuth(w, r, r.URL.Path)
	})).ServeHTTP(w, r)
}

func (h *HandlerV2) handleProtected(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path
	switch {
	case strings.HasPrefix(path, "/api/v2.0/issues"):
		h.handleIssues(w, r, path)
	case strings.HasPrefix(path, "/api/v2.0/labels"):
		h.handleLabels(w, r, path)
	case strings.HasPrefix(path, "/api/v2.0/reactions"):
		h.handleReactions(w, r, path)
	default:
		h.writeError(w, apperrors.ErrNotFound)
	}
}

func (h *HandlerV2) handleUsersAuth(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v2.0/users/") {
		idStr := strings.TrimPrefix(path, "/api/v2.0/users/")
		if !strings.Contains(idStr, "/") {
			id, err := parseID(idStr)
			if err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			switch r.Method {
			case http.MethodGet:
				h.getUser(w, r, id)
			case http.MethodPut:
				h.updateUser(w, r, id)
			case http.MethodDelete:
				role := auth.RoleFromContext(r.Context())
				if role != model.RoleAdmin {
					login := auth.LoginFromContext(r.Context())
					user, err := h.userService.FindByID(r.Context(), id)
					if err != nil || user.Login != login {
						h.writeError(w, apperrors.ErrForbidden)
						return
					}
				}
				h.deleteUser(w, r, id)
			default:
				h.writeError(w, apperrors.ErrNotFound)
			}
			return
		}
	}

	if path == "/api/v2.0/users" {
		switch r.Method {
		case http.MethodGet:
			h.getUsers(w, r)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

func (h *HandlerV2) writeJSON(w http.ResponseWriter, status int, data interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	json.NewEncoder(w).Encode(data)
}

func (h *HandlerV2) writeError(w http.ResponseWriter, err error) {
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

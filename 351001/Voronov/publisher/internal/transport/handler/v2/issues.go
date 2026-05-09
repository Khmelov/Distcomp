package v2

import (
	"encoding/json"
	"net/http"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/model"
	"publisher/internal/transport/dto/request"

	"publisher/internal/auth"
)

func (h *HandlerV2) handleIssues(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v2.0/issues/") {
		idStr := strings.TrimPrefix(path, "/api/v2.0/issues/")
		if !strings.Contains(idStr, "/") {
			id, err := parseID(idStr)
			if err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			switch r.Method {
			case http.MethodGet:
				issue, err := h.issueService.FindByID(r.Context(), id)
				if err != nil {
					h.writeError(w, err)
					return
				}
				h.writeJSON(w, http.StatusOK, issue)
			case http.MethodPut:
				var req request.IssueRequestTo
				if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
					h.writeError(w, apperrors.ErrBadRequest)
					return
				}
				issue, err := h.issueService.Update(r.Context(), id, &req)
				if err != nil {
					h.writeError(w, err)
					return
				}
				h.writeJSON(w, http.StatusOK, issue)
			case http.MethodDelete:
				role := auth.RoleFromContext(r.Context())
				if role != model.RoleAdmin {
					h.writeError(w, apperrors.ErrForbidden)
					return
				}
				if err := h.issueService.Delete(r.Context(), id); err != nil {
					h.writeError(w, err)
					return
				}
				w.WriteHeader(http.StatusNoContent)
			default:
				h.writeError(w, apperrors.ErrNotFound)
			}
			return
		}
	}

	if path == "/api/v2.0/issues" {
		switch r.Method {
		case http.MethodGet:
			issues, err := h.issueService.FindAll(r.Context())
			if err != nil {
				h.writeError(w, err)
				return
			}
			h.writeJSON(w, http.StatusOK, issues)
		case http.MethodPost:
			var req request.IssueRequestTo
			if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			issue, err := h.issueService.Create(r.Context(), &req)
			if err != nil {
				h.writeError(w, err)
				return
			}
			h.writeJSON(w, http.StatusCreated, issue)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

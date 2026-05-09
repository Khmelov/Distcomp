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

func (h *HandlerV2) handleLabels(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v2.0/labels/") {
		idStr := strings.TrimPrefix(path, "/api/v2.0/labels/")
		if !strings.Contains(idStr, "/") {
			id, err := parseID(idStr)
			if err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			switch r.Method {
			case http.MethodGet:
				label, err := h.labelService.FindByID(r.Context(), id)
				if err != nil {
					h.writeError(w, err)
					return
				}
				h.writeJSON(w, http.StatusOK, label)
			case http.MethodPut:
				var req request.LabelRequestTo
				if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
					h.writeError(w, apperrors.ErrBadRequest)
					return
				}
				label, err := h.labelService.Update(r.Context(), id, &req)
				if err != nil {
					h.writeError(w, err)
					return
				}
				h.writeJSON(w, http.StatusOK, label)
			case http.MethodDelete:
				role := auth.RoleFromContext(r.Context())
				if role != model.RoleAdmin {
					h.writeError(w, apperrors.ErrForbidden)
					return
				}
				if err := h.labelService.Delete(r.Context(), id); err != nil {
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

	if path == "/api/v2.0/labels" {
		switch r.Method {
		case http.MethodGet:
			labels, err := h.labelService.FindAll(r.Context())
			if err != nil {
				h.writeError(w, err)
				return
			}
			h.writeJSON(w, http.StatusOK, labels)
		case http.MethodPost:
			var req request.LabelRequestTo
			if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			label, err := h.labelService.Create(r.Context(), &req)
			if err != nil {
				h.writeError(w, err)
				return
			}
			h.writeJSON(w, http.StatusCreated, label)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

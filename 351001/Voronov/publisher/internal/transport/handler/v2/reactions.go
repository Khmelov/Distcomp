package v2

import (
	"encoding/json"
	"net/http"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
)

func (h *HandlerV2) handleReactions(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v2.0/reactions/") {
		idStr := strings.TrimPrefix(path, "/api/v2.0/reactions/")
		if !strings.Contains(idStr, "/") {
			id, err := parseID(idStr)
			if err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			switch r.Method {
			case http.MethodGet:
				reaction, err := h.reactionService.FindByID(r.Context(), id)
				if err != nil {
					h.writeError(w, err)
					return
				}
				h.writeJSON(w, http.StatusOK, reaction)
			case http.MethodPut:
				var req request.ReactionRequestTo
				if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
					h.writeError(w, apperrors.ErrBadRequest)
					return
				}
				reaction, err := h.reactionService.Update(r.Context(), id, &req)
				if err != nil {
					h.writeError(w, err)
					return
				}
				h.writeJSON(w, http.StatusOK, reaction)
			case http.MethodDelete:
				if err := h.reactionService.Delete(r.Context(), id); err != nil {
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

	if path == "/api/v2.0/reactions" {
		switch r.Method {
		case http.MethodGet:
			reactions, err := h.reactionService.FindAll(r.Context())
			if err != nil {
				h.writeError(w, err)
				return
			}
			h.writeJSON(w, http.StatusOK, reactions)
		case http.MethodPost:
			var req request.ReactionRequestTo
			if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			reaction, err := h.reactionService.Create(r.Context(), &req)
			if err != nil {
				h.writeError(w, err)
				return
			}
			h.writeJSON(w, http.StatusCreated, reaction)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

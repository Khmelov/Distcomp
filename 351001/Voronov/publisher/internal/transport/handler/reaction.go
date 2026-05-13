package handler

import (
	"encoding/json"
	"net/http"
	"strconv"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
)

func (h *Handler) handleReactions(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v1.0/reactions/") {
		idStr := strings.TrimPrefix(path, "/api/v1.0/reactions/")
		if !strings.Contains(idStr, "/") {
			id, err := strconv.ParseInt(idStr, 10, 64)
			if err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			switch r.Method {
			case http.MethodGet:
				h.getReaction(w, r, id)
			case http.MethodPut:
				h.updateReaction(w, r, id)
			case http.MethodDelete:
				h.deleteReaction(w, r, id)
			default:
				h.writeError(w, apperrors.ErrNotFound)
			}
			return
		}
	}

	if path == "/api/v1.0/reactions" {
		switch r.Method {
		case http.MethodGet:
			h.getReactions(w, r)
		case http.MethodPost:
			h.createReaction(w, r)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

func (h *Handler) getReaction(w http.ResponseWriter, r *http.Request, id int64) {
	reaction, err := h.reactionService.FindByID(r.Context(), id)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, reaction)
}

func (h *Handler) getReactions(w http.ResponseWriter, r *http.Request) {
	reactions, err := h.reactionService.FindAll(r.Context())
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, reactions)
}

func (h *Handler) createReaction(w http.ResponseWriter, r *http.Request) {
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
}

func (h *Handler) updateReaction(w http.ResponseWriter, r *http.Request, id int64) {
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
}

func (h *Handler) deleteReaction(w http.ResponseWriter, r *http.Request, id int64) {
	if err := h.reactionService.Delete(r.Context(), id); err != nil {
		h.writeError(w, err)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

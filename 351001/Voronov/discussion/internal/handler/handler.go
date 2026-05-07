package handler

import (
	"encoding/json"
	"errors"
	"net/http"
	"strconv"
	"strings"

	apperrors "discussion/internal/errors"
	"discussion/internal/model"
	"discussion/internal/service"
)

type Handler struct {
	svc service.ReactionService
}

func New(svc service.ReactionService) *Handler {
	return &Handler{svc: svc}
}

func (h *Handler) RegisterRoutes(mux *http.ServeMux) {
	mux.HandleFunc("/api/v1.0/reactions", h.handleCollection)
	mux.HandleFunc("/api/v1.0/reactions/", h.handleItem)
}

func (h *Handler) handleCollection(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case http.MethodGet:
		reactions, err := h.svc.FindAll(r.Context())
		if err != nil {
			h.writeError(w, err)
			return
		}
		h.writeJSON(w, http.StatusOK, reactions)
	case http.MethodPost:
		var req model.Reaction
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			h.writeError(w, apperrors.ErrBadRequest)
			return
		}
		reaction, err := h.svc.Create(r.Context(), &req)
		if err != nil {
			h.writeError(w, err)
			return
		}
		h.writeJSON(w, http.StatusCreated, reaction)
	default:
		h.writeError(w, apperrors.ErrNotFound)
	}
}

func (h *Handler) handleItem(w http.ResponseWriter, r *http.Request) {
	idStr := strings.TrimSuffix(strings.TrimPrefix(r.URL.Path, "/api/v1.0/reactions/"), "/")
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}

	switch r.Method {
	case http.MethodGet:
		reaction, err := h.svc.FindByID(r.Context(), id)
		if err != nil {
			h.writeError(w, err)
			return
		}
		h.writeJSON(w, http.StatusOK, reaction)
	case http.MethodPut:
		var req model.Reaction
		if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
			h.writeError(w, apperrors.ErrBadRequest)
			return
		}
		reaction, err := h.svc.Update(r.Context(), id, &req)
		if err != nil {
			h.writeError(w, err)
			return
		}
		h.writeJSON(w, http.StatusOK, reaction)
	case http.MethodDelete:
		if err := h.svc.Delete(r.Context(), id); err != nil {
			h.writeError(w, err)
			return
		}
		w.WriteHeader(http.StatusNoContent)
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

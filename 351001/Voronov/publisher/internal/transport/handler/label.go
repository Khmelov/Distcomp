package handler

import (
	"encoding/json"
	"net/http"
	"strconv"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
)

func (h *Handler) handleLabels(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v1.0/labels/") {
		idStr := strings.TrimPrefix(path, "/api/v1.0/labels/")
		if !strings.Contains(idStr, "/") {
			id, err := strconv.ParseInt(idStr, 10, 64)
			if err != nil {
				h.writeError(w, apperrors.ErrBadRequest)
				return
			}
			switch r.Method {
			case http.MethodGet:
				h.getLabel(w, r, id)
			case http.MethodPut:
				h.updateLabel(w, r, id)
			case http.MethodDelete:
				h.deleteLabel(w, r, id)
			default:
				h.writeError(w, apperrors.ErrNotFound)
			}
			return
		}
	}

	if path == "/api/v1.0/labels" {
		switch r.Method {
		case http.MethodGet:
			h.getLabels(w, r)
		case http.MethodPost:
			h.createLabel(w, r)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

func (h *Handler) getLabel(w http.ResponseWriter, r *http.Request, id int64) {
	label, err := h.labelService.FindByID(r.Context(), id)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, label)
}

func (h *Handler) getLabels(w http.ResponseWriter, r *http.Request) {
	labels, err := h.labelService.FindAll(r.Context())
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, labels)
}

func (h *Handler) createLabel(w http.ResponseWriter, r *http.Request) {
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
}

func (h *Handler) updateLabel(w http.ResponseWriter, r *http.Request, id int64) {
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
}

func (h *Handler) deleteLabel(w http.ResponseWriter, r *http.Request, id int64) {
	if err := h.labelService.Delete(r.Context(), id); err != nil {
		h.writeError(w, err)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

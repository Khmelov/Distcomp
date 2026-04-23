package handler

import (
	"encoding/json"
	"io"
	"net/http"
	"strconv"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
)

func (h *Handler) handleUsers(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v1.0/users/") {
		idStr := strings.TrimPrefix(path, "/api/v1.0/users/")
		if !strings.Contains(idStr, "/") {
			id, err := strconv.ParseInt(idStr, 10, 64)
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
				h.deleteUser(w, r, id)
			default:
				h.writeError(w, apperrors.ErrNotFound)
			}
			return
		}
	}

	if path == "/api/v1.0/users" {
		switch r.Method {
		case http.MethodGet:
			h.getUsers(w, r)
		case http.MethodPost:
			h.createUser(w, r)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

func (h *Handler) getUser(w http.ResponseWriter, r *http.Request, id int64) {
	user, err := h.userService.FindByID(r.Context(), id)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, user)
}

func (h *Handler) getUsers(w http.ResponseWriter, r *http.Request) {
	users, err := h.userService.FindAll(r.Context())
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, users)
}

func (h *Handler) createUser(w http.ResponseWriter, r *http.Request) {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}
	var req request.UserRequestTo
	if err := json.Unmarshal(body, &req); err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}
	user, err := h.userService.Create(r.Context(), &req)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusCreated, user)
}

func (h *Handler) updateUser(w http.ResponseWriter, r *http.Request, id int64) {
	body, err := io.ReadAll(r.Body)
	if err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}
	var req request.UserRequestTo
	if err := json.Unmarshal(body, &req); err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}
	user, err := h.userService.Update(r.Context(), id, &req)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, user)
}

func (h *Handler) deleteUser(w http.ResponseWriter, r *http.Request, id int64) {
	if err := h.userService.Delete(r.Context(), id); err != nil {
		h.writeError(w, err)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

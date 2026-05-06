package v2

import (
	"encoding/json"
	"net/http"
	"strconv"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
)

func parseID(s string) (int64, error) {
	return strconv.ParseInt(s, 10, 64)
}

func (h *HandlerV2) getUser(w http.ResponseWriter, r *http.Request, id int64) {
	user, err := h.userService.FindByID(r.Context(), id)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, user)
}

func (h *HandlerV2) getUsers(w http.ResponseWriter, r *http.Request) {
	users, err := h.userService.FindAll(r.Context())
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, users)
}

func (h *HandlerV2) updateUser(w http.ResponseWriter, r *http.Request, id int64) {
	var req request.UserRequestTo
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
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

func (h *HandlerV2) deleteUser(w http.ResponseWriter, r *http.Request, id int64) {
	if err := h.userService.Delete(r.Context(), id); err != nil {
		h.writeError(w, err)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

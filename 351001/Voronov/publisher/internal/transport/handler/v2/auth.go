package v2

import (
	"encoding/json"
	"net/http"

	"publisher/internal/auth"
	apperrors "publisher/internal/errors"
	"publisher/internal/model"
)

func (h *HandlerV2) handleLogin(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		h.writeError(w, apperrors.ErrNotFound)
		return
	}
	var req auth.LoginRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}
	resp, err := h.authService.Login(r.Context(), &req)
	if err != nil {
		h.writeError(w, apperrors.ErrUnauthorized)
		return
	}
	h.writeJSON(w, http.StatusOK, resp)
}

func (h *HandlerV2) registerUser(w http.ResponseWriter, r *http.Request) {
	var req auth.RegisterRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		h.writeError(w, apperrors.ErrBadRequest)
		return
	}
	if req.Role == "" {
		req.Role = model.RoleCustomer
	}
	user, err := h.authService.Register(r.Context(), &req)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusCreated, user)
}

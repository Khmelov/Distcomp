package handler

import (
	"encoding/json"
	"net/http"
	"strconv"
	"strings"

	apperrors "publisher/internal/errors"
	"publisher/internal/transport/dto/request"
)

func (h *Handler) handleIssues(w http.ResponseWriter, r *http.Request, path string) {
	path = strings.TrimSuffix(path, "/")

	if strings.HasPrefix(path, "/api/v1.0/issues/") {
		idStr := strings.TrimPrefix(path, "/api/v1.0/issues/")
		parts := strings.SplitN(idStr, "/", 2)

		id, err := strconv.ParseInt(parts[0], 10, 64)
		if err != nil {
			h.writeError(w, apperrors.ErrBadRequest)
			return
		}

		if len(parts) == 2 {
			switch parts[1] {
			case "user":
				h.getUserByIssue(w, r, id)
			case "labels":
				h.getLabelsByIssue(w, r, id)
			case "reactions":
				h.getReactionsByIssue(w, r, id)
			default:
				h.writeError(w, apperrors.ErrNotFound)
			}
			return
		}

		switch r.Method {
		case http.MethodGet:
			h.getIssue(w, r, id)
		case http.MethodPut:
			h.updateIssue(w, r, id)
		case http.MethodDelete:
			h.deleteIssue(w, r, id)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	if path == "/api/v1.0/issues" {
		switch r.Method {
		case http.MethodGet:
			h.getIssues(w, r)
		case http.MethodPost:
			h.createIssue(w, r)
		default:
			h.writeError(w, apperrors.ErrNotFound)
		}
		return
	}

	h.writeError(w, apperrors.ErrNotFound)
}

func (h *Handler) getIssue(w http.ResponseWriter, r *http.Request, id int64) {
	issue, err := h.issueService.FindByID(r.Context(), id)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, issue)
}

func (h *Handler) getIssues(w http.ResponseWriter, r *http.Request) {
	issues, err := h.issueService.FindAll(r.Context())
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, issues)
}

func (h *Handler) createIssue(w http.ResponseWriter, r *http.Request) {
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
}

func (h *Handler) updateIssue(w http.ResponseWriter, r *http.Request, id int64) {
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
}

func (h *Handler) deleteIssue(w http.ResponseWriter, r *http.Request, id int64) {
	if err := h.issueService.Delete(r.Context(), id); err != nil {
		h.writeError(w, err)
		return
	}
	w.WriteHeader(http.StatusNoContent)
}

func (h *Handler) getUserByIssue(w http.ResponseWriter, r *http.Request, issueID int64) {
	user, err := h.issueService.FindByUserID(r.Context(), issueID)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, user)
}

func (h *Handler) getLabelsByIssue(w http.ResponseWriter, r *http.Request, issueID int64) {
	labels, _, err := h.issueService.FindByIssueID(r.Context(), issueID)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, labels)
}

func (h *Handler) getReactionsByIssue(w http.ResponseWriter, r *http.Request, issueID int64) {
	_, reactions, err := h.issueService.FindByIssueID(r.Context(), issueID)
	if err != nil {
		h.writeError(w, err)
		return
	}
	h.writeJSON(w, http.StatusOK, reactions)
}

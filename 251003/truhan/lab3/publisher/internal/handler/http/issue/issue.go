package issue

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"strconv"

	issueModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	issueDbModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/psql/issue"

	"github.com/gorilla/mux"
)

type Issue struct {
	srv IssueService
}

func New(srv IssueService) Issue {
	return Issue{
		srv: srv,
	}
}

type IssueService interface {
	CreateIssue(ctx context.Context, issue issueModel.Issue) (issueModel.Issue, error)
	GetIssues(ctx context.Context) ([]issueModel.Issue, error)
	GetIssueByID(ctx context.Context, id int64) (issueModel.Issue, error)
	UpdateIssueByID(ctx context.Context, issue issueModel.Issue) (issueModel.Issue, error)
	DeleteIssueByID(ctx context.Context, id int64) error
}

func (i Issue) InitRoutes(r *mux.Router) {
	r.HandleFunc("/tweets", i.getIssuesList).Methods(http.MethodGet)
	r.HandleFunc("/tweets", i.createIssue).Methods(http.MethodPost)
	r.HandleFunc("/tweets/{id}", i.getIssueByID).Methods(http.MethodGet)
	r.HandleFunc("/tweets/{id}", i.deleteIssueByID).Methods(http.MethodDelete)
	r.HandleFunc("/tweets", i.updateIssueByID).Methods(http.MethodPut)
}

func (i Issue) getIssuesList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	issues, err := i.srv.GetIssues(ctx)
	if err != nil {
		http.Error(w, fmt.Sprintf("failed to retrieve issues: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(issues); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode issues: %v", err), http.StatusInternalServerError)
	}
}

func (i Issue) createIssue(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	var issue issueModel.Issue

	if err := json.NewDecoder(r.Body).Decode(&issue); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := issue.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	createdIssue, err := i.srv.CreateIssue(ctx, issue)
	if err != nil {
		if errors.Is(err, issueDbModel.ErrInvalidForeignKey) {
			http.Error(w, "{}", http.StatusForbidden)
			return
		}

		if errors.Is(err, issueDbModel.ErrInvalidIssueData) {
			http.Error(w, "{}", http.StatusForbidden)
			return
		}

		http.Error(w, "{}", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(createdIssue); err != nil {
		http.Error(w, "{}", http.StatusInternalServerError)
	}
}

func (i Issue) getIssueByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	idStr := mux.Vars(r)["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	issue, err := i.srv.GetIssueByID(ctx, id)
	if err != nil {
		if err == issueDbModel.ErrIssueNotFound {
			http.Error(w, "issue not found", http.StatusNotFound)
		} else {
			http.Error(w, fmt.Sprintf("failed to retrieve issue: %v", err), http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(issue); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode issue: %v", err), http.StatusInternalServerError)
	}
}

func (i Issue) deleteIssueByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	idStr := mux.Vars(r)["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid issue ID: %v", err), http.StatusBadRequest)
		return
	}

	if err := i.srv.DeleteIssueByID(ctx, id); err != nil {
		if err == issueDbModel.ErrIssueNotFound {
			http.Error(w, "{}", http.StatusNotFound)
		} else {
			http.Error(w, "{}", http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusNoContent)
	w.Write([]byte(`{}`))
}

func (i Issue) updateIssueByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	var issue issueModel.Issue

	if err := json.NewDecoder(r.Body).Decode(&issue); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := issue.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	updatedIssue, err := i.srv.UpdateIssueByID(ctx, issue)
	if err != nil {
		if err == issueDbModel.ErrIssueNotFound {
			http.Error(w, "{}", http.StatusNotFound)
		} else {
			http.Error(w, "{}", http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(updatedIssue); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode updated issue: %v", err), http.StatusInternalServerError)
	}
}

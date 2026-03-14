package issue

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	issueErr "github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/storage/issue"
	"net/http"
	"strconv"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/model"
	"github.com/gorilla/mux"
)

type issue struct {
	srv Service
}

func New(srv Service) issue {
	return issue{
		srv: srv,
	}
}

type Service interface {
	Create(ctx context.Context, issue model.Issue) (model.Issue, error)
	GetList(ctx context.Context) ([]model.Issue, error)
	Get(ctx context.Context, id int64) (model.Issue, error)
	Update(ctx context.Context, issue model.Issue) (model.Issue, error)
	Delete(ctx context.Context, id int64) error
}

func (i issue) InitRoutes(r *mux.Router) {
	r.HandleFunc("/issues", i.getList).Methods(http.MethodGet)
	r.HandleFunc("/issues", i.create).Methods(http.MethodPost)
	r.HandleFunc("/issues/{id}", i.get).Methods(http.MethodGet)
	r.HandleFunc("/issues/{id}", i.deletet).Methods(http.MethodDelete)
	r.HandleFunc("/issues", i.update).Methods(http.MethodPut)
}

func (i issue) getList(w http.ResponseWriter, r *http.Request) {
	result, err := i.srv.GetList(r.Context())
	if err != nil {
		http.Error(w, fmt.Sprintf("failed to retrieve result: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(result)
}

func (i issue) create(w http.ResponseWriter, r *http.Request) {
	var req model.Issue

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, fmt.Sprintf("invalid input: %v", err), http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, fmt.Sprintf("validation error: %v", err), http.StatusBadRequest)
		return
	}

	result, err := i.srv.Create(r.Context(), req)
	if err != nil {
		if errors.Is(err, issueErr.ErrInvalidForeignKey) {
			http.Error(w, fmt.Sprintf("failed to create req: %v", err), http.StatusBadRequest)
		}

		if errors.Is(err, issueErr.ErrInvalidIssueData) {
			http.Error(w, fmt.Sprintf("failed to create req: %v", err), http.StatusBadRequest)
		}

		http.Error(w, fmt.Sprintf("failed to create req: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(result)
}

func (i issue) get(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.ParseInt(mux.Vars(r)["id"], 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid result ID: %v", err), http.StatusBadRequest)
		return
	}

	result, err := i.srv.Get(r.Context(), id)
	if err != nil {
		if errors.Is(err, issueErr.ErrIssueNotFound) {
			http.Error(w, "result not found", http.StatusNotFound)
			return
		}

		http.Error(w, fmt.Sprintf("failed to retrieve result: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(result)
}

func (i issue) deletet(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.ParseInt(mux.Vars(r)["id"], 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid issue ID: %v", err), http.StatusBadRequest)
		return
	}

	if err := i.srv.Delete(r.Context(), id); err != nil {
		if errors.Is(err, issueErr.ErrIssueNotFound) {
			http.Error(w, "{}", http.StatusNotFound)
			return
		}

		http.Error(w, "{}", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusNoContent)
	w.Write([]byte(`{}`))
}

func (i issue) update(w http.ResponseWriter, r *http.Request) {
	var req model.Issue

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	result, err := i.srv.Update(r.Context(), req)
	if err != nil {
		if errors.Is(err, issueErr.ErrIssueNotFound) {
			http.Error(w, "{}", http.StatusNotFound)
			return
		}

		http.Error(w, "{}", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(result)
}

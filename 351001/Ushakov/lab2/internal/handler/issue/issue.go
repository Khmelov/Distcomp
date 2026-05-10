package issue

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/handler/label"
	issueErr "github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/issue"
	labelErr "github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/label"
	"math"
	"math/rand"
	"net/http"
	"strconv"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/model"
	"github.com/gorilla/mux"
)

type issue struct {
	srv          Service
	lableService label.Service
}

func New(srv Service, lableService label.Service) issue {
	return issue{
		srv:          srv,
		lableService: lableService,
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
	r.HandleFunc("/issues", i.create).Methods(http.Methodgithub.com / Khmelov / Distcomp / 351001 / Ushakov)
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
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if req.Labels != nil {
		rand.Seed(time.Now().UnixNano())

		for _, l := range req.Labels {
			_, err := i.lableService.Create(r.Context(), model.Label{Name: l, ID: int64(rand.Intn(math.MaxInt) + 1)})
			if err != nil {
				if errors.Is(err, labelErr.ErrConstraintsCheck) {
					http.Error(w, "{}", http.StatusBadRequest)
					return
				}

				http.Error(w, "{}", http.StatusBadRequest)
				return
			}
		}
	}

	result, err := i.srv.Create(r.Context(), req)
	if err != nil {
		if errors.Is(err, issueErr.ErrDublicate) {
			http.Error(w, "{}", http.StatusForbidden)
			return
		}

		if errors.Is(err, issueErr.ErrInvalidForeignKey) {
			http.Error(w, "{}", http.StatusBadRequest)
		}

		if errors.Is(err, issueErr.ErrInvalidIssueData) {
			http.Error(w, "{}", http.StatusBadRequest)
		}

		http.Error(w, "{}", http.StatusBadRequest)
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

package author

import (
	"context"
	"encoding/json"
	"errors"
	authorErr "github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage/author"
	"net/http"
	"strconv"

	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/model"
	"github.com/gorilla/mux"
)

type Handler struct {
	srv Service
}

func New(srv Service) Handler {
	return Handler{
		srv: srv,
	}
}

type Service interface {
	Create(ctx context.Context, cr model.Author) (model.Author, error)
	GetList(ctx context.Context) ([]model.Author, error)
	Get(ctx context.Context, id int) (model.Author, error)
	Update(ctx context.Context, cr model.Author) (model.Author, error)
	Delete(ctx context.Context, id int) error
}

func (c Handler) InitRoutes(r *mux.Router) {
	r.HandleFunc("/authors", c.getList).Methods(http.MethodGet)
	r.HandleFunc("/authors/{id}", c.get).Methods(http.MethodGet)
	r.HandleFunc("/authors", c.create).Methods(http.MethodPost)
	r.HandleFunc("/authors/{id}", c.delete).Methods(http.MethodDelete)
	r.HandleFunc("/authors", c.update).Methods(http.MethodPut)
}

func (c Handler) getList(w http.ResponseWriter, r *http.Request) {
	result, err := c.srv.GetList(r.Context())
	if err != nil {
		http.Error(w, "Failed to get result", http.StatusInternalServerError)
		return
	}

	if result == nil {
		result = make([]model.Author, 0)
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(result); err != nil {
		http.Error(w, "Failed to encode result", http.StatusInternalServerError)
	}
}

func (c Handler) get(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.Atoi(mux.Vars(r)["id"])
	if err != nil {
		http.Error(w, "Invalid ID format", http.StatusBadRequest)
		return
	}

	author, err := c.srv.Get(r.Context(), id)
	if err != nil {
		http.Error(w, "Failed to get Handler", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(author); err != nil {
		http.Error(w, "Failed to encode Handler", http.StatusInternalServerError)
	}
}

func (c Handler) create(w http.ResponseWriter, r *http.Request) {
	var req model.Author

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, `{"error": "`+err.Error()+`"}`, http.StatusBadRequest)
		return
	}

	result, err := c.srv.Create(r.Context(), req)
	if err != nil {
		http.Error(w, "{}", http.StatusForbidden)
		return
	}

	w.Header().Set("Content-Type", "text/plain")
	w.WriteHeader(http.StatusCreated)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(result)
}

func (c Handler) delete(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.Atoi(mux.Vars(r)["id"])
	if err != nil {
		http.Error(w, `{"error": "Invalid ID format"}`, http.StatusBadRequest)
		return
	}

	ctx := r.Context()

	err = c.srv.Delete(ctx, id)
	if err != nil {
		if errors.Is(err, authorErr.ErrAuthorNotFound) {
			http.Error(w, `{"error": "Handler not found"}`, http.StatusNotFound)
			return
		}

		http.Error(w, `{"error": "Failed to delete Handler"}`, http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusNoContent)
	_, _ = w.Write([]byte(`{"comment": "Handler deleted successfully"}`))
}

func (c Handler) update(w http.ResponseWriter, r *http.Request) {
	var req model.Author

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, `{"error": "`+err.Error()+`"}`, http.StatusBadRequest)
		return
	}

	result, err := c.srv.Update(r.Context(), req)
	if err != nil {
		if errors.Is(err, authorErr.ErrAuthorNotFound) {
			http.Error(w, "Handler not found", http.StatusNotFound)
			return
		}

		http.Error(w, "Failed to update Handler", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(result)
}

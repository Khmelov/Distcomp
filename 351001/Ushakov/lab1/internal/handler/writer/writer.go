package writer

import (
	"context"
	"encoding/json"
	"errors"
	writerErr "github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/storage/writer"
	"net/http"
	"strconv"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/model"
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
	Create(ctx context.Context, cr model.Writer) (model.Writer, error)
	GetList(ctx context.Context) ([]model.Writer, error)
	Get(ctx context.Context, id int) (model.Writer, error)
	Update(ctx context.Context, cr model.Writer) (model.Writer, error)
	Delete(ctx context.Context, id int) error
}

func (c Handler) InitRoutes(r *mux.Router) {
	r.HandleFunc("/writers", c.getList).Methods(http.MethodGet)
	r.HandleFunc("/writers/{id}", c.get).Methods(http.MethodGet)
	r.HandleFunc("/writers", c.create).Methods(http.MethodPost)
	r.HandleFunc("/writers/{id}", c.delete).Methods(http.MethodDelete)
	r.HandleFunc("/writers", c.update).Methods(http.MethodPut)
}

func (c Handler) getList(w http.ResponseWriter, r *http.Request) {
	result, err := c.srv.GetList(r.Context())
	if err != nil {
		http.Error(w, "Failed to get result", http.StatusInternalServerError)
		return
	}

	if result == nil {
		result = make([]model.Writer, 0)
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

	writer, err := c.srv.Get(r.Context(), id)
	if err != nil {
		http.Error(w, "Failed to get Handler", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(writer); err != nil {
		http.Error(w, "Failed to encode Handler", http.StatusInternalServerError)
	}
}

func (c Handler) create(w http.ResponseWriter, r *http.Request) {
	var req model.Writer

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
		http.Error(w, "Failed to create Handler", http.StatusInternalServerError)
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
		if errors.Is(err, writerErr.ErrWriterNotFound) {
			http.Error(w, `{"error": "Handler not found"}`, http.StatusNotFound)
			return
		}

		http.Error(w, `{"error": "Failed to delete Handler"}`, http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusNoContent)
	_, _ = w.Write([]byte(`{"post": "Handler deleted successfully"}`))
}

func (c Handler) update(w http.ResponseWriter, r *http.Request) {
	var req model.Writer

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
		if errors.Is(err, writerErr.ErrWriterNotFound) {
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

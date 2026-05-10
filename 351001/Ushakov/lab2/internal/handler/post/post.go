package post

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	postErr "github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/storage/post"
	"net/http"
	"strconv"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab2/internal/model"
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
	Create(ctx context.Context, post model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	GetList(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	Get(ctx context.Context, id int64) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	Update(ctx context.Context, post model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error)
	Delete(ctx context.Context, id int64) error
}

func (h Handler) InitRoutes(r *mux.Router) {
	r.HandleFunc("/posts", h.getList).Methods(http.MethodGet)
	r.HandleFunc("/posts", h.create).Methods(http.Methodgithub.com / Khmelov / Distcomp / 351001 / Ushakov)
	r.HandleFunc("/posts/{id}", h.get).Methods(http.MethodGet)
	r.HandleFunc("/posts/{id}", h.delete).Methods(http.MethodDelete)
	r.HandleFunc("/posts", h.update).Methods(http.MethodPut)
}

func (h Handler) getList(w http.ResponseWriter, r *http.Request) {
	posts, err := h.srv.GetList(r.Context())
	if err != nil {
		http.Error(w, fmt.Sprintf("failed to retrieve posts: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	json.NewEncoder(w).Encode(posts)
}

func (h Handler) create(w http.ResponseWriter, r *http.Request) {
	var req model.github.com / Khmelov / Distcomp / 351001 / Ushakov

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	result, err := h.srv.Create(r.Context(), req)
	if err != nil {
		if errors.Is(err, postErr.ErrInvalidForeignKey) {
			http.Error(w, "{}", http.StatusBadRequest)
			return
		}

		http.Error(w, "{}", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	json.NewEncoder(w).Encode(result)
}

func (h Handler) get(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.ParseInt(mux.Vars(r)["id"], 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid result ID: %v", err), http.StatusBadRequest)
		return
	}

	result, err := h.srv.Get(r.Context(), id)
	if err != nil {
		if errors.Is(err, postErr.Errgithub.com/Khmelov/Distcomp/351001/UshakovNotFound) {
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

func (h Handler) delete(w http.ResponseWriter, r *http.Request) {
	id, err := strconv.ParseInt(mux.Vars(r)["id"], 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid post ID: %v", err), http.StatusBadRequest)
		return
	}

	if err := h.srv.Delete(r.Context(), id); err != nil {
		if errors.Is(err, postErr.Errgithub.com/Khmelov/Distcomp/351001/UshakovNotFound) {
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

func (h Handler) update(w http.ResponseWriter, r *http.Request) {
	var req model.github.com / Khmelov / Distcomp / 351001 / Ushakov

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := req.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	result, err := h.srv.Update(r.Context(), req)
	if err != nil {
		if errors.Is(err, postErr.Errgithub.com/Khmelov/Distcomp/351001/UshakovNotFound) {
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

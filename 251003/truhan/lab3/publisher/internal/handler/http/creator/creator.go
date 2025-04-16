package creator

import (
	"context"
	"encoding/json"
	"errors"
	"net/http"
	"strconv"

	creatorModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	creatorDbModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/psql/creator"

	"github.com/gorilla/mux"
)

type Creator struct {
	srv CreatorService
}

func New(srv CreatorService) Creator {
	return Creator{
		srv: srv,
	}
}

type CreatorService interface {
	CreateCreator(ctx context.Context, cr creatorModel.Creator) (creatorModel.Creator, error)
	GetCreators(ctx context.Context) ([]creatorModel.Creator, error)
	GetCreatorByID(ctx context.Context, id int) (creatorModel.Creator, error)
	UpdateCreatorByID(ctx context.Context, cr creatorModel.Creator) (creatorModel.Creator, error)
	DeleteCreatorByID(ctx context.Context, id int) error
}

func (c Creator) InitRoutes(r *mux.Router) {
	r.HandleFunc("/authors", c.getCreatorsList).Methods(http.MethodGet)
	r.HandleFunc("/authors/{id}", c.getCreatorByID).Methods(http.MethodGet)
	r.HandleFunc("/authors", c.createCreator).Methods(http.MethodPost)
	r.HandleFunc("/authors/{id}", c.deleteCreatorByID).Methods(http.MethodDelete)
	r.HandleFunc("/authors", c.updateCreatorByID).Methods(http.MethodPut)
}

func (c Creator) getCreatorsList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	creators, err := c.srv.GetCreators(ctx)
	if err != nil {
		http.Error(w, "Failed to get creators", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(creators); err != nil {
		http.Error(w, "Failed to encode creators", http.StatusInternalServerError)
	}
}

func (c Creator) getCreatorByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, "Invalid ID format", http.StatusBadRequest)
		return
	}

	ctx := r.Context()

	creator, err := c.srv.GetCreatorByID(ctx, id)
	if err != nil {
		http.Error(w, "Failed to get creator", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(creator); err != nil {
		http.Error(w, "Failed to encode creator", http.StatusInternalServerError)
	}
}

func (c Creator) createCreator(w http.ResponseWriter, r *http.Request) {
	var cr creatorModel.Creator
	if err := json.NewDecoder(r.Body).Decode(&cr); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if err := cr.Validate(); err != nil {
		http.Error(w, `{"error": "`+err.Error()+`"}`, http.StatusBadRequest)
		return
	}

	ctx := r.Context()

	creator, err := c.srv.CreateCreator(ctx, cr)
	if err != nil {
		if errors.Is(err, creatorDbModel.ErrLoginExists) {
			http.Error(w, "{}", http.StatusForbidden)
			return
		}

		http.Error(w, "Failed to create creator", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "text/plain")
	w.WriteHeader(http.StatusCreated)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(creator)
}

func (c Creator) deleteCreatorByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.Atoi(idStr)
	if err != nil {
		http.Error(w, `{"error": "Invalid ID format"}`, http.StatusBadRequest)
		return
	}

	ctx := r.Context()

	err = c.srv.DeleteCreatorByID(ctx, id)
	if err != nil {
		if errors.Is(err, creatorDbModel.ErrCreatorNotFound) {
			http.Error(w, `{"error": "Creator not found"}`, http.StatusNotFound)
			return
		}
		http.Error(w, `{"error": "Failed to delete creator"}`, http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusNoContent)
	_, _ = w.Write([]byte(`{"message": "Creator deleted successfully"}`))
}

func (c Creator) updateCreatorByID(w http.ResponseWriter, r *http.Request) {
	var cr creatorModel.Creator
	if err := json.NewDecoder(r.Body).Decode(&cr); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		return
	}

	if err := cr.Validate(); err != nil {
		http.Error(w, `{"error": "`+err.Error()+`"}`, http.StatusBadRequest)
		return
	}

	ctx := r.Context()

	updatedCreator, err := c.srv.UpdateCreatorByID(ctx, cr)
	if err != nil {
		if errors.Is(err, creatorDbModel.ErrCreatorNotFound) {
			http.Error(w, "Creator not found", http.StatusNotFound)
			return
		}
		http.Error(w, "Failed to update creator", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(updatedCreator); err != nil {
		http.Error(w, "Failed to encode updated creator", http.StatusInternalServerError)
	}
}

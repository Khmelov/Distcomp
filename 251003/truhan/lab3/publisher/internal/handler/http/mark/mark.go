package mark

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"strconv"

	markModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	markDbModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/psql/mark"

	"github.com/gorilla/mux"
)

type Mark struct {
	srv MarkService
}

func New(srv MarkService) Mark {
	return Mark{
		srv: srv,
	}
}

type MarkService interface {
	CreateMark(ctx context.Context, mark markModel.Mark) (markModel.Mark, error)
	GetMarks(ctx context.Context) ([]markModel.Mark, error)
	GetMarkByID(ctx context.Context, id int64) (markModel.Mark, error)
	UpdateMarkByID(ctx context.Context, mark markModel.Mark) (markModel.Mark, error)
	DeleteMarkByID(ctx context.Context, id int64) error
}

func (m Mark) InitRoutes(r *mux.Router) {
	r.HandleFunc("/labels", m.getMarksList).Methods(http.MethodGet)
	r.HandleFunc("/labels", m.createMark).Methods(http.MethodPost)
	r.HandleFunc("/labels/{id}", m.getMarkByID).Methods(http.MethodGet)
	r.HandleFunc("/labels/{id}", m.deleteMarkByID).Methods(http.MethodDelete)
	r.HandleFunc("/labels", m.updateMarkByID).Methods(http.MethodPut)
}

func (m Mark) getMarksList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	marks, err := m.srv.GetMarks(ctx)
	if err != nil {
		http.Error(w, fmt.Sprintf("failed to retrieve marks: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(marks); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode marks: %v", err), http.StatusInternalServerError)
	}
}

func (m Mark) createMark(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	var mark markModel.Mark

	if err := json.NewDecoder(r.Body).Decode(&mark); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := mark.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	createdMark, err := m.srv.CreateMark(ctx, mark)
	if err != nil {
		if errors.Is(err, markDbModel.ErrConstraintsCheck) {
			http.Error(w, "{}", http.StatusBadRequest)
			return
		}

		http.Error(w, "{}", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(createdMark); err != nil {
		http.Error(w, "{}", http.StatusInternalServerError)
	}
}

func (m Mark) getMarkByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	idStr := mux.Vars(r)["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid mark ID: %v", err), http.StatusBadRequest)
		return
	}

	mark, err := m.srv.GetMarkByID(ctx, id)
	if err != nil {
		if err == markDbModel.ErrMessageNotFound {
			http.Error(w, "mark not found", http.StatusNotFound)
		} else {
			http.Error(w, fmt.Sprintf("failed to retrieve message: %v", err), http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(mark); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode mark: %v", err), http.StatusInternalServerError)
	}
}

func (m Mark) deleteMarkByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	idStr := mux.Vars(r)["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid mark ID: %v", err), http.StatusBadRequest)
		return
	}

	if err := m.srv.DeleteMarkByID(ctx, id); err != nil {
		if err == markDbModel.ErrMessageNotFound {
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

func (m Mark) updateMarkByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	var mark markModel.Mark

	if err := json.NewDecoder(r.Body).Decode(&mark); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := mark.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	updatedMark, err := m.srv.UpdateMarkByID(ctx, mark)
	if err != nil {
		if err == markDbModel.ErrMessageNotFound {
			http.Error(w, "{}", http.StatusNotFound)
		} else {
			http.Error(w, "{}", http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(updatedMark); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode updated message: %v", err), http.StatusInternalServerError)
	}
}

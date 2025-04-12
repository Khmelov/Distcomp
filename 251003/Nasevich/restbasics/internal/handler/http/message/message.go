package message

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net/http"
	"strconv"

	messageModel "github.com/Khmelov/Distcomp/251003/Nasevich/restbasics/internal/model"
	messageDbModel "github.com/Khmelov/Distcomp/251003/Nasevich/restbasics/internal/storage/psql/message"

	"github.com/gorilla/mux"
)

type Message struct {
	srv MessageService
}

func New(srv MessageService) Message {
	return Message{
		srv: srv,
	}
}

type MessageService interface {
	CreateMessage(ctx context.Context, message messageModel.Message) (messageModel.Message, error)
	GetMessages(ctx context.Context) ([]messageModel.Message, error)
	GetMessageByID(ctx context.Context, id int64) (messageModel.Message, error)
	UpdateMessageByID(ctx context.Context, message messageModel.Message) (messageModel.Message, error)
	DeleteMessageByID(ctx context.Context, id int64) error
}

func (m Message) InitRoutes(r *mux.Router) {
	r.HandleFunc("/messages", m.getMessagesList).Methods(http.MethodGet)
	r.HandleFunc("/messages", m.createMessage).Methods(http.MethodPost)
	r.HandleFunc("/messages/{id}", m.getMessageByID).Methods(http.MethodGet)
	r.HandleFunc("/messages/{id}", m.deleteMessageByID).Methods(http.MethodDelete)
	r.HandleFunc("/messages", m.updateMessageByID).Methods(http.MethodPut)
}

func (m Message) getMessagesList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	messages, err := m.srv.GetMessages(ctx)
	if err != nil {
		http.Error(w, fmt.Sprintf("failed to retrieve messages: %v", err), http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(messages); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode messages: %v", err), http.StatusInternalServerError)
	}
}

func (m Message) createMessage(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	var message messageModel.Message

	if err := json.NewDecoder(r.Body).Decode(&message); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := message.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	createdMessage, err := m.srv.CreateMessage(ctx, message)
	if err != nil {
		if errors.Is(err, messageDbModel.ErrInvalidForeignKey) {
			http.Error(w, "{}", http.StatusBadRequest)
			return
		}

		http.Error(w, "{}", http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(createdMessage); err != nil {
		http.Error(w, "{}", http.StatusInternalServerError)
	}
}

func (m Message) getMessageByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	idStr := mux.Vars(r)["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid message ID: %v", err), http.StatusBadRequest)
		return
	}

	message, err := m.srv.GetMessageByID(ctx, id)
	if err != nil {
		if err == messageDbModel.ErrMessageNotFound {
			http.Error(w, "message not found", http.StatusNotFound)
		} else {
			http.Error(w, fmt.Sprintf("failed to retrieve message: %v", err), http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(message); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode message: %v", err), http.StatusInternalServerError)
	}
}

func (m Message) deleteMessageByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	idStr := mux.Vars(r)["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		http.Error(w, fmt.Sprintf("invalid message ID: %v", err), http.StatusBadRequest)
		return
	}

	if err := m.srv.DeleteMessageByID(ctx, id); err != nil {
		if err == messageDbModel.ErrMessageNotFound {
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

func (m Message) updateMessageByID(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	var message messageModel.Message

	if err := json.NewDecoder(r.Body).Decode(&message); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	if err := message.Validate(); err != nil {
		http.Error(w, "{}", http.StatusBadRequest)
		return
	}

	updatedMessage, err := m.srv.UpdateMessageByID(ctx, message)
	if err != nil {
		if err == messageDbModel.ErrMessageNotFound {
			http.Error(w, "{}", http.StatusNotFound)
		} else {
			http.Error(w, "{}", http.StatusInternalServerError)
		}
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(updatedMessage); err != nil {
		http.Error(w, fmt.Sprintf("failed to encode updated message: %v", err), http.StatusInternalServerError)
	}
}

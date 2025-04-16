package message

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	messageModel "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/model"
	messageService "github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service/message"
	"github.com/gorilla/mux"
)

type MessageHandler struct {
	service messageService.MessageService
}

func New(srv messageService.MessageService) *MessageHandler {
	return &MessageHandler{
		service: srv,
	}
}

func (h *MessageHandler) InitRoutes(r *mux.Router) {
	r.HandleFunc("/comments", h.getMessagesList).Methods(http.MethodGet)
	r.HandleFunc("/comments", h.createMessage).Methods(http.MethodPost)
	r.HandleFunc("/comments/{id}", h.getMessageByID).Methods(http.MethodGet)
	r.HandleFunc("/comments/{id}", h.deleteMessageByID).Methods(http.MethodDelete)
	r.HandleFunc("/comments", h.updateMessageByID).Methods(http.MethodPut)
}

func (h *MessageHandler) getMessagesList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	messages, err := h.service.GetMessages(ctx)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to get messages")
		return
	}

	if messages == nil {
		messages = []messageModel.Message{}
	}

	respondWithJSON(w, http.StatusOK, messages)
}

func (h *MessageHandler) getMessageByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid ID format")
		return
	}

	ctx := r.Context()

	message, err := h.service.GetMessageByID(ctx, id)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to get message")
		return
	}

	respondWithJSON(w, http.StatusOK, message)
}

func (h *MessageHandler) createMessage(w http.ResponseWriter, r *http.Request) {
	var msg messageModel.Message
	if err := json.NewDecoder(r.Body).Decode(&msg); err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	if err := msg.Validate(); err != nil {
		respondWithError(w, http.StatusBadRequest, err.Error())
		return
	}

	ctx := r.Context()

	createdMessage, err := h.service.CreateMessage(ctx, msg)
	if err != nil {
		fmt.Println(err.Error())
		respondWithError(w, http.StatusInternalServerError, "Failed to create message")
		return
	}

	respondWithJSON(w, http.StatusCreated, createdMessage)
}

func (h *MessageHandler) deleteMessageByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid ID format")
		return
	}

	ctx := r.Context()

	err = h.service.DeleteMessageByID(ctx, id)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to delete message")
		return
	}

	respondWithJSON(w, http.StatusNoContent, map[string]string{"message": "Message deleted successfully"})
}

func (h *MessageHandler) updateMessageByID(w http.ResponseWriter, r *http.Request) {
	var msg messageModel.Message
	if err := json.NewDecoder(r.Body).Decode(&msg); err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	if err := msg.Validate(); err != nil {
		respondWithError(w, http.StatusBadRequest, err.Error())
		return
	}

	ctx := r.Context()

	updatedMessage, err := h.service.UpdateMessageByID(ctx, msg)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to update message")
		return
	}

	respondWithJSON(w, http.StatusOK, updatedMessage)
}

func respondWithError(w http.ResponseWriter, code int, message string) {
	respondWithJSON(w, code, map[string]string{"error": message})
}

func respondWithJSON(w http.ResponseWriter, code int, payload interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(code)
	if payload != nil {
		json.NewEncoder(w).Encode(payload)
	}
}

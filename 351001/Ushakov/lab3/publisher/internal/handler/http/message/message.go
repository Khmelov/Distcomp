package post

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	postModel "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/model"
	postService "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/service/post"
	"github.com/gorilla/mux"
)

type github.com/Khmelov/Distcomp/351001/UshakovHandler struct {
service postService.github.com/Khmelov/Distcomp/351001/UshakovService
}

func New(srv postService.github.com/Khmelov/Distcomp/351001/UshakovService) *github.com/Khmelov/Distcomp/351001/UshakovHandler {
return &github.com/Khmelov/Distcomp/351001/UshakovHandler{
service: srv,
}
}

func (h *github.com /Khmelov/Distcomp/351001/UshakovHandler) InitRoutes(r *mux.Router) {
	r.HandleFunc("/comments", h.getgithub.com/Khmelov/Distcomp/351001/UshakovsList).Methods(http.MethodGet)
	r.HandleFunc("/comments", h.creategithub.com/Khmelov/Distcomp/351001/Ushakov).Methods(http.Methodgithub.com / Khmelov / Distcomp / 351001 / Ushakov)
	r.HandleFunc("/comments/{id}", h.getgithub.com/Khmelov/Distcomp/351001/UshakovByID).Methods(http.MethodGet)
	r.HandleFunc("/comments/{id}", h.deletegithub.com/Khmelov/Distcomp/351001/UshakovByID).Methods(http.MethodDelete)
	r.HandleFunc("/comments", h.updategithub.com/Khmelov/Distcomp/351001/UshakovByID).Methods(http.MethodPut)
}

func (h *github.com /Khmelov/Distcomp/351001/UshakovHandler) getgithub.com/Khmelov/Distcomp/351001/UshakovsList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	posts, err := h.service.Getgithub.com / Khmelov / Distcomp / 351001 / Ushakovs(ctx)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to get posts")
		return
	}

	if posts == nil {
		posts = []postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov{}
	}

	respondWithJSON(w, http.StatusOK, posts)
}

func (h *github.com /Khmelov/Distcomp/351001/UshakovHandler) getgithub.com/Khmelov/Distcomp/351001/UshakovByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid ID format")
		return
	}

	ctx := r.Context()

	post, err := h.service.Getgithub.com / Khmelov / Distcomp / 351001 / UshakovByID(ctx, id)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to get post")
		return
	}

	respondWithJSON(w, http.StatusOK, post)
}

func (h *github.com /Khmelov/Distcomp/351001/UshakovHandler) creategithub.com/Khmelov/Distcomp/351001/Ushakov(w http.ResponseWriter, r *http.Request) {
	var msg postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov
	if err := json.NewDecoder(r.Body).Decode(&msg); err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	if err := msg.Validate(); err != nil {
		respondWithError(w, http.StatusBadRequest, err.Error())
		return
	}

	ctx := r.Context()

	createdgithub.com / Khmelov / Distcomp / 351001 / Ushakov, err := h.service.Creategithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, msg)
	if err != nil {
		fmt.Println(err.Error())
		respondWithError(w, http.StatusInternalServerError, "Failed to create post")
		return
	}

	respondWithJSON(w, http.StatusCreated, createdgithub.com/Khmelov/Distcomp/351001/Ushakov)
}

func (h *github.com /Khmelov/Distcomp/351001/UshakovHandler) deletegithub.com/Khmelov/Distcomp/351001/UshakovByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid ID format")
		return
	}

	ctx := r.Context()

	err = h.service.Deletegithub.com / Khmelov / Distcomp / 351001 / UshakovByID(ctx, id)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to delete post")
		return
	}

	respondWithJSON(w, http.StatusNoContent, map[string]string{"post": "github.com/Khmelov/Distcomp/351001/Ushakov deleted successfully"})
}

func (h *github.com /Khmelov/Distcomp/351001/UshakovHandler) updategithub.com/Khmelov/Distcomp/351001/UshakovByID(w http.ResponseWriter, r *http.Request) {
	var msg postModel.github.com / Khmelov / Distcomp / 351001 / Ushakov
	if err := json.NewDecoder(r.Body).Decode(&msg); err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	if err := msg.Validate(); err != nil {
		respondWithError(w, http.StatusBadRequest, err.Error())
		return
	}

	ctx := r.Context()

	updatedgithub.com / Khmelov / Distcomp / 351001 / Ushakov, err := h.service.Updategithub.com / Khmelov / Distcomp / 351001 / UshakovByID(ctx, msg)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to update post")
		return
	}

	respondWithJSON(w, http.StatusOK, updatedgithub.com/Khmelov/Distcomp/351001/Ushakov)
}

func respondWithError(w http.ResponseWriter, code int, post string) {
	respondWithJSON(w, code, map[string]string{"error": post})
}

func respondWithJSON(w http.ResponseWriter, code int, payload interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(code)
	if payload != nil {
		json.NewEncoder(w).Encode(payload)
	}
}

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

type PostHandler struct {
	service postService.PostService
}

func New(srv postService.PostService) *PostHandler {
	return &PostHandler{
		service: srv,
	}
}

func (h *PostHandler) InitRoutes(r *mux.Router) {
	r.HandleFunc("/comments", h.getPostsList).Methods(http.MethodGet)
	r.HandleFunc("/comments", h.createPost).Methods(http.MethodPost)
	r.HandleFunc("/comments/{id}", h.getPostByID).Methods(http.MethodGet)
	r.HandleFunc("/comments/{id}", h.deletePostByID).Methods(http.MethodDelete)
	r.HandleFunc("/comments", h.updatePostByID).Methods(http.MethodPut)
}

func (h *PostHandler) getPostsList(w http.ResponseWriter, r *http.Request) {
	ctx := r.Context()

	posts, err := h.service.GetPosts(ctx)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to get posts")
		return
	}

	if posts == nil {
		posts = []postModel.Post{}
	}

	respondWithJSON(w, http.StatusOK, posts)
}

func (h *PostHandler) getPostByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid ID format")
		return
	}

	ctx := r.Context()

	post, err := h.service.GetPostByID(ctx, id)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to get post")
		return
	}

	respondWithJSON(w, http.StatusOK, post)
}

func (h *PostHandler) createPost(w http.ResponseWriter, r *http.Request) {
	var msg postModel.Post
	if err := json.NewDecoder(r.Body).Decode(&msg); err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	if err := msg.Validate(); err != nil {
		respondWithError(w, http.StatusBadRequest, err.Error())
		return
	}

	ctx := r.Context()

	createdPost, err := h.service.CreatePost(ctx, msg)
	if err != nil {
		fmt.Println(err.Error())
		respondWithError(w, http.StatusInternalServerError, "Failed to create post")
		return
	}

	respondWithJSON(w, http.StatusCreated, createdPost)
}

func (h *PostHandler) deletePostByID(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	idStr := vars["id"]
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid ID format")
		return
	}

	ctx := r.Context()

	err = h.service.DeletePostByID(ctx, id)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to delete post")
		return
	}

	respondWithJSON(w, http.StatusNoContent, map[string]string{"post": "Post deleted successfully"})
}

func (h *PostHandler) updatePostByID(w http.ResponseWriter, r *http.Request) {
	var msg postModel.Post
	if err := json.NewDecoder(r.Body).Decode(&msg); err != nil {
		respondWithError(w, http.StatusBadRequest, "Invalid request body")
		return
	}

	if err := msg.Validate(); err != nil {
		respondWithError(w, http.StatusBadRequest, err.Error())
		return
	}

	ctx := r.Context()

	updatedPost, err := h.service.UpdatePostByID(ctx, msg)
	if err != nil {
		respondWithError(w, http.StatusInternalServerError, "Failed to update post")
		return
	}

	respondWithJSON(w, http.StatusOK, updatedPost)
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

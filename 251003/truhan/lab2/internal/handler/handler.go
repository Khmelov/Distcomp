package handler

import (
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/handler/author"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/handler/comment"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/handler/label"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/handler/tweet"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/service"
	"github.com/gorilla/mux"
	"net/http"
)

const apiPrefix = "/api/v1.0"

func New(srv service.Service) http.Handler {
	h := mux.NewRouter()

	api := h.PathPrefix(apiPrefix).Subrouter()

	author.New(srv.Author).InitRoutes(api)
	tweet.New(srv.Tweet, srv.Label).InitRoutes(api)
	comment.New(srv.Comment).InitRoutes(api)
	label.New(srv.Label).InitRoutes(api)

	return h
}

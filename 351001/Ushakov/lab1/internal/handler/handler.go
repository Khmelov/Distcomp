package handler

import (
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/handler/issue"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/handler/label"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/handler/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/handler/writer"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/service"
	"github.com/gorilla/mux"
	"net/http"
)

const apiPrefix = "/api/v1.0"

func New(srv service.Service) http.Handler {
	h := mux.NewRouter()

	api := h.PathPrefix(apiPrefix).Subrouter()

	writer.New(srv.Writer).InitRoutes(api)
	issue.New(srv.Issue).InitRoutes(api)
	post.New(srv.Post).InitRoutes(api)
	label.New(srv.Label).InitRoutes(api)

	return h
}

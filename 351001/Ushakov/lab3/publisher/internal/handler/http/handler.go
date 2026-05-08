package http

import (
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/handler/http/creator"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/handler/http/issue"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/handler/http/mark"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/handler/http/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/publisher/internal/service"
	"github.com/gorilla/mux"
)

const pathPrefix = "/api/v1.0"

func New(srv service.Service) *mux.Router {
	r := mux.NewRouter()
	api := r.PathPrefix(pathPrefix).Subrouter()

	creator := creator.New(srv.Creator)
	creator.InitRoutes(api)

	issue := issue.New(srv.Issue)
	issue.InitRoutes(api)

	post := post.New(srv.github.com / Khmelov / Distcomp / 351001 / Ushakov)
	post.InitRoutes(api)

	mark := mark.New(srv.Mark)
	mark.InitRoutes(api)

	return r
}

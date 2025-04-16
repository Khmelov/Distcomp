package http

import (
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/handler/http/creator"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/handler/http/issue"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/handler/http/mark"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/handler/http/message"
	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/service"
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

	message := message.New(srv.Message)
	message.InitRoutes(api)

	mark := mark.New(srv.Mark)
	mark.InitRoutes(api)

	return r
}

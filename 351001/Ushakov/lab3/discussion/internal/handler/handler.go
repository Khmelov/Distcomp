package handler

import (
	"net/http"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/handler/http/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/service"
	"github.com/gin-gonic/gin"
)

func New(svc service.Service) http.Handler {
	engine := gin.Default()

	router := engine.Group("/api")
	{
		post.New(svc).InitRoutes(router)
	}

	return engine
}

package handler

import (
	"net/http"

	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/label"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/news"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/notice"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/handler/http/writer"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/service"
	"github.com/gin-gonic/gin"
)

func New(svc *service.Service) http.Handler {
	router := gin.Default()

	writer := writer.New(svc)
	news := news.New(svc)
	notice := notice.New(svc)
	label := label.New(svc)

	r := router.Group("/api")
	{
		writer.InitRoutes(r)
		notice.InitRoutes(r)
		news.InitRoutes(r)
		label.InitRoutes(r)
	}

	return router
}

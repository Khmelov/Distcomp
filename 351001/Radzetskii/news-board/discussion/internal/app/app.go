package app

import (
	"log"
	"net/http"
	"news-board/discussion/internal/api/controllers"
	apiErrors "news-board/discussion/internal/api/errors"
	"news-board/discussion/internal/config"
	"news-board/discussion/internal/service"
	"news-board/discussion/internal/storage/cassandra/repository"
	cassdb "news-board/discussion/pkg/store/cassandra"

	"github.com/gin-gonic/gin"
)

func Run() {
	cfg := config.Load("../infra/env/discussion.env")

	session, err := cassdb.NewSession(&cassdb.Config{
		Host:     cfg.Host,
		Port:     cfg.Port,
		Keyspace: cfg.Keyspace,
	})
	if err != nil {
		log.Fatal("Failed to connect to Cassandra:", err)
	}
	defer session.Close()

	noticeRepo := repository.NewNoticeRepository(session)
	noticeSvc := service.NewNoticeService(noticeRepo)

	router := gin.Default()
	router.Use(apiErrors.ErrorHandler())

	api := router.Group("/api")
	{
		controllers.NewNoticeHandler(noticeSvc).RegisterRoutes(api)
	}

	router.NoRoute(func(c *gin.Context) {
		c.JSON(http.StatusNotFound, gin.H{
			"errorMessage": "Endpoint not found",
			"errorCode":    "40400",
		})
	})

	log.Println("Discussion service starting on", cfg.Address)
	if err := router.Run(cfg.Address); err != nil {
		log.Fatal("Discussion service failed:", err)
	}
}

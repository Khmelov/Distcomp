package app

import (
	"context"
	"log"
	"net/http"
	"news-board/discussion/internal/api/controllers"
	apiErrors "news-board/discussion/internal/api/errors"
	"news-board/discussion/internal/config"
	"news-board/discussion/internal/service"
	"news-board/discussion/internal/storage/cassandra/repository"
	cassdb "news-board/discussion/pkg/store/cassandra"
	"os"
	"os/signal"
	"syscall"
	"time"

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

	kafkaProducer, err := service.NewKafkaProducer(cfg.KafkaBrokers)
	if err != nil {
		log.Fatal("Failed to create Kafka producer:", err)
	}
	defer kafkaProducer.Close()

	noticeRepo := repository.NewNoticeRepository(session)
	noticeSvc := service.NewNoticeService(noticeRepo)

	kafkaConsumer, err := service.NewKafkaConsumer(cfg.KafkaBrokers, cfg.KafkaGroup, noticeSvc, kafkaProducer)
	if err != nil {
		log.Fatal("Failed to create Kafka consumer:", err)
	}
	defer kafkaConsumer.Close()

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()
	kafkaConsumer.Start(ctx)

	router := gin.Default()
	router.Use(apiErrors.ErrorHandler())

	api := router.Group("/api")
	{
		controllers.NewNoticeHandler(noticeSvc).RegisterRoutes(api)
	}

	srv := &http.Server{
		Addr:    cfg.Address,
		Handler: router,
	}

	go func() {
		log.Println("Discussion service starting on", cfg.Address)
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Discussion service failed: %v", err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Println("Shutting down discussion service...")
	cancel()

	shutdownCtx, shutdownCancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer shutdownCancel()

	if err := srv.Shutdown(shutdownCtx); err != nil {
		log.Fatal("Server forced to shutdown:", err)
	}

	log.Println("Server exiting")
}

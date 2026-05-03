package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"distcomp/internal/apperrors"
	"distcomp/internal/discussion/repository/cassandra"
	"distcomp/internal/discussion/service"
	v1 "distcomp/internal/discussion/transport/http/v1"
	cassClient "distcomp/pkg/client/cassandra"

	"github.com/gin-gonic/gin"
)

func main() {
	host := os.Getenv("CASSANDRA_HOST")
	keyspace := os.Getenv("CASSANDRA_KEYSPACE")
	port := os.Getenv("DISCUSSION_PORT")

	if host == "" {
		host = "localhost"
	}
	if keyspace == "" {
		keyspace = "distcomp"
	}
	if port == "" {
		port = "24130"
	}

	session, err := cassClient.NewClient(cassClient.Config{
		Host:     host,
		Keyspace: keyspace,
	})
	if err != nil {
		log.Fatalf("Failed to connect to Cassandra: %v", err)
	}
	defer session.Close()

	repo := cassandra.NewCommentStorage(session)
	srv := service.NewCommentService(repo)

	gin.SetMode(gin.ReleaseMode)
	router := gin.Default()

	router.HandleMethodNotAllowed = true
	router.NoRoute(func(c *gin.Context) {
		c.JSON(http.StatusNotFound, apperrors.New("endpoint not found", apperrors.CodeNotFound))
	})
	router.NoMethod(func(c *gin.Context) {
		c.JSON(http.StatusMethodNotAllowed, apperrors.New("method not allowed", apperrors.CodeBadRequest))
	})

	handlers := v1.NewHandler(srv)
	api := router.Group("/api")
	handlers.InitRoutes(api)

	addr := fmt.Sprintf("0.0.0.0:%s", port)
	server := &http.Server{
		Addr:    addr,
		Handler: router,
	}

	go func() {
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("failed to start server: %v", err)
		}
	}()

	log.Printf("Discussion server started on %s", addr)

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Println("Shutting down server...")
	shutdownCtx, cancelShutdown := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancelShutdown()

	if err := server.Shutdown(shutdownCtx); err != nil {
		log.Printf("Server forced to shutdown: %v", err)
	}

	log.Println("Server exiting")
}
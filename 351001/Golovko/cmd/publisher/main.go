package main

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"distcomp/internal/apperrors"
	"distcomp/internal/config"
	"distcomp/internal/publisher/repository/postgres"
	"distcomp/internal/publisher/service"
	v1 "distcomp/internal/publisher/transport/http/v1"
	"distcomp/pkg/client/postgresql"
	redisClient "distcomp/pkg/client/redis"
	"distcomp/pkg/logger"

	_ "distcomp/docs"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

func main() {
	cfg := config.Load()
	log := logger.SetupLogger(cfg.Env)

	ctx, cancelInit := context.WithTimeout(context.Background(), 20*time.Second)
	defer cancelInit()

	dbCfg := postgresql.Config{
		Host:     cfg.DBHost,
		Port:     cfg.DBPort,
		User:     cfg.DBUser,
		Password: cfg.DBPass,
		DBName:   cfg.DBName,
		Schema:   cfg.DBSchema,
	}
	db, err := postgresql.NewClient(ctx, dbCfg, log)
	if err != nil {
		log.Error("Failed to initialize database client", slog.Any("error", err))
		os.Exit(1)
	}
	defer db.Close()

	redisHost := os.Getenv("REDIS_HOST")
	if redisHost == "" {
		redisHost = "localhost:6379"
	}
	rdb, err := redisClient.NewClient(ctx, redisHost)
	if err != nil {
		log.Error("Failed to connect to Redis", slog.Any("error", err))
		os.Exit(1)
	}
	defer rdb.Close()

	kafkaBrokersEnv := os.Getenv("KAFKA_BROKERS")
	if kafkaBrokersEnv == "" {
		kafkaBrokersEnv = "localhost:9092"
	}
	brokers := strings.Split(kafkaBrokersEnv, ",")

	storage := postgres.NewStorage(db)
	services := service.NewManager(storage, brokers, rdb)

	if cfg.Env == "prod" {
		gin.SetMode(gin.ReleaseMode)
	}
	router := gin.Default()

	router.HandleMethodNotAllowed = true
	router.NoRoute(func(c *gin.Context) {
		c.JSON(http.StatusNotFound, apperrors.New("endpoint not found", apperrors.CodeNotFound))
	})
	router.NoMethod(func(c *gin.Context) {
		c.JSON(http.StatusMethodNotAllowed, apperrors.New("method not allowed", apperrors.CodeBadRequest))
	})

	router.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	handlers := v1.NewHandler(services)
	api := router.Group("/api")
	handlers.InitRoutes(api)

	port := os.Getenv("PUBLISHER_PORT")
	if port == "" {
		port = "24110"
	}

	addr := fmt.Sprintf("0.0.0.0:%s", port)
	srv := &http.Server{
		Addr:    addr,
		Handler: router,
	}

	go func() {
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Error("failed to start server", slog.Any("error", err))
		}
	}()

	log.Info("Publisher server started", slog.String("address", addr))

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Info("shutting down server...")
	shutdownCtx, cancelShutdown := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancelShutdown()

	if err := srv.Shutdown(shutdownCtx); err != nil {
		log.Error("server forced to shutdown", slog.Any("error", err))
	}
	log.Info("server exiting")
}
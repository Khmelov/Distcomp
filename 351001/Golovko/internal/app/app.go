package app

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"distcomp/internal/apperrors"
	"distcomp/internal/config"
	"distcomp/internal/repository/postgres"
	"distcomp/internal/service"
	v1 "distcomp/internal/transport/http/v1"
	"distcomp/pkg/client/postgresql"
	"distcomp/pkg/logger"

	_ "distcomp/docs"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

type App struct {
	cfg      *config.Config
	logger   *slog.Logger
	router   *gin.Engine
	services *service.Manager
	db       *sql.DB
}

func Run() error {
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
		return err
	}

	storage := postgres.NewStorage(db)
	services := service.NewManager(storage)

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

	app := &App{
		cfg:      cfg,
		logger:   log,
		router:   router,
		services: services,
		db:       db,
	}

	addr := fmt.Sprintf("%s:%s", app.cfg.Host, app.cfg.Port)
	srv := &http.Server{
		Addr:    addr,
		Handler: app.router,
	}

	go func() {
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			app.logger.Error("failed to start server", slog.Any("error", err))
			os.Exit(1)
		}
	}()

	app.logger.Info("server started", slog.String("address", addr))

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	app.logger.Info("shutting down server...")

	shutdownCtx, cancelShutdown := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancelShutdown()

	if err := srv.Shutdown(shutdownCtx); err != nil {
		app.logger.Error("server forced to shutdown", slog.Any("error", err))
	}
	if err := app.db.Close(); err != nil {
		app.logger.Error("database connection close error", slog.Any("error", err))
	}

	app.logger.Info("server exiting")
	return nil
}
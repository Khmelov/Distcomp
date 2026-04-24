package app

import (
	"context"
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
	"distcomp/internal/repository/memory"
	"distcomp/internal/service"
	v1 "distcomp/internal/transport/http/v1"
	"distcomp/pkg/logger"

	"github.com/gin-gonic/gin"
)

type App struct {
	cfg      *config.Config
	logger   *slog.Logger
	router   *gin.Engine
	services *service.Manager
}

func Run() error {
	cfg := config.Load()
	log := logger.SetupLogger(cfg.Env)

	storage := memory.NewStorage()
	services := service.NewManager(storage)

	if cfg.Env == "prod" {
		gin.SetMode(gin.ReleaseMode)
	}
	router := gin.Default()

	// Настройка глобальных JSON-ошибок для неизвестных роутов
	router.HandleMethodNotAllowed = true
	router.NoRoute(func(c *gin.Context) {
		c.JSON(http.StatusNotFound, apperrors.New("endpoint not found", apperrors.CodeNotFound))
	})
	router.NoMethod(func(c *gin.Context) {
		c.JSON(http.StatusMethodNotAllowed, apperrors.New("method not allowed", apperrors.CodeBadRequest))
	})

	handlers := v1.NewHandler(services)
	api := router.Group("/api")
	handlers.InitRoutes(api)

	app := &App{
		cfg:      cfg,
		logger:   log,
		router:   router,
		services: services,
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

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		app.logger.Error("server forced to shutdown", slog.Any("error", err))
		return err
	}

	app.logger.Info("server exiting")
	return nil
}
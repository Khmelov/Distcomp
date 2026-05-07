package app

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"net/http"
	"time"

	"publisher/internal/config"
	"publisher/internal/gateway"
	"publisher/internal/repository"
	"publisher/internal/service"
	"publisher/internal/transport/handler"
	"publisher/pkg/postgres"

	_ "github.com/lib/pq"
	"github.com/pressly/goose/v3"
	"go.uber.org/zap"
)

func Run(ctx context.Context, logger *zap.Logger) error {
	cfg, err := config.New()
	if err != nil {
		return fmt.Errorf("load config: %w", err)
	}
	if err := cfg.Validate(); err != nil {
		return fmt.Errorf("validate config: %w", err)
	}

	db, err := sql.Open("postgres", cfg.GooseDBString)
	if err != nil {
		return fmt.Errorf("open db: %w", err)
	}
	defer db.Close()

	if err := db.PingContext(ctx); err != nil {
		return fmt.Errorf("ping db: %w", err)
	}

	if _, err = db.ExecContext(ctx, "CREATE SCHEMA IF NOT EXISTS distcomp;"); err != nil {
		return fmt.Errorf("create schema: %w", err)
	}

	goose.SetTableName("distcomp.schema_migrations")
	if err := goose.SetDialect("postgres"); err != nil {
		return fmt.Errorf("set goose dialect: %w", err)
	}
	if err := goose.Up(db, "migrations"); err != nil {
		return fmt.Errorf("run migrations: %w", err)
	}
	logger.Info("migrations applied")

	pool, err := postgres.NewPool(ctx, &postgres.Config{
		Host:     cfg.PostgresHost,
		Port:     cfg.PostgresPort,
		Username: cfg.PostgresUser,
		Password: cfg.PostgresPass,
		Database: cfg.PostgresDB,
	})
	if err != nil {
		return fmt.Errorf("create pool: %w", err)
	}
	defer pool.Close()

	if err := pool.Ping(ctx); err != nil {
		return fmt.Errorf("ping pool: %w", err)
	}

	userRepo := repository.NewUserRepository(pool)
	issueRepo := repository.NewIssueRepository(pool)
	labelRepo := repository.NewLabelRepository(pool)

	discussionClient := gateway.NewDiscussionClient(cfg.DiscussionURL)

	mapper := service.NewMapper()
	reactionService := service.NewReactionService(discussionClient)
	userService := service.NewUserService(userRepo, mapper)
	issueService := service.NewIssueService(issueRepo, userRepo, labelRepo, reactionService, mapper)
	labelService := service.NewLabelService(labelRepo, mapper)

	h := handler.NewHandler(userService, issueService, labelService, reactionService)
	mux := http.NewServeMux()
	h.RegisterRoutes(mux)

	server := &http.Server{
		Addr:    fmt.Sprintf(":%s", cfg.HTTPport),
		Handler: mux,
	}

	go func() {
		logger.Info("publisher listening", zap.String("addr", server.Addr))
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			logger.Fatal("listen failed", zap.Error(err))
		}
	}()

	<-ctx.Done()
	logger.Info("shutting down publisher")

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		return fmt.Errorf("shutdown: %w", err)
	}

	logger.Info("publisher stopped")
	return nil
}

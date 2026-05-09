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
	gwcache "publisher/internal/gateway/cache"
	kafkagateway "publisher/internal/gateway/kafka"
	kafkahandler "publisher/internal/handler/kafka"
	"publisher/internal/repository"
	repocache "publisher/internal/repository/cache"
	"publisher/internal/service"
	"publisher/internal/transport/handler"
	handlerv2 "publisher/internal/transport/handler/v2"
	"publisher/internal/auth"
	"publisher/pkg/postgres"

	_ "github.com/jackc/pgx/v5/stdlib"
	"github.com/pressly/goose/v3"
	"github.com/redis/go-redis/v9"
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

	db, err := sql.Open("pgx", cfg.GooseDBString)
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

	rdb := redis.NewClient(&redis.Options{Addr: cfg.RedisAddr})
	defer rdb.Close()

	userRepo := repocache.NewCachedUserRepository(repository.NewUserRepository(pool), rdb)
	issueRepo := repocache.NewCachedIssueRepository(repository.NewIssueRepository(pool), rdb)
	labelRepo := repocache.NewCachedLabelRepository(repository.NewLabelRepository(pool), rdb)
	reactionRepo := repository.NewReactionRepository(pool)

	if err := kafkagateway.EnsureTopic(cfg.KafkaBroker, cfg.KafkaInTopic, 3, 1); err != nil {
		logger.Warn("ensure InTopic failed", zap.Error(err))
	}
	if err := kafkagateway.EnsureTopic(cfg.KafkaBroker, cfg.KafkaOutTopic, 3, 1); err != nil {
		logger.Warn("ensure OutTopic failed", zap.Error(err))
	}

	partCount, err := kafkagateway.LookupPartitionCount(cfg.KafkaBroker, cfg.KafkaInTopic)
	if err != nil || partCount == 0 {
		partCount = 3
	}
	kafkaProducer := kafkagateway.NewReactionProducer(cfg.KafkaBroker, cfg.KafkaInTopic, partCount, logger)
	defer kafkaProducer.Close()

	discussionClient := gateway.NewDiscussionClient(cfg.DiscussionURL)
	reactionGW := gwcache.NewCachedReactionGateway(discussionClient, rdb)

	mapper := service.NewMapper()
	reactionService := service.NewReactionService(reactionGW, issueRepo, kafkaProducer, logger)
	userService := service.NewUserService(userRepo, mapper)
	issueService := service.NewIssueService(issueRepo, userRepo, labelRepo, reactionService, mapper)
	labelService := service.NewLabelService(labelRepo, mapper)

	consumer := kafkahandler.NewReactionConsumer(
		reactionRepo,
		cfg.KafkaBroker,
		cfg.KafkaOutTopic,
		cfg.KafkaGroupID,
		logger,
	)
	go consumer.Run(ctx)

	h := handler.NewHandler(userService, issueService, labelService, reactionService)
	mux := http.NewServeMux()
	h.RegisterRoutes(mux)

	jwtSvc := auth.NewJWTService(cfg.JWTSecret)
	authSvc := auth.NewAuthService(repository.NewUserRepository(pool), jwtSvc)
	hv2 := handlerv2.NewHandlerV2(userService, issueService, labelService, reactionService, authSvc, jwtSvc)
	hv2.RegisterRoutes(mux)

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

package app

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"path/filepath"
	"regexp"
	"syscall"
	"time"

	"news-board/publisher/internal/api/controllers"
	"news-board/publisher/internal/config"
	"news-board/publisher/internal/service"
	"news-board/publisher/internal/storage/postgres/repository"
	dbpkg "news-board/publisher/pkg/store/pg"

	apiErrors "news-board/publisher/internal/api/errors"

	"github.com/gin-gonic/gin"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/redis/go-redis/v9"
)

func Run() {
	cfg := config.Load("../infra/env/.env")

	dbCfg := &dbpkg.Config{
		Host:     cfg.DBHost,
		Port:     cfg.DBPort,
		User:     cfg.DBUser,
		Password: cfg.DBPassword,
		DBName:   cfg.DBName,
		Schema:   cfg.DBSchema,
		SSLMode:  cfg.DBSSLMode,
	}
	pool, err := dbpkg.NewPostgresPool(dbCfg)
	if err != nil {
		log.Fatal("Failed to connect to database:", err)
	}
	defer pool.Close()

	if err := runMigrations(pool, cfg.DBSchema); err != nil {
		log.Fatal("Failed to run migrations:", err)
	}

	rdb := redis.NewClient(&redis.Options{
		Addr: cfg.RedisAddr,
	})
	defer rdb.Close()

	pingCtx, cancelPing := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancelPing()
	if err := rdb.Ping(pingCtx).Err(); err != nil {
		log.Fatal("Failed to connect to Redis:", err)
	}
	log.Printf("Connected to Redis at %s", cfg.RedisAddr)

	kafkaProducer, err := service.NewKafkaProducer(cfg.KafkaBrokers)
	if err != nil {
		log.Fatal("Failed to connect to Kafka:", err)
	}
	defer kafkaProducer.Close()

	userRepo := repository.NewUserRepository(pool)
	newsRepo := repository.NewNewsRepository(pool)
	markerRepo := repository.NewMarkerRepository(pool)

	userSvc := service.NewUserService(userRepo)
	newsSvc := service.NewNewsService(newsRepo, markerRepo)
	markerSvc := service.NewMarkerService(markerRepo)

	noticeSvc := service.NewNoticeService(kafkaProducer, newsRepo, cfg.DiscussionBaseURL, rdb)

	replyConsumer := service.NewKafkaConsumer(cfg.KafkaBrokers, cfg.KafkaGroup, service.OutTopic, noticeSvc)
	replyConsumer.StartReplyListener(context.Background())
	defer replyConsumer.Close()

	r := gin.Default()
	r.Use(apiErrors.ErrorHandler())

	api := r.Group("/api")
	{
		controllers.NewUserHandler(userSvc).RegisterRoutes(api)
		controllers.NewNewsHandler(newsSvc).RegisterRoutes(api)
		controllers.NewMarkerHandler(markerSvc).RegisterRoutes(api)
		controllers.NewNoticeHandler(noticeSvc).RegisterRoutes(api)
	}

	r.NoRoute(func(c *gin.Context) {
		c.JSON(http.StatusNotFound, gin.H{
			"errorMessage": "Endpoint not found",
			"errorCode":    "40400",
		})
	})

	srv := &http.Server{
		Addr:    ":" + cfg.AppPort,
		Handler: r,
	}

	go func() {
		log.Printf("Server starting on port %s", cfg.AppPort)
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Server failed: %v", err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	log.Println("Shutting down server...")

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Fatal("Server forced to shutdown:", err)
	}

	log.Println("Server exiting")
}

func runMigrations(pool *pgxpool.Pool, schema string) error {
	migrationFile, err := resolveMigrationFile("000001_init_schema.up.sql")
	if err != nil {
		return err
	}
	content, err := os.ReadFile(migrationFile)
	if err != nil {
		return err
	}

	ctx := context.Background()
	if !isSafeIdentifier(schema) {
		return fmt.Errorf("invalid schema name: %q", schema)
	}

	if _, err := pool.Exec(ctx, fmt.Sprintf("CREATE SCHEMA IF NOT EXISTS %s", schema)); err != nil {
		return err
	}

	if _, err := pool.Exec(ctx, fmt.Sprintf("SET search_path TO %s", schema)); err != nil {
		return err
	}

	if _, err := pool.Exec(ctx, string(content)); err != nil {
		return err
	}

	log.Println("Migrations applied successfully")
	return nil
}

var identifierPattern = regexp.MustCompile(`^[A-Za-z_][A-Za-z0-9_]*$`)

func isSafeIdentifier(value string) bool {
	return identifierPattern.MatchString(value)
}

func resolveMigrationFile(name string) (string, error) {
	candidates := []string{
		filepath.Join("publisher", "migrations", name),
		filepath.Join("migrations", name),
		filepath.Join("..", "publisher", "migrations", name),
		filepath.Join("..", "migrations", name),
	}

	for _, candidate := range candidates {
		if _, err := os.Stat(candidate); err == nil {
			return candidate, nil
		}
	}

	return "", fmt.Errorf("migration file %q not found", name)
}

package app

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"regexp"

	"news-board/publisher/internal/api/controllers"
	"news-board/publisher/internal/config"
	"news-board/publisher/internal/service"
	"news-board/publisher/internal/storage/postgres/repository"
	dbpkg "news-board/publisher/pkg/store/pg"

	apiErrors "news-board/publisher/internal/api/errors"

	"github.com/gin-gonic/gin"
	"github.com/jackc/pgx/v5/pgxpool"
)

// p.s. I know that there are many logical mistakes especially in data processing (i mean requests with 1st get and update than)
// it could be enough just to add constraints and check on violation
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

	userRepo := repository.NewUserRepository(pool)
	newsRepo := repository.NewNewsRepository(pool)
	markerRepo := repository.NewMarkerRepository(pool)
	userSvc := service.NewUserService(userRepo)
	newsSvc := service.NewNewsService(newsRepo, markerRepo)
	markerSvc := service.NewMarkerService(markerRepo)
	noticeSvc := service.NewNoticeService(cfg.DiscussionBaseURL, newsRepo)

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

	log.Println("Server starting on :24110")
	if err := r.Run(":24110"); err != nil {
		log.Fatal("Server failed:", err)
	}
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

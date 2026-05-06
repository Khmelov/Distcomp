package postgresql

import (
	"context"
	"database/sql"
	"fmt"
	"log/slog"
	"time"

	_ "github.com/lib/pq"
)

type Config struct {
	Host     string
	Port     string
	User     string
	Password string
	DBName   string
	Schema   string
}

func NewClient(ctx context.Context, cfg Config, logger *slog.Logger) (*sql.DB, error) {
	dsn := fmt.Sprintf("host=%s port=%s user=%s password=%s dbname=%s sslmode=disable search_path=%s",
		cfg.Host, cfg.Port, cfg.User, cfg.Password, cfg.DBName, cfg.Schema)

	var db *sql.DB
	var err error

	maxRetries := 5
	for i := 0; i < maxRetries; i++ {
		db, err = sql.Open("postgres", dsn)
		if err == nil {
			err = db.PingContext(ctx)
			if err == nil {
				return db, nil
			}
		}

		logger.Warn("Failed to connect to database, retrying...", slog.Int("attempt", i+1), slog.Any("error", err))
		time.Sleep(3 * time.Second)
	}

	return nil, fmt.Errorf("failed to connect to database after %d attempts: %w", maxRetries, err)
}
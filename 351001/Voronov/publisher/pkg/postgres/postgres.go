package postgres

import (
	"context"
	"fmt"

	"github.com/jackc/pgx/v5/pgxpool"
)

type Config struct {
	Host     string
	Port     uint16
	Username string
	Password string
	Database string
}

func (c *Config) DSN() string {
	return fmt.Sprintf(
		"postgres://%s:%s@%s:%d/%s?sslmode=disable&search_path=distcomp",
		c.Username, c.Password, c.Host, c.Port, c.Database,
	)
}

func NewPool(ctx context.Context, cfg *Config) (*pgxpool.Pool, error) {
	pool, err := pgxpool.New(ctx, cfg.DSN())
	if err != nil {
		return nil, fmt.Errorf("create pgxpool: %w", err)
	}
	return pool, nil
}

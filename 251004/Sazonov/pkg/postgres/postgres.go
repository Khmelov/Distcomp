package postgres

import (
	"context"
	"fmt"

	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/jackc/pgx/v5/stdlib"
	"github.com/jmoiron/sqlx"
)

type Config struct {
	User     string
	Password string
	Host     string
	Port     string
	DBName   string
	SSLMode  string
}

func (c Config) ConnectionString() string {
	return fmt.Sprintf(
		"postgres://%s:%s@%s:%s/%s?sslmode=%s",
		c.User,
		c.Password,
		c.Host,
		c.Port,
		c.DBName,
		c.SSLMode,
	)
}

func Connect(ctx context.Context, cfg Config) (*sqlx.DB, error) {
	pool, err := pgxpool.New(ctx, cfg.ConnectionString())
	if err != nil {
		return nil, err
	}

	db := sqlx.NewDb(stdlib.OpenDBFromPool(pool), "pgx")

	return db, nil
}

package bootstrap

import (
	"context"
	"github.com/jackc/pgx/v5/pgxpool"
	"github.com/jackc/pgx/v5/stdlib"
	"go.uber.org/fx"
	"go.uber.org/zap"

	"note-service/internal/config"
	"note-service/internal/pkg/migrator"
	"note-service/migrations"
)

func NewPostgresPool(lc fx.Lifecycle, cfg config.Config, log *zap.Logger) *pgxpool.Pool {
	poolConfig, err := pgxpool.ParseConfig(cfg.PGConfig.URL)
	if err != nil {
		log.Fatal("Failed to parse PG config", zap.Error(err))
	}

	pool, err := pgxpool.NewWithConfig(context.Background(), poolConfig)
	if err != nil {
		log.Fatal("Failed to connect to Postgres", zap.Error(err))
	}

	stdDB := stdlib.OpenDBFromPool(pool)
	m := migrator.NewMigrator(stdDB, migrations.FS, log)
	if err := m.Up(); err != nil {
		log.Fatal("Failed to apply database migrations", zap.Error(err))
	}

	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			return pool.Ping(ctx)
		},
		OnStop: func(ctx context.Context) error {
			pool.Close()
			return nil
		},
	})

	return pool
}

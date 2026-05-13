package migrator

import (
	"context"
	"database/sql"
	"embed"
	"errors"
	"fmt"
	"go.uber.org/zap"
	"time"

	"github.com/cenkalti/backoff/v4"
	"github.com/pressly/goose/v3"
	"github.com/pressly/goose/v3/lock"
)

type Migrator struct {
	fs   embed.FS
	logg zap.Logger
	db   *sql.DB
}

func NewMigrator(db *sql.DB, fs embed.FS, logg *zap.Logger) Migrator {
	return Migrator{
		db:   db,
		fs:   fs,
		logg: *logg,
	}
}

func (m Migrator) Up() error {
	err := m.ping(m.db)
	if err != nil {
		return err
	}

	locker, err := lock.NewPostgresSessionLocker()
	if err != nil {
		return fmt.Errorf("new locker: %w", err)
	}

	prov, err := goose.NewProvider(goose.DialectPostgres, m.db, m.fs, goose.WithSessionLocker(locker))
	if err != nil {
		return fmt.Errorf("new provider: %w", err)
	}

	m.logg.Info("Starting migration...")

	if _, err := prov.Up(context.Background()); err != nil {
		if errors.Is(err, goose.ErrNoNextVersion) {
			m.logg.Info("No migrations to apply")
			return nil
		}
		m.logg.Error("Migration failed", zap.Error(err))
		return fmt.Errorf("migration error: %w", err)
	}

	m.logg.Info("Migration succeeded")
	return nil
}

func (m Migrator) ping(stdDB *sql.DB) error {
	expBackoff := backoff.NewExponentialBackOff()
	expBackoff.InitialInterval = 2 * time.Second
	expBackoff.MaxInterval = 5 * time.Second
	expBackoff.MaxElapsedTime = 5 * time.Minute
	if err := backoff.Retry(func() error {
		if err := stdDB.Ping(); err != nil {
			m.logg.Warn("database connection issue, retrying...", zap.Error(err))
			return err
		}
		return nil
	}, expBackoff); err != nil {
		return fmt.Errorf("database ping attempts failed: %w", err)
	}
	return nil
}

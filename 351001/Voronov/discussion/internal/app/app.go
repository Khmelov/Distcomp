package app

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"time"

	"discussion/internal/config"
	"discussion/internal/handler"
	"discussion/internal/repository"
	"discussion/internal/service"

	"github.com/gocql/gocql"
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

	session, err := connectCassandra(cfg)
	if err != nil {
		return fmt.Errorf("cassandra: %w", err)
	}
	defer session.Close()
	logger.Info("connected to Cassandra", zap.String("host", cfg.CassandraHost))

	if err := initSchema(session, cfg.CassandraDB); err != nil {
		return fmt.Errorf("init schema: %w", err)
	}

	repo := repository.NewCassandraRepository(session)
	svc := service.NewReactionService(repo)
	h := handler.New(svc)

	mux := http.NewServeMux()
	h.RegisterRoutes(mux)

	server := &http.Server{
		Addr:    fmt.Sprintf(":%s", cfg.HTTPPort),
		Handler: mux,
	}

	go func() {
		logger.Info("discussion listening", zap.String("addr", server.Addr))
		if err := server.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			logger.Fatal("listen failed", zap.Error(err))
		}
	}()

	<-ctx.Done()
	logger.Info("shutting down discussion")

	shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := server.Shutdown(shutdownCtx); err != nil {
		return fmt.Errorf("shutdown: %w", err)
	}

	logger.Info("discussion stopped")
	return nil
}

func connectCassandra(cfg *config.Config) (*gocql.Session, error) {
	cluster := gocql.NewCluster(cfg.CassandraHost)
	cluster.Port = cfg.CassandraPort
	cluster.Consistency = gocql.Quorum
	return cluster.CreateSession()
}

func initSchema(session *gocql.Session, keyspace string) error {
	if err := session.Query(fmt.Sprintf(
		`CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy','replication_factor':1}`,
		keyspace,
	)).Exec(); err != nil {
		return err
	}
	return session.Query(fmt.Sprintf(
		`CREATE TABLE IF NOT EXISTS %s.tbl_reaction (id bigint PRIMARY KEY, issue_id bigint, content text)`,
		keyspace,
	)).Exec()
}

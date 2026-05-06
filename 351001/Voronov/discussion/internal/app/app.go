package app

import (
	"context"
	"errors"
	"fmt"
	"net/http"
	"time"

	"discussion/internal/config"
	"discussion/internal/handler"
	kafkagateway "discussion/internal/gateway/kafka"
	kafkahandler "discussion/internal/handler/kafka"
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

	// Kafka topic provisioning
	if err := kafkagateway.EnsureTopic(cfg.KafkaBroker, cfg.KafkaInTopic, 3, 1); err != nil {
		logger.Warn("ensure InTopic failed", zap.Error(err))
	}
	if err := kafkagateway.EnsureTopic(cfg.KafkaBroker, cfg.KafkaOutTopic, 3, 1); err != nil {
		logger.Warn("ensure OutTopic failed", zap.Error(err))
	}

	// Kafka producer (OutTopic)
	partCount, err := kafkagateway.LookupPartitionCount(cfg.KafkaBroker, cfg.KafkaOutTopic)
	if err != nil || partCount == 0 {
		partCount = 3
	}
	kafkaProducer := kafkagateway.NewReactionProducer(cfg.KafkaBroker, cfg.KafkaOutTopic, partCount, logger)
	defer kafkaProducer.Close()

	// Kafka consumer (InTopic) — background goroutine
	consumer := kafkahandler.NewReactionConsumer(
		svc,
		kafkaProducer,
		cfg.KafkaBroker,
		cfg.KafkaInTopic,
		cfg.KafkaGroupID,
		logger,
	)
	go consumer.Run(ctx)

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
	if err := session.Query(fmt.Sprintf(
		`CREATE TABLE IF NOT EXISTS %s.tbl_reaction (
			id bigint PRIMARY KEY,
			issue_id bigint,
			content text,
			state text
		)`, keyspace,
	)).Exec(); err != nil {
		return err
	}
	// Add state column for existing tables (ignore AlreadyExists error)
	_ = session.Query(fmt.Sprintf(
		`ALTER TABLE %s.tbl_reaction ADD state text`, keyspace,
	)).Exec()
	return nil
}

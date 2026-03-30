package app

import (
	"context"
	"errors"
	"fmt"
	kafkacontroller "labs/discussion/internal/controller/kafka"
	kafkarepository "labs/discussion/internal/repository/kafka"
	kafkaservice "labs/discussion/internal/service/kafka"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"labs/discussion/internal/config"
	"labs/discussion/internal/controller"
	"labs/discussion/internal/repository"
	"labs/discussion/internal/service"
	"labs/shared/middleware"

	"github.com/gin-gonic/gin"
	"github.com/gocql/gocql"
	"github.com/scylladb/gocqlx/v2"
)

type App struct {
	cfg *config.Config
}

func New(cfg *config.Config) *App {
	return &App{
		cfg: cfg,
	}
}

func (a *App) Run() error {
	cluster := gocql.NewCluster(a.cfg.CassandraHosts...)

	cluster.Port = a.cfg.CassandraPort
	cluster.Keyspace = a.cfg.CassandraKeyspace

	cluster.Consistency = gocql.One

	cluster.PoolConfig.HostSelectionPolicy = gocql.TokenAwareHostPolicy(gocql.RoundRobinHostPolicy())

	cluster.ConnectTimeout = 5 * time.Second

	session, err := gocqlx.WrapSession(cluster.CreateSession())
	if err != nil {
		return fmt.Errorf("failed to connect to cassandra cluster: %w", err)
	}
	defer session.Close()

	log.Println("Successfully connected to Cassandra!")
	repos := repository.NewCas(session)
	services := service.New(repos)
	controllers := controller.New(services)

	kafkaRepository := kafkarepository.NewReplyRepository(a.cfg.Brokers)
	kafkaService := kafkaservice.NewService(repos, kafkaRepository)
	kafkaController := kafkacontroller.NewConsumerController(a.cfg.Brokers, kafkaService)

	router := gin.New()
	router.Use(gin.Recovery())
	router.Use(middleware.RequestLogger())

	router.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"status": "ok", "time": time.Now().Format(time.RFC3339)})
	})

	api := router.Group("/api/v1.0")
	controllers.RegisterRoutes(api)

	addr := fmt.Sprintf(":%d", a.cfg.ServerPort)

	srv := &http.Server{
		Addr:    addr,
		Handler: router,
	}

	go func() {
		log.Printf("Starting HTTP server on %s", addr)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("Listen error: %s\n", err)
		}
	}()

	ctx, cancel := context.WithCancel(context.Background())
	go func() {
		log.Printf("Starting kafka server on %s", a.cfg.ServerPort)
		kafkaController.Start(ctx)
	}()
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	cancel()

	log.Println("Shutting down server...")

	ctx, cancel = context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Printf("Server forced to shutdown: %v", err)
		return err
	}

	log.Println("Server exiting")
	return nil
}

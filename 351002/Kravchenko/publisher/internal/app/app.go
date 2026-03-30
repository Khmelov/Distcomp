package app

import (
	"context"
	"database/sql"
	"errors"
	"fmt"
	"labs/publisher/internal/client/kafka"
	"labs/publisher/internal/config"
	editorctrl "labs/publisher/internal/controller/editor"
	issuectrl "labs/publisher/internal/controller/issue"
	notectrl "labs/publisher/internal/controller/note"
	stickerctrl "labs/publisher/internal/controller/sticker"
	"labs/publisher/internal/repository"
	"labs/publisher/internal/service"
	"labs/shared/middleware"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/gin-gonic/gin"
	_ "github.com/lib/pq"
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
	db, err := sql.Open("postgres", a.cfg.DSN())
	if err != nil {
		return fmt.Errorf("failed to open database connection: %w", err)
	}
	defer db.Close()

	pingCtx, pingCancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer pingCancel()
	if err := db.PingContext(pingCtx); err != nil {
		return fmt.Errorf("failed to ping database: %w", err)
	}
	log.Println("Successfully connected to the database!")

	disClient := kafka.NewKafkaDiscussionClient(a.cfg.Brokers)

	repos := repository.NewCas(db, disClient)
	services := service.New(repos)

	editorController := editorctrl.NewEditorController(services.EditorService())
	issueController := issuectrl.NewIssueController(services.IssueService())
	noteController := notectrl.NewNoteController(services.NoteService())
	stickerController := stickerctrl.New(services.StickerService())

	router := gin.New()
	router.Use(gin.Recovery())
	router.Use(middleware.RequestLogger())

	router.GET("/health", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{"status": "ok", "time": time.Now().Format(time.RFC3339)})
	})

	api := router.Group("/api/v1.0")
	editorController.RegisterRoutes(api)
	issueController.RegisterRoutes(api)
	noteController.RegisterRoutes(api)
	stickerController.RegisterRoutes(api)

	addr := a.cfg.ServerPort

	srv := &http.Server{
		Addr:    ":" + addr,
		Handler: router,
	}

	go func() {
		log.Printf("Starting HTTP server on %s", addr)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			log.Fatalf("Listen error: %s\n", err)
		}
	}()

	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	log.Println("Shutting down server...")

	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	if err := srv.Shutdown(ctx); err != nil {
		log.Printf("Server forced to shutdown: %v", err)
		return err
	}

	log.Println("Server exiting")
	return nil
}

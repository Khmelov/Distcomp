package bootstrap

import (
	"context"
	"fmt"
	"github.com/gocql/gocql"
	"github.com/gofiber/fiber/v2"
	"go.uber.org/fx"
	"go.uber.org/zap"
	"time"

	"discussion-service/internal/api/discussion_service/http"
	"discussion-service/internal/config"
	"discussion-service/internal/entity"
	"discussion-service/internal/repository"
	"discussion-service/internal/usecase"
)

func NewApp() *fx.App {
	return fx.New(
		fx.Provide(
			config.GetConfig, newLogger,
			func(cfg config.Config, log *zap.Logger) (*gocql.Session, error) {
				cluster := gocql.NewCluster(cfg.Cassandra.Hosts...)
				cluster.ProtoVersion = 4
				cluster.ConnectTimeout = 10 * time.Second

				cluster.RetryPolicy = &gocql.ExponentialBackoffRetryPolicy{
					NumRetries: 10,
					Min:        2 * time.Second,
					Max:        10 * time.Second,
				}

				log.Info("Connecting to Cassandra...", zap.Strings("hosts", cfg.Cassandra.Hosts))

				var session *gocql.Session
				var err error
				for i := 0; i < 15; i++ {
					session, err = cluster.CreateSession()
					if err == nil {
						log.Info("Successfully connected to Cassandra")
						return session, nil
					}
					log.Warn("Cassandra not ready yet, retrying in 5s...", zap.Error(err))
					time.Sleep(5 * time.Second)
				}

				return nil, fmt.Errorf("could not connect to cassandra after retries: %w", err)
			},
			repository.NewCommentRepository,
			usecase.NewCommentUsecase,
			handler.NewCommentHandler,
			func() *fiber.App {
				return fiber.New(fiber.Config{
					ErrorHandler: func(c *fiber.Ctx, err error) error {
						code := 500
						eCode := "50000"
						msg := err.Error()

						if e, ok := err.(*entity.CustomError); ok {
							code = e.Status
							eCode = fmt.Sprintf("%d%s", e.Status, e.SubCode)
							msg = e.Message
						}
						return c.Status(code).JSON(entity.ErrorResponse{ErrorCode: eCode, ErrorMessage: msg})
					},
				})
			},
		),
		fx.Invoke(startServer),
	)
}

func startServer(lc fx.Lifecycle, cfg config.Config, logg *zap.Logger, app *fiber.App, commentH *handler.CommentHandler) {
	api := app.Group("/api/v1.0")
	api.Post("/comments", commentH.Create)
	api.Get("/comments", commentH.GetAll)
	api.Get("/comments/:id", commentH.GetByID)
	api.Put("/comments/:id", commentH.Update)
	api.Delete("/comments/:id", commentH.Delete)

	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			go app.Listen(cfg.AppConfig.AppAddress)
			logg.Info("Discussion service started", zap.String("addr", cfg.AppConfig.AppAddress))
			return nil
		},
		OnStop: func(ctx context.Context) error { return app.Shutdown() },
	})
}

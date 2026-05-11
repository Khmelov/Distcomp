package bootstrap

import (
	"context"
	"fmt"
	"strings"

	"github.com/gofiber/fiber/v2"
	"go.uber.org/fx"
	"go.uber.org/zap"

	"note-service/internal/api/note_service/http"
	"note-service/internal/config"
	"note-service/internal/entity"
	"note-service/internal/pkg/auth"
	"note-service/internal/pkg/cache"
	client "note-service/internal/pkg/clients"
	"note-service/internal/pkg/kafka"
	"note-service/internal/repository"
	"note-service/internal/usecase"
)

func NewApp() *fx.App {
	return fx.New(
		fx.Provide(
			config.GetConfig,
			newLogger,
			NewPostgresPool,

			func(cfg config.Config) *cache.RedisCache {
				return cache.NewRedisCache(cfg.RedisConfig.Host, cfg.RedisConfig.Password, cfg.RedisConfig.DB)
			},
			func(cfg config.Config, log *zap.Logger) *kafka.Producer {
				kafkaCfg := kafka.Config{
					Brokers:  cfg.KafkaConfig.Brokers,
					InTopic:  cfg.KafkaConfig.InTopic,
					OutTopic: cfg.KafkaConfig.OutTopic,
				}
				return kafka.NewProducer(kafkaCfg, log)
			},

			func(cfg config.Config, log *zap.Logger) *kafka.Consumer {
				kafkaCfg := kafka.Config{
					Brokers:  cfg.KafkaConfig.Brokers,
					InTopic:  cfg.KafkaConfig.InTopic,
					OutTopic: cfg.KafkaConfig.OutTopic,
				}
				return kafka.NewConsumer(kafkaCfg, log)
			},

			repository.NewAuthorRepository,
			repository.NewTopicRepository,
			repository.NewMarkerRepository,
			repository.NewCommentRepository,

			func(cfg config.Config) *auth.JWTService {
				return auth.NewJWTService(cfg.JWTConfig.Secret, cfg.JWTConfig.ExpiryMinutes)
			},

			func(repo repository.AuthorRepository, cache *cache.RedisCache) *usecase.AuthorUsecase {
				return usecase.NewAuthorUsecase(repo, cache)
			},
			func(repo repository.NoteRepository[entity.Topic], cache *cache.RedisCache) *usecase.TopicUsecase {
				return usecase.NewTopicUsecase(repo, cache)
			},
			func(repo repository.NoteRepository[entity.Marker], cache *cache.RedisCache) *usecase.MarkerUsecase {
				return usecase.NewMarkerUsecase(repo, cache)
			},
			usecase.NewCommentUsecase,

			http.NewAuthorHandler,
			http.NewTopicHandler,
			http.NewMarkerHandler,
			http.NewAuthHandler,
			func(
				authorH *http.AuthorHandler,
				topicH *http.TopicHandler,
				markerH *http.MarkerHandler,
				commentH *http.CommentProxyHandler,
				topicsRepo repository.NoteRepository[entity.Topic],
				authorsUC *usecase.AuthorUsecase,
				topicsUC *usecase.TopicUsecase,
				markersUC *usecase.MarkerUsecase,
			) *http.SecuredHandlers {
				return http.NewSecuredHandlers(authorH, topicH, markerH, commentH, topicsRepo, authorsUC, topicsUC, markersUC)
			},

			func(c *client.DiscussionClient, topics repository.NoteRepository[entity.Topic]) *http.CommentProxyHandler {
				return http.NewCommentProxyHandler(c, topics)
			},

			client.NewDiscussionClient,

			func(logg *zap.Logger) *fiber.App {
				return fiber.New(fiber.Config{
					ErrorHandler: func(c *fiber.Ctx, err error) error {
						if strings.Contains(err.Error(), "duplicate key value ") {
							return c.Status(403).JSON(entity.ErrorResponse{
								ErrorCode:    "40301",
								ErrorMessage: "Entity with such unique key already exists",
							})
						}

						if strings.Contains(err.Error(), "violates foreign key constraint ") {
							return c.Status(400).JSON(entity.ErrorResponse{
								ErrorCode:    "40001",
								ErrorMessage: "Referenced entity not found",
							})
						}

						if e, ok := err.(*entity.CustomError); ok {
							return c.Status(e.Status).JSON(entity.ErrorResponse{
								ErrorCode:    fmt.Sprintf("%d%s", e.Status, e.SubCode),
								ErrorMessage: e.Message,
							})
						}

						if fiberErr, ok := err.(*fiber.Error); ok {
							return c.Status(fiberErr.Code).JSON(entity.ErrorResponse{
								ErrorCode:    fmt.Sprintf("%d00", fiberErr.Code),
								ErrorMessage: fiberErr.Message,
							})
						}

						logg.Error("Unhandled internal error", zap.Error(err))
						return c.Status(500).JSON(entity.ErrorResponse{
							ErrorCode:    "50000",
							ErrorMessage: "Internal Server Error",
						})
					},
				})
			},
		),
		fx.Invoke(startFiberServer),
		fx.Invoke(checkRedisConnection),
		fx.Invoke(startKafkaConsumer),
	)
}

func startFiberServer(
	lc fx.Lifecycle,
	cfg config.Config,
	logg *zap.Logger,
	app *fiber.App,
	authorRepo repository.AuthorRepository,
	jwtSvc *auth.JWTService,
	authH *http.AuthHandler,
	sec *http.SecuredHandlers,
	authorH *http.AuthorHandler,
	topicH *http.TopicHandler,
	markerH *http.MarkerHandler,
	commentH *http.CommentProxyHandler,
) {
	api := app.Group("/api/v1.0")
	api.Post("/authors", authorH.Create)
	api.Get("/authors", authorH.GetAll)
	api.Get("/authors/:id", authorH.GetByID)
	api.Put("/authors", authorH.Update)
	api.Put("/authors/:id", authorH.Update)
	api.Delete("/authors/:id", authorH.Delete)

	api.Post("/topics", topicH.Create)
	api.Get("/topics", topicH.GetAll)
	api.Get("/topics/:id", topicH.GetByID)
	api.Put("/topics", topicH.Update)
	api.Put("/topics/:id", topicH.Update)
	api.Delete("/topics/:id", topicH.Delete)

	api.Post("/markers", markerH.Create)
	api.Get("/markers", markerH.GetAll)
	api.Get("/markers/:id", markerH.GetByID)
	api.Put("/markers", markerH.Update)
	api.Put("/markers/:id", markerH.Update)
	api.Delete("/markers/:id", markerH.Delete)

	api.All("/comments", commentH.Handle)
	api.All("/comments/*", commentH.Handle)

	jwtMw := http.JWTProtect(authorRepo, jwtSvc)

	v2 := app.Group("/api/v2.0")
	v2.Post("/login", authH.Login)
	v2.Post("/authors", authH.Register)

	pr := v2.Group("", jwtMw)
	pr.Get("/me", authH.Me)

	pr.Get("/authors", sec.AuthorGetAll)
	pr.Get("/authors/:id", sec.AuthorGetByID)
	pr.Put("/authors", sec.AuthorUpdate)
	pr.Put("/authors/:id", sec.AuthorUpdate)
	pr.Delete("/authors/:id", sec.AuthorDelete)

	pr.Post("/topics", sec.TopicCreate)
	pr.Get("/topics", sec.TopicGetAll)
	pr.Get("/topics/:id", sec.TopicGetByID)
	pr.Put("/topics", sec.TopicUpdate)
	pr.Put("/topics/:id", sec.TopicUpdate)
	pr.Delete("/topics/:id", sec.TopicDelete)

	pr.Post("/markers", sec.MarkerCreate)
	pr.Get("/markers", sec.MarkerGetAll)
	pr.Get("/markers/:id", sec.MarkerGetByID)
	pr.Put("/markers", sec.MarkerUpdate)
	pr.Put("/markers/:id", sec.MarkerUpdate)
	pr.Delete("/markers/:id", sec.MarkerDelete)

	pr.All("/comments", sec.CommentsRoute)
	pr.All("/comments/*", sec.CommentsRoute)

	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			addr := cfg.AppConfig.AppAddress
			logg.Info("Starting Fiber REST API", zap.String("address", addr))
			go func() {
				if err := app.Listen(addr); err != nil {
					logg.Fatal("Server failed to start", zap.Error(err))
				}
			}()
			return nil
		},
		OnStop: func(ctx context.Context) error {
			return app.Shutdown()
		},
	})
}

func checkRedisConnection(
	lc fx.Lifecycle,
	logg *zap.Logger,
	cacheClient *cache.RedisCache,
) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			if err := cacheClient.Ping(ctx); err != nil {
				logg.Warn("Redis not available", zap.Error(err))
				return nil
			}
			logg.Info("Redis connected successfully")
			return nil
		},
		OnStop: func(ctx context.Context) error {
			return cacheClient.Close()
		},
	})
}

func startKafkaConsumer(
	lc fx.Lifecycle,
	logg *zap.Logger,
	consumer *kafka.Consumer,
	client *client.DiscussionClient,
) {
	lc.Append(fx.Hook{
		OnStart: func(ctx context.Context) error {
			handlers := kafka.ConsumerHandlers{
				OnCommentResult: client.HandleKafkaResponse,
			}
			consumer.Start(ctx, handlers)
			logg.Info("Kafka consumer started")
			return nil
		},
		OnStop: func(ctx context.Context) error {
			return consumer.Close()
		},
	})
}

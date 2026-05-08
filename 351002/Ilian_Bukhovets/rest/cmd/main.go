package main

import (
	"github.com/go-redis/redis/v8"
	"github.com/gofiber/fiber/v2"
	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
	"gridusko_rest/internal/cache"
	"gridusko_rest/internal/resolver"
	author_service "gridusko_rest/internal/services/author"
	comment_service "gridusko_rest/internal/services/comment"
	issue_service "gridusko_rest/internal/services/issue"
	marker_service "gridusko_rest/internal/services/marker"
	"gridusko_rest/internal/storage/author"
	"gridusko_rest/internal/storage/comment"
	"gridusko_rest/internal/storage/issue"
	"gridusko_rest/internal/storage/marker"
)

func main() {
	db, err := sqlx.Connect("postgres", "user=postgres password=postgres dbname=distcomp sslmode=disable host=172.17.0.1")
	if err != nil {
		panic(err)
	}
	defer db.Close()

	authorStorage := author.NewStorage(db)
	commentStorage := comment.NewStorage(db)
	issueStorage := issue.NewStorage(db)
	markerStorage := marker.NewStorage(db)

	kafkaProducer := comment_service.NewKafkaProducer()
	kafkaConsumer := comment_service.NewKafkaConsumer("ResponseTopic")

	redisClient := redis.NewClient(&redis.Options{
		Addr: "localhost:6379",
	})

	cacheauthor := cache.NewCacheStorage(redisClient, *authorStorage)

	authorService := author_service.NewService(authorStorage)
	commentService := comment_service.NewService(commentStorage, kafkaProducer, *kafkaConsumer)
	issueService := issue_service.NewService(issueStorage)
	markerService := marker_service.NewService(markerStorage)

	handler := resolver.NewHandler(authorService, commentService, issueService, markerService)

	app := fiber.New()

	app.Post("/api/v1.0/authors", handler.CreateAuthorHandler)
	app.Get("/api/v1.0/authors/:id", handler.GetAuthorByIDHandler)
	app.Get("/api/v1.0/authors", handler.GetAuthorsHandler)
	app.Put("/api/v1.0/authors", handler.UpdateAuthorHandler)
	app.Delete("/api/v1.0/authors/:id", handler.DeleteAuthorHandler)

	app.Post("/api/v1.0/issues", handler.CreateIssueHandler)
	app.Get("/api/v1.0/issues/:id", handler.GetIssueByIDHandler)
	app.Get("/api/v1.0/issues", handler.GetIssuesHandler)
	app.Put("/api/v1.0/issues", handler.UpdateIssueHandler)
	app.Delete("/api/v1.0/issues/:id", handler.DeleteIssueHandler)

	app.Post("/api/v1.0/comments", handler.CreateCommentHandler)
	app.Get("/api/v1.0/comments/:id", handler.GetCommentByIDHandler)
	app.Get("/api/v1.0/comments", handler.GetCommentsHandler)
	app.Put("/api/v1.0/comments", handler.UpdateCommentHandler)
	app.Delete("/api/v1.0/comments/:id", handler.DeleteCommentHandler)

	app.Post("/api/v1.0/markers", handler.CreateMarkerHandler)
	app.Get("/api/v1.0/markers/:id", handler.GetMarkerByIDHandler)
	app.Get("/api/v1.0/markers", handler.GetMarkersHandler)
	app.Put("/api/v1.0/markers", handler.UpdateMarkerHandler)
	app.Delete("/api/v1.0/markers/:id", handler.DeleteMarkerHandler)

	_ = cacheauthor
	
	println("Server started")
	err = app.Listen("0.0.0.0:24110")
	if err != nil {
		commentService.DeleteComment(0)
		commentService.GetComments()
		commentService.UpdateComment(nil)
		commentService.GetCommentByID(0)
		panic(err)
	}
}

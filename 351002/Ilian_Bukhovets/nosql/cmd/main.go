package main

import (
	"fmt"
	"github.com/gocql/gocql"
	"github.com/gofiber/fiber/v2"
	_ "github.com/lib/pq"
	"gridusko_rest/internal/resolver"
	comment_service "gridusko_rest/internal/services/comment"
	"gridusko_rest/internal/storage/comment"
	"log"
)

func main() {
	cluster := gocql.NewCluster("127.0.0.1")
	cluster.Keyspace = "distcomp"
	cluster.Consistency = gocql.Quorum

	session, err := cluster.CreateSession()
	if err != nil {
		log.Fatalf("Failed to connect to Cassandra: %v", err)
	}
	defer session.Close()

	fmt.Println("Connected to Cassandra!")

	commentStorage := comment.NewStorage(session)

	commentService := comment_service.NewService(commentStorage)

	producer := comment_service.NewKafkaProducer()

	kafka := comment_service.NewKafkaConsumer("InTopic", commentService, producer)
	go kafka.ConsumeMessages()

	kafka2 := comment_service.NewKafkaConsumer("ReqTopic", commentService, producer)
	go kafka2.ConsumeRequests()
	
	handler := resolver.NewHandler(commentService)

	app := fiber.New()

	app.Post("/api/v1.0/comments", handler.CreateCommentHandler)
	app.Get("/api/v1.0/comments/:id", handler.GetCommentByIDHandler)
	app.Get("/api/v1.0/comments", handler.GetCommentsHandler)
	app.Put("/api/v1.0/comments/:id", handler.UpdateCommentHandler)
	app.Delete("/api/v1.0/comments/:id", handler.DeleteCommentHandler)

	println("Server started")
	err = app.Listen("0.0.0.0:24130")
	if err != nil {
		panic(err)
	}
}

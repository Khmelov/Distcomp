package main

import (
	"context"
	"discussion/internal/domain"
	"discussion/internal/repository"
	"encoding/json"
	"math/rand"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/redis/go-redis/v9"
	"github.com/segmentio/kafka-go"
)

func main() {
	repo := repository.NewNoteRepository()

	rdb := redis.NewClient(&redis.Options{Addr: "localhost:6379"})
	ctx := context.Background()
	_ = rdb

	go func() {
		reader := kafka.NewReader(kafka.ReaderConfig{
			Brokers: []string{"localhost:9092"},
			Topic:   "InTopic",
			GroupID: "discussion-group",
		})
		writer := &kafka.Writer{
			Addr:     kafka.TCP("localhost:9092"),
			Topic:    "OutTopic",
			Balancer: &kafka.LeastBytes{},
		}
		for {
			m, err := reader.ReadMessage(ctx)
			if err != nil {
				continue
			}
			var note domain.Note
			json.Unmarshal(m.Value, &note)
			note.State = "PENDING"
			repo.Create(note)
			if strings.Contains(strings.ToLower(note.Content), "bad") {
				note.State = "DECLINE"
			} else {
				note.State = "APPROVE"
			}
			repo.Update(note)
			payload, _ := json.Marshal(note)
			writer.WriteMessages(ctx, kafka.Message{Value: payload})
		}
	}()

	r := gin.Default()
	v1 := r.Group("/api/v1.0")
	{
		v1.POST("/notes", func(c *gin.Context) {
			var n domain.Note
			c.ShouldBindJSON(&n)
			if n.ID == 0 {
				rand.Seed(time.Now().UnixNano())
				n.ID = rand.Intn(100000)
			}
			n.State = "PENDING"
			repo.Create(n)
			c.JSON(201, n)
		})
		v1.GET("/notes", func(c *gin.Context) {
			notes, _ := repo.GetAll()
			if notes == nil {
				notes = []domain.Note{}
			}
			c.JSON(200, notes)
		})
		v1.GET("/notes/:id", func(c *gin.Context) {
			id, _ := strconv.Atoi(c.Param("id"))
			n, err := repo.GetByID(id)
			if err != nil {
				c.JSON(404, gin.H{"errorCode": "40401", "errorMessage": "Not found"})
				return
			}
			c.JSON(200, n)
		})
		v1.PUT("/notes/:id", func(c *gin.Context) {
			idStr := c.Param("id")
			id, _ := strconv.Atoi(idStr)
			var n domain.Note
			c.BindJSON(&n)
			n.ID = id
			if n.State == "" {
				n.State = "APPROVE"
			}
			repo.Update(n)

			c.JSON(200, n)
		})
		v1.DELETE("/notes/:id", func(c *gin.Context) {
			idStr := c.Param("id")
			id, _ := strconv.Atoi(idStr)
			repo.Delete(id)

			// rdb.Del(ctx, "note:"+idStr)

			c.Status(204)
		})
	}
	r.Run(":24130")
}

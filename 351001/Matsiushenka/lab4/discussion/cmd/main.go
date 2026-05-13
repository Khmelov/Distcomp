package main

import (
	"context"
	"discussion/internal/domain"
	"discussion/internal/repository"
	"encoding/json"
	"fmt"
	"math/rand"
	"strconv"
	"strings"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/segmentio/kafka-go"
)

func main() {
	repo := repository.NewNoteRepository()

	go func() {
		reader := kafka.NewReader(kafka.ReaderConfig{
			Brokers:  []string{"localhost:9092"},
			Topic:    "InTopic",
			GroupID:  "discussion-group",
			MinBytes: 10e3, // 10KB
			MaxBytes: 10e6, // 10MB
		})
		defer reader.Close()

		writer := &kafka.Writer{
			Addr:     kafka.TCP("localhost:9092"),
			Topic:    "OutTopic",
			Balancer: &kafka.LeastBytes{},
		}
		defer writer.Close()

		fmt.Println("Kafka Worker started: listening InTopic...")

		for {
			m, err := reader.ReadMessage(context.Background())
			if err != nil {
				fmt.Printf("Error reading message: %v\n", err)
				continue
			}

			var note domain.Note
			if err := json.Unmarshal(m.Value, &note); err != nil {
				fmt.Printf("Error unmarshaling note: %v\n", err)
				continue
			}

			note.State = "PENDING"
			repo.Create(note)
			fmt.Printf("Note %d received, state: PENDING\n", note.ID)

			if strings.Contains(strings.ToLower(note.Content), "bad") {
				note.State = "DECLINE"
			} else {
				note.State = "APPROVE"
			}

			repo.Update(note)
			fmt.Printf("Note %d moderated, state: %s\n", note.ID, note.State)

			payload, _ := json.Marshal(note)
			err = writer.WriteMessages(context.Background(), kafka.Message{
				Value: payload,
			})
			if err != nil {
				fmt.Printf("Error sending message to OutTopic: %v\n", err)
			}
		}
	}()

	r := gin.Default()

	v1 := r.Group("/api/v1.0")
	{
		v1.POST("/notes", func(c *gin.Context) {
			var n domain.Note
			if err := c.ShouldBindJSON(&n); err != nil {
				c.JSON(400, gin.H{"error": "bad request"})
				return
			}
			if n.ID == 0 {
				rand.Seed(time.Now().UnixNano())
				n.ID = rand.Intn(100000)
			}
			if n.State == "" {
				n.State = "PENDING"
			}
			repo.Create(n)
			c.JSON(201, n)
		})

		v1.GET("/notes", func(c *gin.Context) {
			notes, err := repo.GetAll()
			if err != nil || notes == nil {
				c.JSON(200, []domain.Note{})
				return
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
			id, _ := strconv.Atoi(c.Param("id"))
			var n domain.Note
			if err := c.ShouldBindJSON(&n); err != nil {
				c.Status(400)
				return
			}
			n.ID = id
			repo.Update(n)
			c.JSON(200, n)
		})

		v1.DELETE("/notes/:id", func(c *gin.Context) {
			id, _ := strconv.Atoi(c.Param("id"))
			repo.Delete(id)
			c.Status(204)
		})
	}

	fmt.Println("Discussion service starting on :24130")
	r.Run(":24130")
}

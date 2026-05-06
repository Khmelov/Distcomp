package comment

import (
	"encoding/json"
	"fmt"
	"gridusko_rest/internal/model"
	"log"
	"strings"

	"github.com/confluentinc/confluent-kafka-go/kafka"
)

type Request struct {
	RequestID int    `json:"request_id"`
	Type      string `json:"type"`
	CommentID int64  `json:"comment_id"`
	Payload   []byte `json:"payload"`
}

type Response struct {
	RequestID int    `json:"request_id"`
	Status    string `json:"status"`
	Data      string `json:"data"`
}

type KafkaConsumer struct {
	commentsService *Service
	consumer        *kafka.Consumer

	producer KafkaProducer
}

func NewKafkaConsumer(topic string, commentsService *Service, producer KafkaProducer) *KafkaConsumer {
	config := &kafka.ConfigMap{
		"bootstrap.servers": "localhost:9092",
		"group.id":          "discussion-group",
		"auto.offset.reset": "earliest",
	}

	consumer, err := kafka.NewConsumer(config)
	if err != nil {
		log.Fatalf("Failed to create Kafka consumer: %v", err)
	}

	consumer.SubscribeTopics([]string{topic}, nil)

	return &KafkaConsumer{
		consumer:        consumer,
		commentsService: commentsService,
		producer:        producer,
	}
}

func (c *KafkaConsumer) ConsumeMessages() {
	for {
		msg, err := c.consumer.ReadMessage(-1)
		if err != nil {
			log.Printf("Error while consuming message: %v", err)
			continue
		}

		var comment model.Comment
		err = json.Unmarshal(msg.Value, &comment)
		if err != nil {
			log.Printf("Failed to unmarshal message: %v", err)
			continue
		}

		c.processComment(&comment)

		fmt.Printf("Processed comment: %+v\n", comment)
	}
}

func (c *KafkaConsumer) processComment(comment *model.Comment) {
	if containsStopWords(comment.Country) {
		comment.State = "DECLINE"
	} else {
		comment.State = "APPROVE"
	}

	err := c.commentsService.CreateComment(comment)
	if err != nil {
		log.Printf("Failed to create comment: %v", err)
		return
	}

}

func containsStopWords(text string) bool {
	stopWords := []string{"bad", "spam", "offensive"}
	for _, word := range stopWords {
		if strings.Contains(text, word) {
			return true
		}
	}
	return false
}

func (c *KafkaConsumer) ConsumeRequests() {
	for {
		msg, err := c.consumer.ReadMessage(-1)
		if err != nil {
			log.Printf("Error while consuming message: %v", err)
			continue
		}

		var request Request
		err = json.Unmarshal(msg.Value, &request)
		if err != nil {
			log.Printf("Failed to unmarshal request: %v", err)
			continue
		}

		fmt.Printf("Received request: %+v\n", request)

		response := c.processRequest(request)

		err = c.producer.SendMessage("ResponseTopic", int(request.RequestID), response)
		if err != nil {
			fmt.Printf("Consume req error: %s", err.Error())
		}
	}
}

func (c *KafkaConsumer) processRequest(request Request) Response {
	var data string

	switch request.Type {
	case "get":
		comment, err := c.commentsService.GetCommentByID(request.CommentID)
		if err != nil {
			return Response{
				RequestID: request.RequestID,
				Status:    "ERROR",
				Data:      fmt.Sprintf("Failed to get comment: %v", err),
			}
		}
		data = fmt.Sprintf("Comment found: %+v", comment)

	case "update":
		comm := new(model.Comment)
		err := json.Unmarshal([]byte(request.Payload), comm)
		if err != nil {
			return Response{
				RequestID: request.RequestID,
				Status:    "ERROR",
				Data:      fmt.Sprintf("Failed to update comment: %v", err),
			}
		}
		err = c.commentsService.UpdateComment(comm)

		if err != nil {
			return Response{
				RequestID: request.RequestID,
				Status:    "ERROR",
				Data:      fmt.Sprintf("Failed to update comment: %v", err),
			}
		}
		data = fmt.Sprintf("Comment updated")

	case "delete":
		err := c.commentsService.DeleteComment(request.CommentID)
		if err != nil {
			return Response{
				RequestID: request.RequestID,
				Status:    "ERROR",
				Data:      fmt.Sprintf("Failed to delete comment: %v", err),
			}
		}
		data = "Comment deleted successfully"

	default:
		return Response{
			RequestID: request.RequestID,
			Status:    "ERROR",
			Data:      fmt.Sprintf("Unknown request type: %s", request.Type),
		}
	}

	return Response{
		RequestID: request.RequestID,
		Status:    "SUCCESS",
		Data:      data,
	}
}

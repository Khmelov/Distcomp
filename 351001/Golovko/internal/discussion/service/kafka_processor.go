package service

import (
	"context"
	"encoding/json"
	"log"
	"strings"

	"distcomp/internal/domain"
	kafkaMsg "distcomp/internal/kafka"

	"github.com/segmentio/kafka-go"
)

type KafkaProcessor struct {
	reader  *kafka.Reader
	writer  *kafka.Writer
	service *CommentService
}

func NewKafkaProcessor(brokers []string, svc *CommentService) *KafkaProcessor {
	return &KafkaProcessor{
		reader: kafka.NewReader(kafka.ReaderConfig{
			Brokers: brokers,
			GroupID: "discussion-worker-group",
			Topic:   "InTopic",
		}),
		writer: &kafka.Writer{
			Addr:  kafka.TCP(brokers...),
			Topic: "OutTopic",
		},
		service: svc,
	}
}

func (p *KafkaProcessor) Start() {
	log.Println("Starting Kafka Processor in discussion service...")
	for {
		m, err := p.reader.ReadMessage(context.Background())
		if err != nil {
			log.Printf("Failed to read from InTopic: %v", err)
			continue
		}

		var msg kafkaMsg.Message
		if err := json.Unmarshal(m.Value, &msg); err != nil {
			log.Printf("Failed to unmarshal kafka message: %v", err)
			continue
		}

		go p.processMessage(msg)
	}
}

func (p *KafkaProcessor) processMessage(msg kafkaMsg.Message) {
	ctx := context.Background()

	outMsg := kafkaMsg.Message{
		CorrelationID: msg.CorrelationID,
		Operation:     msg.Operation,
	}

	switch msg.Operation {
	case kafkaMsg.OpCreate:
		if msg.Response == nil {
			return
		}
		
		state := "APPROVE"
		contentLower := strings.ToLower(msg.Response.Content)
		if strings.Contains(contentLower, "spam") || strings.Contains(contentLower, "спам") {
			state = "DECLINE"
		}

		c := &domain.Comment{
			ID:        msg.Response.ID,
			ArticleID: msg.Response.ArticleID,
			Content:   msg.Response.Content,
			State:     state,
		}
		
		_ = p.service.Repo.Create(ctx, c)

	case kafkaMsg.OpGet:
		res, err := p.service.GetByID(ctx, msg.CommentID)
		if err != nil {
			outMsg.Error = err.Error()
		} else {
			outMsg.Response = &res
		}

	case kafkaMsg.OpGetAll:
		res, err := p.service.GetAll(ctx)
		if err != nil {
			outMsg.Error = err.Error()
		} else {
			outMsg.Responses = res
		}

	case kafkaMsg.OpUpdate:
		if msg.Request != nil {
			res, err := p.service.Update(ctx, msg.CommentID, *msg.Request)
			if err != nil {
				outMsg.Error = err.Error()
			} else {
				outMsg.Response = &res
			}
		}

	case kafkaMsg.OpDelete:
		err := p.service.Delete(ctx, msg.CommentID)
		if err != nil {
			outMsg.Error = err.Error()
		}
	}

	b, err := json.Marshal(outMsg)
	if err == nil {
		_ = p.writer.WriteMessages(ctx, kafka.Message{
			Value: b,
		})
	}
}
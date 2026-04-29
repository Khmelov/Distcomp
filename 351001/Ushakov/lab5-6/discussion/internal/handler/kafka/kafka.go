package kafka

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"time"

	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/discussion/internal/model"
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/discussion/internal/service"
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/discussion/pkg/keylock"
	"github.com/segmentio/kafka-go"
)

type KafkaConsumer struct {
	reader        *kafka.Reader
	writer        *kafka.Writer
	noticeService service.MessageService
	locks         *keylock.KeyLock
}

func NewKafkaConsumer(brokers []string, groupID string, noticeService service.MessageService) *KafkaConsumer {
	return &KafkaConsumer{
		reader: kafka.NewReader(kafka.ReaderConfig{
			Brokers:         brokers,
			Topic:           "InTopic",
			GroupID:         groupID,
			MinBytes:        10e3,
			MaxBytes:        10e6,
			MaxWait:         1 * time.Second,
			ReadLagInterval: -1,
		}),
		writer: &kafka.Writer{
			Addr:         kafka.TCP(brokers...),
			Topic:        "OutTopic",
			Balancer:     &kafka.Hash{},
			BatchTimeout: 10 * time.Millisecond,
		},
		noticeService: noticeService,
		locks:         keylock.New(),
	}
}

func (k *KafkaConsumer) processMessage(ctx context.Context) {
	msg, err := k.reader.FetchMessage(ctx)
	if err != nil {
		if errors.Is(err, context.Canceled) {
			return
		}
		log.Printf("Error fetching message: %v", err)
		return
	}

	log.Printf("Processing message - Partition: %d, Offset: %d", msg.Partition, msg.Offset)

	var request struct {
		Action    string `json:"action"`
		ID        int64  `json:"id,omitempty"`
		IssueID   int64  `json:"issue_id"`
		Content   string `json:"content,omitempty"`
		ReplyTo   string `json:"reply_to"`
		Timestamp int64  `json:"timestamp"`
		Version   int    `json:"version"`
	}

	if err := json.Unmarshal(msg.Value, &request); err != nil {
		log.Printf("Error unmarshaling message: %v", err)
		k.commitMessage(ctx, msg)
		return
	}

	lockKey := fmt.Sprintf("issue_%d", request.IssueID)
	k.locks.Lock(lockKey)
	defer k.locks.Unlock(lockKey)

	if request.Version != 1 {
		log.Printf("Unsupported message version: %d", request.Version)
		k.commitMessage(ctx, msg)
		return
	}

	response := k.processRequest(ctx, request)

	if err := k.sendResponse(ctx, request.ReplyTo, response); err != nil {
		log.Printf("Error sending response: %v", err)
		return
	}

	k.commitMessage(ctx, msg)
}

func (k *KafkaConsumer) processRequest(ctx context.Context, request struct {
	Action    string `json:"action"`
	ID        int64  `json:"id,omitempty"`
	IssueID   int64  `json:"issue_id"`
	Content   string `json:"content,omitempty"`
	ReplyTo   string `json:"reply_to"`
	Timestamp int64  `json:"timestamp"`
	Version   int    `json:"version"`
}) map[string]interface{} {

	// Логирование начала обработки
	log.Printf("Processing %s for issue %d", request.Action, request.IssueID)

	startTime := time.Now()
	var response map[string]interface{}

	defer func() {
		// Логирование результата обработки
		duration := time.Since(startTime)
		if err, ok := response["error"]; ok {
			log.Printf("Failed %s for issue %d after %v: %v", request.Action, request.IssueID, duration, err)
		} else {
			log.Printf("Completed %s for issue %d in %v", request.Action, request.IssueID, duration)
		}
	}()

	switch request.Action {
	case "create":
		notice, err := k.noticeService.CreateMessage(ctx, model.Message{
			IssueID: request.IssueID,
			Content: request.Content,
		})
		if err != nil {
			response = map[string]interface{}{"error": err.Error()}
		} else {
			response = map[string]interface{}{"message": notice}
		}

	case "get":
		notice, err := k.noticeService.GetMessage(ctx, request.ID)
		if err != nil {
			response = map[string]interface{}{"error": err.Error()}
		} else {
			response = map[string]interface{}{"message": notice}
		}

	case "get_all":
		notices, err := k.noticeService.GetMessages(ctx)
		if err != nil {
			response = map[string]interface{}{"error": err.Error()}
		} else {
			response = map[string]interface{}{"messages": notices}
		}

	case "update":
		notice, err := k.noticeService.UpdateMessage(ctx, model.Message{
			ID:      request.ID,
			IssueID: request.IssueID,
			Content: request.Content,
		})
		if err != nil {
			response = map[string]interface{}{"error": err.Error()}
		} else {
			response = map[string]interface{}{"message": notice}
		}

	case "delete":
		err := k.noticeService.DeleteMessage(ctx, request.ID)
		if err != nil {
			response = map[string]interface{}{"error": err.Error()}
		} else {
			response = map[string]interface{}{"status": "deleted"}
		}

	default:
		response = map[string]interface{}{"error": "unknown action"}
	}

	return response
}

func (k *KafkaConsumer) sendResponse(ctx context.Context, replyTo string, response map[string]interface{}) error {
	responseBytes, err := json.Marshal(response)
	if err != nil {
		return fmt.Errorf("marshal error: %w", err)
	}

	return k.writer.WriteMessages(ctx, kafka.Message{
		Topic: replyTo,
		Value: responseBytes,
	})
}

func (k *KafkaConsumer) commitMessage(ctx context.Context, msg kafka.Message) {
	ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
	defer cancel()

	if err := k.reader.CommitMessages(ctx, msg); err != nil {
		log.Printf("Error committing message: %v", err)
	}
}

func (k *KafkaConsumer) Start(ctx context.Context) {
	go func() {
		for {
			select {
			case <-ctx.Done():
				log.Println("Stopping Kafka consumer...")
				if err := k.Close(); err != nil {
					log.Printf("Error closing consumer: %v", err)
				}
				return
			default:
				k.processMessage(ctx)
			}
		}
	}()
}

func (k *KafkaConsumer) Close() error {
	var errs []error

	if err := k.reader.Close(); err != nil {
		errs = append(errs, fmt.Errorf("reader close error: %w", err))
	}

	if err := k.writer.Close(); err != nil {
		errs = append(errs, fmt.Errorf("writer close error: %w", err))
	}

	if len(errs) > 0 {
		return fmt.Errorf("multiple errors during close: %v", errs)
	}

	return nil
}

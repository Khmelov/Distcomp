package kafka

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"labs/publisher/internal/client"
	"log"
	"strconv"
	"sync"
	"time"

	kafkamodel "labs/shared/dto/kafka"
	notemodel "labs/shared/model/note"

	"github.com/segmentio/kafka-go"
)

type kafkaDiscussionClient struct {
	writer    *kafka.Writer
	reader    *kafka.Reader
	responses sync.Map
}

func NewKafkaDiscussionClient(brokers []string) client.DiscussionClient {
	writer := &kafka.Writer{
		Addr:     kafka.TCP(brokers...),
		Topic:    "InTopic",
		Balancer: &kafka.Hash{},
	}

	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		Topic:   "OutTopic",
		GroupID: "publisher-group",
	})

	client := &kafkaDiscussionClient{
		writer: writer,
		reader: reader,
	}

	go client.listenResponses()

	return client
}

// listenResponses постоянно читает OutTopic и рассылает ответы ожидающим методам
func (c *kafkaDiscussionClient) listenResponses() {
	for {
		msg, err := c.reader.ReadMessage(context.Background())
		if err != nil {
			log.Printf("Error reading from Kafka OutTopic: %v", err)
			continue
		}

		var resp kafkamodel.KafkaResponse
		if err := json.Unmarshal(msg.Value, &resp); err != nil {
			log.Printf("Failed to unmarshal kafka response: %v", err)
			continue
		}

		// Ищем канал, который ждет этот CorrelationID
		if ch, ok := c.responses.Load(resp.CorrelationID); ok {
			ch.(chan *kafkamodel.KafkaResponse) <- &resp
		}
	}
}

// Helper: Отправка запроса с ожиданием ответа (Request-Reply)
func (c *kafkaDiscussionClient) doSyncRequest(ctx context.Context, msg kafkamodel.KafkaMessage) (*kafkamodel.KafkaResponse, error) {
	// 1. Генерируем уникальный CorrelationID
	corrID := fmt.Sprintf("%d", time.Now().UnixNano())
	msg.CorrelationID = corrID

	// 2. Создаем канал для ответа и кладем в мапу
	respChan := make(chan *kafkamodel.KafkaResponse, 1)
	c.responses.Store(corrID, respChan)
	defer c.responses.Delete(corrID) // Очищаем после выхода

	// 3. Отправляем в Kafka (без ключа, чтобы раскидывало Round-Robin)
	payload, _ := json.Marshal(msg)
	err := c.writer.WriteMessages(ctx, kafka.Message{
		Value: payload,
	})
	if err != nil {
		return nil, fmt.Errorf("failed to write message: %w", err)
	}

	// 4. Ждем ответ или таймаут 1 секунду (согласно заданию)
	select {
	case resp := <-respChan:
		if resp.Error != "" {
			return nil, errors.New(resp.Error)
		}
		return resp, nil
	case <-time.After(1 * time.Second):
		return nil, errors.New("timeout: discussion service did not respond in 1s")
	}
}

// ==========================================
// Имплементация интерфейса DiscussionClient
// ==========================================

func (c *kafkaDiscussionClient) SyncCreate(ctx context.Context, note *notemodel.Note) error {
	// Fire-and-forget: статус PENDING сразу
	note.State = notemodel.StatePending

	msg := kafkamodel.KafkaMessage{
		Operation: kafkamodel.OpCreate,
		Note:      note,
	}
	payload, _ := json.Marshal(msg)

	// ТРЕБОВАНИЕ: "сообщения одной Issue всегда оказывались в одной partition"
	// Решение: используем IssueID в качестве ключа (Key) сообщения.
	key := []byte(strconv.FormatInt(note.IssueID, 10))

	return c.writer.WriteMessages(ctx, kafka.Message{
		Key:   key,
		Value: payload,
	})
}

func (c *kafkaDiscussionClient) SyncUpdate(ctx context.Context, note *notemodel.Note) error {
	// Здесь мы используем паттерн Request-Reply
	_, err := c.doSyncRequest(ctx, kafkamodel.KafkaMessage{
		Operation: kafkamodel.OpUpdate,
		Note:      note,
	})
	return err
}

func (c *kafkaDiscussionClient) SyncDelete(ctx context.Context, id int64) error {
	_, err := c.doSyncRequest(ctx, kafkamodel.KafkaMessage{
		Operation: kafkamodel.OpDelete,
		NoteID:    id,
	})
	return err
}

func (c *kafkaDiscussionClient) SyncGet(ctx context.Context, id int64) (*notemodel.Note, error) {
	resp, err := c.doSyncRequest(ctx, kafkamodel.KafkaMessage{
		Operation: kafkamodel.OpGet,
		NoteID:    id,
	})
	if err != nil {
		return nil, err
	}
	return resp.Note, nil
}

func (c *kafkaDiscussionClient) SyncList(ctx context.Context) ([]*notemodel.Note, error) {
	resp, err := c.doSyncRequest(ctx, kafkamodel.KafkaMessage{
		Operation: kafkamodel.OpList,
	})
	if err != nil {
		return nil, err
	}
	return resp.Notes, nil
}

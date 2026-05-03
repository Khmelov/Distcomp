package service

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"sync"
	"time"

	"distcomp/internal/dto"
	kafkaMsg "distcomp/internal/kafka"
	"distcomp/internal/repository"

	"github.com/segmentio/kafka-go"
)

type commentKafka struct {
	producer *kafka.Writer
	consumer *kafka.Reader
	pending  sync.Map // map[string]chan *kafkaMsg.Message
}

func NewCommentKafka(brokers []string) Comment {
	writer := &kafka.Writer{
		Addr:     kafka.TCP(brokers...),
		Topic:    "InTopic",
		Balancer: &kafka.Hash{},
		BatchTimeout: 10 * time.Millisecond,
	}

	reader := kafka.NewReader(kafka.ReaderConfig{
		Brokers: brokers,
		GroupID: "publisher-group",
		Topic:   "OutTopic",
	})

	c := &commentKafka{
		producer: writer,
		consumer: reader,
	}

	go c.readLoop()

	return c
}

func (k *commentKafka) readLoop() {
	for {
		m, err := k.consumer.ReadMessage(context.Background())
		if err != nil {
			log.Printf("Kafka read error: %v", err)
			time.Sleep(1 * time.Second)
			continue
		}

		var msg kafkaMsg.Message
		if err := json.Unmarshal(m.Value, &msg); err != nil {
			continue
		}

		if ch, ok := k.pending.Load(msg.CorrelationID); ok {
			ch.(chan *kafkaMsg.Message) <- &msg
		}
	}
}

func (k *commentKafka) doSync(ctx context.Context, msg kafkaMsg.Message, key string) (*kafkaMsg.Message, error) {
	msg.CorrelationID = fmt.Sprintf("%d", time.Now().UnixNano())

	ch := make(chan *kafkaMsg.Message, 1)
	k.pending.Store(msg.CorrelationID, ch)
	defer k.pending.Delete(msg.CorrelationID)

	b, _ := json.Marshal(msg)
	err := k.producer.WriteMessages(ctx, kafka.Message{
		Key:   []byte(key),
		Value: b,
	})
	if err != nil {
		return nil, err
	}

	select {
	case res := <-ch:
		if res.Error != "" {
			return nil, errors.New(res.Error)
		}
		return res, nil
	case <-time.After(1 * time.Second):
		return nil, errors.New("timeout waiting for discussion service via Kafka")
	case <-ctx.Done():
		return nil, ctx.Err()
	}
}

func (k *commentKafka) Create(ctx context.Context, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	id := time.Now().UnixNano()

	res := dto.CommentResponseTo{
		ID:        id,
		ArticleID: req.ArticleID,
		Content:   req.Content,
		State:     "PENDING",
	}

	msg := kafkaMsg.Message{
		Operation: kafkaMsg.OpCreate,
		ArticleID: req.ArticleID,
		Response:  &res,
	}

	b, _ := json.Marshal(msg)

	err := k.producer.WriteMessages(context.Background(), kafka.Message{
		Key:   []byte(fmt.Sprintf("%d", req.ArticleID)),
		Value: b,
	})

	return res, err
}

func (k *commentKafka) GetByID(ctx context.Context, id int64) (dto.CommentResponseTo, error) {
	msg := kafkaMsg.Message{
		Operation: kafkaMsg.OpGet,
		CommentID: id,
	}
	res, err := k.doSync(ctx, msg, "")
	if err != nil {
		return dto.CommentResponseTo{}, err
	}
	return *res.Response, nil
}

func (k *commentKafka) GetAll(ctx context.Context, params repository.ListParams) ([]dto.CommentResponseTo, error) {
	msg := kafkaMsg.Message{
		Operation: kafkaMsg.OpGetAll,
	}
	res, err := k.doSync(ctx, msg, "")
	if err != nil {
		return nil, err
	}
	if res.Responses == nil {
		return []dto.CommentResponseTo{}, nil
	}
	return res.Responses, nil
}

func (k *commentKafka) Update(ctx context.Context, id int64, req dto.CommentRequestTo) (dto.CommentResponseTo, error) {
	msg := kafkaMsg.Message{
		Operation: kafkaMsg.OpUpdate,
		CommentID: id,
		ArticleID: req.ArticleID,
		Request:   &req,
	}

	res, err := k.doSync(ctx, msg, fmt.Sprintf("%d", req.ArticleID))
	if err != nil {
		return dto.CommentResponseTo{}, err
	}
	return *res.Response, nil
}

func (k *commentKafka) Delete(ctx context.Context, id int64) error {
	msg := kafkaMsg.Message{
		Operation: kafkaMsg.OpDelete,
		CommentID: id,
	}
	_, err := k.doSync(ctx, msg, "")
	return err
}

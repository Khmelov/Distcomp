package comment

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/storage/comment"
	"math/rand/v2"
	"time"
)

type Request struct {
	RequestID int    `json:"request_id"`
	CommentID int64  `json:"comment_id"`
	Type      string `json:"type"`
	Payload   []byte `json:"payload"`
}

type Service struct {
	storage  *comment.Storage
	producer KafkaProducer
	consumer KafkaConsumer
}

var (
	ErrInvalidBody   = errors.New("invalid body")
	ErrIssueNotFound = errors.New("issue not found")
)

func checks(comment *model.Comment) error {
	if comment.IssueID == 0 || comment.Content == "" {
		return ErrInvalidBody
	}

	if len(comment.Content) < 2 {
		return ErrInvalidBody
	}

	return nil
}

func NewService(storage *comment.Storage, producer KafkaProducer, consumer KafkaConsumer) *Service {
	return &Service{
		storage:  storage,
		producer: producer,
		consumer: consumer,
	}
}

func (s *Service) CreateComment(c *model.Comment) error {
	if err := checks(c); err != nil {
		return err
	}

	c.ID = int64(rand.Int()) % 1000

	err := s.producer.SendMessage("InTopic", int(c.ID), *c)
	if err != nil {
		return err
	}

	time.Sleep(1 * time.Second)

	return nil
}

func (s *Service) GetCommentByID(id int64) (*model.Comment, error) {

	req := Request{
		RequestID: rand.Int(),
		CommentID: id,
		Type:      "get",
	}

	err := s.producer.SendMessage("ReqTopic", req.RequestID, req)
	if err != nil {
		fmt.Printf("GetCommentByID error: %s\n", err.Error())
	}

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	response, err := s.consumer.WaitForResponse(ctx, req.RequestID)
	if err != nil {
		fmt.Printf("Failed to get response: %s", err)
		return nil, err
	}

	comm := new(model.Comment)
	err = json.Unmarshal([]byte(response.Data), comm)
	if err != nil {
		fmt.Printf("Failed to get response: %s", err)
	}

	return comm, nil
}

func (s *Service) UpdateComment(comment *model.Comment) error {
	bs, err := json.Marshal(comment)
	if err != nil {
		return err
	}

	req := Request{
		RequestID: rand.Int(),
		CommentID: comment.ID,
		Type:      "update",
		Payload:   bs,
	}

	err = s.producer.SendMessage("ReqTopic", req.RequestID, req)
	if err != nil {
		fmt.Printf("GetCommentByID error: %s\n", err.Error())
	}

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	response, err := s.consumer.WaitForResponse(ctx, req.RequestID)
	if err != nil {
		fmt.Printf("Failed to get response: %s", err)
		return err
	}

	comm := new(model.Comment)
	err = json.Unmarshal([]byte(response.Data), comm)
	if err != nil {
		fmt.Printf("Failed to get response: %s", err)
	}

	return nil
}

func (s *Service) DeleteComment(id int64) error {
	req := Request{
		RequestID: rand.Int(),
		CommentID: id,
		Type:      "delete",
	}

	err := s.producer.SendMessage("ReqTopic", req.RequestID, req)
	if err != nil {
		fmt.Printf("GetCommentByID error: %s\n", err.Error())
	}

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	response, err := s.consumer.WaitForResponse(ctx, req.RequestID)
	if err != nil {
		fmt.Printf("Failed to get response: %s", err)
		return err
	}

	comm := new(model.Comment)
	err = json.Unmarshal([]byte(response.Data), comm)
	if err != nil {
		fmt.Printf("Failed to get response: %s", err)
	}

	return nil
}

func (s *Service) GetComments() ([]*model.Comment, error) {
	return s.storage.GetComments()
}

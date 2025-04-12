package message

import (
	"context"
	"database/sql"
	"fmt"
	"log"

	"github.com/Khmelov/Distcomp/251003/Nasevich/restbasics/internal/storage/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrMessageNotFound   = fmt.Errorf("message not found")
	ErrFailedToUpdate    = fmt.Errorf("failed to update message")
	ErrFailedToDelete    = fmt.Errorf("failed to delete message")
	ErrInvalidForeignKey = fmt.Errorf("invalid foreign key passed")
)

type instance struct {
	db *sqlx.DB
}

type Message interface {
	CreateMessage(ctx context.Context, is model.Message) (model.Message, error)
	GetMessages(ctx context.Context) ([]model.Message, error)
	GetMessageByID(ctx context.Context, id int64) (model.Message, error)
	UpdateMessageByID(ctx context.Context, is model.Message) (model.Message, error)
	DeleteMessageByID(ctx context.Context, id int64) error
}

func New(db *sqlx.DB) Message {
	return &instance{
		db: db,
	}
}

func (i *instance) CreateMessage(ctx context.Context, is model.Message) (model.Message, error) {
	query := `INSERT INTO tbl_message (issueid, content) 
	          VALUES ($1, $2) RETURNING id`

	var id int64

	err := i.db.QueryRowContext(ctx, query, is.IssueID, is.Content).
		Scan(&id)
	if err != nil {
		log.Println(err)

		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23503" {
			return model.Message{}, ErrInvalidForeignKey
		}

		return model.Message{}, fmt.Errorf("failed to create message: %w", err)
	}

	is.ID = id

	return is, nil
}

func (i *instance) DeleteMessageByID(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_message WHERE id = $1`
	result, err := i.db.ExecContext(ctx, query, id)
	if err != nil {
		log.Println("Error executing DELETE query:", err)
		return fmt.Errorf("failed to delete message: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		log.Println("Error getting rows affected:", err)
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		log.Println("No message found with ID:", id)
		return ErrMessageNotFound
	}

	return nil
}

func (i *instance) GetMessageByID(ctx context.Context, id int64) (model.Message, error) {
	var message model.Message
	query := `SELECT * FROM tbl_message WHERE id = $1`

	err := i.db.GetContext(ctx, &message, query, id)
	if err != nil {
		if err == sql.ErrNoRows {
			return message, ErrMessageNotFound
		}
		return message, fmt.Errorf("failed to retrieve message by ID: %w", err)
	}

	return message, nil
}

func (i *instance) GetMessages(ctx context.Context) ([]model.Message, error) {
	var messages []model.Message
	query := `SELECT * FROM tbl_message`

	err := i.db.SelectContext(ctx, &messages, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve messages: %w", err)
	}

	if len(messages) == 0 {
		return []model.Message{}, nil
	}

	return messages, nil
}

func (i *instance) UpdateMessageByID(ctx context.Context, is model.Message) (model.Message, error) {
	query := `UPDATE tbl_message SET issueId = $1, content = $2
	          WHERE id = $3 RETURNING id, issueId, content`
	var updatedMessage model.Message

	err := i.db.QueryRowContext(ctx, query, is.IssueID, is.Content, is.ID).
		Scan(&updatedMessage.ID, &updatedMessage.IssueID, &updatedMessage.Content)
	if err != nil {
		if err == sql.ErrNoRows {
			log.Println("message not found with id:", is.ID)
			return updatedMessage, ErrMessageNotFound
		}

		log.Println("error with query:", err)
		return updatedMessage, fmt.Errorf("failed to update message: %w", err)
	}

	return updatedMessage, nil
}

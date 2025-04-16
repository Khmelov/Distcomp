package message

import (
	"context"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/internal/model"
	"github.com/gocql/gocql"
	"github.com/stackus/errors"
)

var ErrNoticeNotFound = errors.Wrap(errors.ErrNotFound, "notice is not found")

func (n *MessageRepo) GetMessage(ctx context.Context, id int64) (model.Message, error) {
	const query = `SELECT id, issue_id, content FROM tbl_message WHERE id = ? LIMIT 1`

	var message model.Message

	err := n.session.Query(query, id).
		WithContext(ctx).
		Scan(&message.ID, &message.IssueID, &message.Content)
	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return model.Message{}, ErrNoticeNotFound
		}

		return model.Message{}, err
	}

	return message, nil
}

func (n *MessageRepo) GetMessages(ctx context.Context) ([]model.Message, error) {
	const query = `SELECT id, issue_id, content FROM tbl_message`

	messages := make([]model.Message, 0)

	scanner := n.session.Query(query).WithContext(ctx).Iter().Scanner()

	for scanner.Next() {
		var message model.Message

		err := scanner.Scan(&message.ID, &message.IssueID, &message.Content)
		if err != nil {
			return nil, err
		}

		messages = append(messages, message)
	}

	if _err := scanner.Err(); _err != nil {
		if errors.Is(_err, gocql.ErrNotFound) {
			return nil, ErrNoticeNotFound
		}

		return nil, _err
	}

	return messages, nil
}

func (n *MessageRepo) CreateMessage(ctx context.Context, args model.Message) (model.Message, error) {
	const query = `INSERT INTO tbl_message (id, issue_id, content) VALUES (?, ?, ?)`

	message := model.Message{
		ID:      n.nextID(),
		IssueID: args.IssueID,
		Content: args.Content,
	}

	err := n.session.Query(query, message.ID, message.IssueID, message.Content).
		WithContext(ctx).
		Exec()
	if err != nil {
		return model.Message{}, err
	}

	return message, nil
}

func (n *MessageRepo) UpdateMessage(ctx context.Context, args model.Message) (model.Message, error) {
	const query = `UPDATE tbl_message SET issue_id = ?, content = ? WHERE id = ?`

	_, err := n.GetMessage(ctx, args.ID)
	if err != nil {
		return model.Message{}, err
	}

	err = n.session.Query(query, args.IssueID, args.Content, args.ID).WithContext(ctx).Exec()
	if err != nil {
		return model.Message{}, err
	}

	message, err := n.GetMessage(ctx, args.ID)
	if err != nil {
		return model.Message{}, err
	}

	return message, nil
}

func (n *MessageRepo) DeleteMessage(ctx context.Context, id int64) error {
	const query = `DELETE FROM tbl_message WHERE id = ?`

	_, err := n.GetMessage(ctx, id)
	if err != nil {
		return err
	}

	err = n.session.Query(query, id).WithContext(ctx).Exec()
	if err != nil {
		return err
	}

	return nil
}

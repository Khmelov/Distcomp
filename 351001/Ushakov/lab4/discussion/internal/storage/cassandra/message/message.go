package message

import (
	"context"
	"github.com/Khmelov/Distcomp/351001/Ushakov/labX/discussion/internal/model"
	"github.com/gocql/gocql"
	"github.com/stackus/errors"
)

var ErrNoticeNotFound = errors.Wrap(errors.ErrNotFound, "notice is not found")

func (n *PostRepo) GetPost(ctx context.Context, id int64) (model.Post, error) {
	const query = `SELECT id, issue_id, content FROM tbl_message WHERE id = ? LIMIT 1`

	var message model.Post

	err := n.session.Query(query, id).
		WithContext(ctx).
		Scan(&message.ID, &message.IssueID, &message.Content)
	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return model.Post{}, ErrNoticeNotFound
		}

		return model.Post{}, err
	}

	return message, nil
}

func (n *PostRepo) GetPosts(ctx context.Context) ([]model.Post, error) {
	const query = `SELECT id, issue_id, content FROM tbl_message`

	messages := make([]model.Post, 0)

	scanner := n.session.Query(query).WithContext(ctx).Iter().Scanner()

	for scanner.Next() {
		var message model.Post

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

func (n *PostRepo) CreatePost(ctx context.Context, args model.Post) (model.Post, error) {
	const query = `INSERT INTO tbl_message (id, issue_id, content) VALUES (?, ?, ?)`

	message := model.Post{
		ID:      n.nextID(),
		IssueID: args.IssueID,
		Content: args.Content,
	}

	err := n.session.Query(query, message.ID, message.IssueID, message.Content).
		WithContext(ctx).
		Exec()
	if err != nil {
		return model.Post{}, err
	}

	return message, nil
}

func (n *PostRepo) UpdatePost(ctx context.Context, args model.Post) (model.Post, error) {
	const query = `UPDATE tbl_message SET issue_id = ?, content = ? WHERE id = ?`

	_, err := n.GetPost(ctx, args.ID)
	if err != nil {
		return model.Post{}, err
	}

	err = n.session.Query(query, args.IssueID, args.Content, args.ID).WithContext(ctx).Exec()
	if err != nil {
		return model.Post{}, err
	}

	message, err := n.GetPost(ctx, args.ID)
	if err != nil {
		return model.Post{}, err
	}

	return message, nil
}

func (n *PostRepo) DeletePost(ctx context.Context, id int64) error {
	const query = `DELETE FROM tbl_message WHERE id = ?`

	_, err := n.GetPost(ctx, id)
	if err != nil {
		return err
	}

	err = n.session.Query(query, id).WithContext(ctx).Exec()
	if err != nil {
		return err
	}

	return nil
}

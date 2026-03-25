package post

import (
	"context"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/model"
	"github.com/gocql/gocql"
	"github.com/stackus/errors"
)

var ErrNoticeNotFound = errors.Wrap(errors.ErrNotFound, "notice is not found")

func (n *PostRepo) GetPost(ctx context.Context, id int64) (model.Post, error) {
	const query = `SELECT id, issue_id, content FROM tbl_post WHERE id = ? LIMIT 1`

	var post model.Post

	err := n.session.Query(query, id).
		WithContext(ctx).
		Scan(&post.ID, &post.IssueID, &post.Content)
	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return model.Post{}, ErrNoticeNotFound
		}

		return model.Post{}, err
	}

	return post, nil
}

func (n *PostRepo) GetPosts(ctx context.Context) ([]model.Post, error) {
	const query = `SELECT id, issue_id, content FROM tbl_post`

	posts := make([]model.Post, 0)

	scanner := n.session.Query(query).WithContext(ctx).Iter().Scanner()

	for scanner.Next() {
		var post model.Post

		err := scanner.Scan(&post.ID, &post.IssueID, &post.Content)
		if err != nil {
			return nil, err
		}

		posts = append(posts, post)
	}

	if _err := scanner.Err(); _err != nil {
		if errors.Is(_err, gocql.ErrNotFound) {
			return nil, ErrNoticeNotFound
		}

		return nil, _err
	}

	return posts, nil
}

func (n *PostRepo) CreatePost(ctx context.Context, args model.Post) (model.Post, error) {
	const query = `INSERT INTO tbl_post (id, issue_id, content) VALUES (?, ?, ?)`

	post := model.Post{
		ID:      n.nextID(),
		IssueID: args.IssueID,
		Content: args.Content,
	}

	err := n.session.Query(query, post.ID, post.IssueID, post.Content).
		WithContext(ctx).
		Exec()
	if err != nil {
		return model.Post{}, err
	}

	return post, nil
}

func (n *PostRepo) UpdatePost(ctx context.Context, args model.Post) (model.Post, error) {
	const query = `UPDATE tbl_post SET issue_id = ?, content = ? WHERE id = ?`

	_, err := n.GetPost(ctx, args.ID)
	if err != nil {
		return model.Post{}, err
	}

	err = n.session.Query(query, args.IssueID, args.Content, args.ID).WithContext(ctx).Exec()
	if err != nil {
		return model.Post{}, err
	}

	post, err := n.GetPost(ctx, args.ID)
	if err != nil {
		return model.Post{}, err
	}

	return post, nil
}

func (n *PostRepo) DeletePost(ctx context.Context, id int64) error {
	const query = `DELETE FROM tbl_post WHERE id = ?`

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

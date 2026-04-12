package post

import (
	"context"

	"github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/model"
	"github.com/gocql/gocql"
	"github.com/stackus/errors"
)

var ErrNoticeNotFound = errors.Wrap(errors.ErrNotFound, "notice is not found")

func (n *github.com /Khmelov/Distcomp/351001/UshakovRepo) Getgithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	const query = `SELECT id, issue_id, content FROM tbl_post WHERE id = ? LIMIT 1`

	var post model.github.com / Khmelov / Distcomp / 351001 / Ushakov

	err := n.session.Query(query, id).
		WithContext(ctx).
		Scan(&post.ID, &post.IssueID, &post.Content)
	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, ErrNoticeNotFound
		}

		return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	return post, nil
}

func (n *github.com /Khmelov/Distcomp/351001/UshakovRepo) Getgithub.com/Khmelov/Distcomp/351001/Ushakovs(ctx context.Context) ([]model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	const query = `SELECT id, issue_id, content FROM tbl_post`

	posts := make([]model.github.com / Khmelov / Distcomp / 351001 / Ushakov, 0)

	scanner := n.session.Query(query).WithContext(ctx).Iter().Scanner()

	for scanner.Next() {
		var post model.github.com / Khmelov / Distcomp / 351001 / Ushakov

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

func (n *github.com /Khmelov/Distcomp/351001/UshakovRepo) Creategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, args model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	const query = `INSERT INTO tbl_post (id, issue_id, content) VALUES (?, ?, ?)`

	post := model.github.com / Khmelov / Distcomp / 351001 / Ushakov{
		ID:      n.nextID(),
		IssueID: args.IssueID,
		Content: args.Content,
	}

	err := n.session.Query(query, post.ID, post.IssueID, post.Content).
		WithContext(ctx).
		Exec()
	if err != nil {
		return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	return post, nil
}

func (n *github.com /Khmelov/Distcomp/351001/UshakovRepo) Updategithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, args model.github.com/Khmelov/Distcomp/351001/Ushakov) (model.github.com/Khmelov/Distcomp/351001/Ushakov, error) {
	const query = `UPDATE tbl_post SET issue_id = ?, content = ? WHERE id = ?`

	_, err := n.Getgithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, args.ID)
	if err != nil {
		return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	err = n.session.Query(query, args.IssueID, args.Content, args.ID).WithContext(ctx).Exec()
	if err != nil {
		return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	post, err := n.Getgithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, args.ID)
	if err != nil {
		return model.github.com / Khmelov / Distcomp / 351001 / Ushakov{}, err
	}

	return post, nil
}

func (n *github.com /Khmelov/Distcomp/351001/UshakovRepo) Deletegithub.com/Khmelov/Distcomp/351001/Ushakov(ctx context.Context, id int64) error {
	const query = `DELETE FROM tbl_post WHERE id = ?`

	_, err := n.Getgithub.com / Khmelov / Distcomp / 351001 / Ushakov(ctx, id)
	if err != nil {
		return err
	}

	err = n.session.Query(query, id).WithContext(ctx).Exec()
	if err != nil {
		return err
	}

	return nil
}

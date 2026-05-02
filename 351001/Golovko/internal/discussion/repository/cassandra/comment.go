package cassandra

import (
	"context"
	"errors"

	"distcomp/internal/domain"

	"github.com/gocql/gocql"
)

var ErrNotFound = errors.New("entity not found")

type CommentStorage struct {
	session *gocql.Session
}

func NewCommentStorage(session *gocql.Session) *CommentStorage {
	return &CommentStorage{session: session}
}

func (s *CommentStorage) getNextID() (int64, error) {
	var id int64
	iter := s.session.Query(`SELECT id FROM tbl_comment`).Iter()
	var currentID int64
	for iter.Scan(&currentID) {
		if currentID > id {
			id = currentID
		}
	}
	if err := iter.Close(); err != nil {
		return 0, err
	}
	return id + 1, nil
}

func (s *CommentStorage) Create(ctx context.Context, comment *domain.Comment) error {
	if comment.ID == 0 {
		id, err := s.getNextID()
		if err != nil {
			return err
		}
		comment.ID = id
	}
	if comment.State == "" {
		comment.State = "APPROVE"
	}
	return s.session.Query(
		`INSERT INTO tbl_comment (article_id, id, content, state) VALUES (?, ?, ?, ?)`,
		comment.ArticleID, comment.ID, comment.Content, comment.State,
	).WithContext(ctx).Exec()
}

func (s *CommentStorage) GetByID(ctx context.Context, id int64) (*domain.Comment, error) {
	var c domain.Comment
	err := s.session.Query(
		`SELECT article_id, id, content, state FROM tbl_comment WHERE id = ? ALLOW FILTERING`,
		id,
	).WithContext(ctx).Scan(&c.ArticleID, &c.ID, &c.Content, &c.State)

	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return nil, ErrNotFound
		}
		return nil, err
	}
	return &c, nil
}

func (s *CommentStorage) GetAll(ctx context.Context) ([]*domain.Comment, error) {
	var comments []*domain.Comment
	iter := s.session.Query(`SELECT article_id, id, content, state FROM tbl_comment`).WithContext(ctx).Iter()

	var articleID, id int64
	var content, state string
	for iter.Scan(&articleID, &id, &content, &state) {
		comments = append(comments, &domain.Comment{
			ArticleID: articleID,
			ID:        id,
			Content:   content,
			State:     state,
		})
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}
	return comments, nil
}

func (s *CommentStorage) Update(ctx context.Context, comment *domain.Comment) error {
	old, err := s.GetByID(ctx, comment.ID)
	if err != nil {
		return err
	}

	state := comment.State
	if state == "" {
		state = old.State
	}

	if old.ArticleID != comment.ArticleID {
		_ = s.session.Query(`DELETE FROM tbl_comment WHERE article_id = ? AND id = ?`, old.ArticleID, old.ID).WithContext(ctx).Exec()
		return s.session.Query(
			`INSERT INTO tbl_comment (article_id, id, content, state) VALUES (?, ?, ?, ?)`,
			comment.ArticleID, comment.ID, comment.Content, state,
		).WithContext(ctx).Exec()
	}

	return s.session.Query(
		`UPDATE tbl_comment SET content = ?, state = ? WHERE article_id = ? AND id = ?`,
		comment.Content, state, comment.ArticleID, comment.ID,
	).WithContext(ctx).Exec()
}

func (s *CommentStorage) Delete(ctx context.Context, id int64) error {
	old, err := s.GetByID(ctx, id)
	if err != nil {
		return err
	}
	return s.session.Query(
		`DELETE FROM tbl_comment WHERE article_id = ? AND id = ?`,
		old.ArticleID, old.ID,
	).WithContext(ctx).Exec()
}

package repository

import (
	"context"
	"discussion-service/internal/entity"
	"fmt"
	"github.com/gocql/gocql"
)

type CommentRepository struct {
	session *gocql.Session
}

func NewCommentRepository(session *gocql.Session) (*CommentRepository, error) {
	_ = session.Query(fmt.Sprintf(`CREATE KEYSPACE IF NOT EXISTS distcomp WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}`)).Exec()
	err := session.Query(`
		CREATE TABLE IF NOT EXISTS distcomp.tbl_comment (
			id bigint PRIMARY KEY,
			topic_id bigint,
			country text,
			content text,
			state text
		)`).Exec()
	return &CommentRepository{session: session}, err
}

func (r *CommentRepository) Create(ctx context.Context, c entity.Comment) (entity.Comment, error) {
	c.ID = gocql.TimeUUID().Timestamp()
	if c.State == "" {
		c.State = "PENDING"
	}
	err := r.session.Query(`INSERT INTO distcomp.tbl_comment (id, topic_id, country, content, state) VALUES (?, ?, ?, ?, ?)`,
		c.ID, c.TopicID, c.Country, c.Content, c.State).WithContext(ctx).Exec()
	return c, err
}

func (r *CommentRepository) GetByIDOnly(ctx context.Context, id int64) (entity.Comment, error) {
	var c entity.Comment
	err := r.session.Query(`SELECT id, topic_id, country, content, state FROM distcomp.tbl_comment WHERE id = ?`,
		id).WithContext(ctx).Scan(&c.ID, &c.TopicID, &c.Country, &c.Content, &c.State)
	if err == gocql.ErrNotFound {
		return c, entity.NewErr(404, "01", "Comment not found")
	}
	return c, err
}

func (r *CommentRepository) GetByTopicID(ctx context.Context, topicID int64) ([]entity.Comment, error) {
	var comments []entity.Comment
	iter := r.session.Query(`SELECT id, topic_id, country, content, state FROM distcomp.tbl_comment WHERE topic_id = ? ALLOW FILTERING`,
		topicID).WithContext(ctx).Iter()
	var c entity.Comment
	for iter.Scan(&c.ID, &c.TopicID, &c.Country, &c.Content, &c.State) {
		comments = append(comments, c)
		c = entity.Comment{}
	}
	return comments, iter.Close()
}

func (r *CommentRepository) GetAll(ctx context.Context) ([]entity.Comment, error) {
	var comments []entity.Comment
	iter := r.session.Query(`SELECT id, topic_id, country, content, state FROM distcomp.tbl_comment`).WithContext(ctx).Iter()
	var c entity.Comment
	for iter.Scan(&c.ID, &c.TopicID, &c.Country, &c.Content, &c.State) {
		comments = append(comments, c)
		c = entity.Comment{}
	}
	return comments, iter.Close()
}

func (r *CommentRepository) Update(ctx context.Context, c entity.Comment) error {
	return r.session.Query(`UPDATE distcomp.tbl_comment SET content = ?, country = ?, topic_id = ?, state = ? WHERE id = ?`,
		c.Content, c.Country, c.TopicID, c.State, c.ID).WithContext(ctx).Exec()
}

func (r *CommentRepository) DeleteByIDOnly(ctx context.Context, id int64) error {
	return r.session.Query(`DELETE FROM distcomp.tbl_comment WHERE id = ?`, id).WithContext(ctx).Exec()
}

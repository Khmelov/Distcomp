package repository

import (
	"context"
	"sync/atomic"

	apperrors "discussion/internal/errors"
	"discussion/internal/model"

	"github.com/gocql/gocql"
)

type cassandraRepo struct {
	session *gocql.Session
	counter int64
}

func NewCassandraRepository(session *gocql.Session) ReactionRepository {
	return &cassandraRepo{session: session}
}

func (r *cassandraRepo) nextID() int64 {
	return atomic.AddInt64(&r.counter, 1)
}

func (r *cassandraRepo) FindByID(ctx context.Context, id int64) (*model.Reaction, error) {
	var reaction model.Reaction
	err := r.session.Query(
		`SELECT id, issue_id, content FROM distcomp.tbl_reaction WHERE id = ? ALLOW FILTERING`,
		id,
	).WithContext(ctx).Scan(&reaction.ID, &reaction.IssueID, &reaction.Content)
	if err == gocql.ErrNotFound {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, apperrors.ErrInternal
	}
	return &reaction, nil
}

func (r *cassandraRepo) FindAll(ctx context.Context) ([]*model.Reaction, error) {
	iter := r.session.Query(`SELECT id, issue_id, content FROM distcomp.tbl_reaction`).
		WithContext(ctx).Iter()

	items := make([]*model.Reaction, 0)
	var id, issueID int64
	var content string
	for iter.Scan(&id, &issueID, &content) {
		items = append(items, &model.Reaction{ID: id, IssueID: issueID, Content: content})
	}
	if err := iter.Close(); err != nil {
		return nil, apperrors.ErrInternal
	}
	return items, nil
}

func (r *cassandraRepo) FindByIssueID(ctx context.Context, issueID int64) ([]*model.Reaction, error) {
	iter := r.session.Query(
		`SELECT id, issue_id, content FROM distcomp.tbl_reaction WHERE issue_id = ? ALLOW FILTERING`,
		issueID,
	).WithContext(ctx).Iter()

	items := make([]*model.Reaction, 0)
	var id, iID int64
	var content string
	for iter.Scan(&id, &iID, &content) {
		items = append(items, &model.Reaction{ID: id, IssueID: iID, Content: content})
	}
	if err := iter.Close(); err != nil {
		return nil, apperrors.ErrInternal
	}
	return items, nil
}

func (r *cassandraRepo) Create(ctx context.Context, reaction *model.Reaction) (*model.Reaction, error) {
	reaction.ID = r.nextID()
	err := r.session.Query(
		`INSERT INTO distcomp.tbl_reaction (id, issue_id, content) VALUES (?, ?, ?)`,
		reaction.ID, reaction.IssueID, reaction.Content,
	).WithContext(ctx).Exec()
	if err != nil {
		return nil, apperrors.ErrInternal
	}
	return reaction, nil
}

func (r *cassandraRepo) Update(ctx context.Context, id int64, reaction *model.Reaction) (*model.Reaction, error) {
	if _, err := r.FindByID(ctx, id); err != nil {
		return nil, err
	}
	err := r.session.Query(
		`UPDATE distcomp.tbl_reaction SET issue_id = ?, content = ? WHERE id = ?`,
		reaction.IssueID, reaction.Content, id,
	).WithContext(ctx).Exec()
	if err != nil {
		return nil, apperrors.ErrInternal
	}
	reaction.ID = id
	return reaction, nil
}

func (r *cassandraRepo) Delete(ctx context.Context, id int64) error {
	if _, err := r.FindByID(ctx, id); err != nil {
		return err
	}
	return r.session.Query(
		`DELETE FROM distcomp.tbl_reaction WHERE id = ?`,
		id,
	).WithContext(ctx).Exec()
}

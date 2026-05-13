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
	var stateStr string
	err := r.session.Query(
		`SELECT id, issue_id, content, state FROM distcomp.tbl_reaction WHERE id = ? ALLOW FILTERING`,
		id,
	).WithContext(ctx).Scan(&reaction.ID, &reaction.IssueID, &reaction.Content, &stateStr)
	if err == gocql.ErrNotFound {
		return nil, apperrors.ErrNotFound
	}
	if err != nil {
		return nil, apperrors.ErrInternal
	}
	reaction.State = stateFromString(stateStr)
	return &reaction, nil
}

func (r *cassandraRepo) FindAll(ctx context.Context) ([]*model.Reaction, error) {
	iter := r.session.Query(
		`SELECT id, issue_id, content, state FROM distcomp.tbl_reaction`,
	).WithContext(ctx).Iter()

	items := make([]*model.Reaction, 0)
	var id, issueID int64
	var content, stateStr string
	for iter.Scan(&id, &issueID, &content, &stateStr) {
		items = append(items, &model.Reaction{
			ID:      id,
			IssueID: issueID,
			Content: content,
			State:   stateFromString(stateStr),
		})
	}
	if err := iter.Close(); err != nil {
		return nil, apperrors.ErrInternal
	}
	return items, nil
}

func (r *cassandraRepo) FindByIssueID(ctx context.Context, issueID int64) ([]*model.Reaction, error) {
	iter := r.session.Query(
		`SELECT id, issue_id, content, state FROM distcomp.tbl_reaction WHERE issue_id = ? ALLOW FILTERING`,
		issueID,
	).WithContext(ctx).Iter()

	items := make([]*model.Reaction, 0)
	var id, iID int64
	var content, stateStr string
	for iter.Scan(&id, &iID, &content, &stateStr) {
		items = append(items, &model.Reaction{
			ID:      id,
			IssueID: iID,
			Content: content,
			State:   stateFromString(stateStr),
		})
	}
	if err := iter.Close(); err != nil {
		return nil, apperrors.ErrInternal
	}
	return items, nil
}

func (r *cassandraRepo) Create(ctx context.Context, reaction *model.Reaction) (*model.Reaction, error) {
	reaction.ID = r.nextID()
	if reaction.State == "" {
		reaction.State = model.ReactionStatePending
	}
	err := r.session.Query(
		`INSERT INTO distcomp.tbl_reaction (id, issue_id, content, state) VALUES (?, ?, ?, ?)`,
		reaction.ID, reaction.IssueID, reaction.Content, string(reaction.State),
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
	if reaction.State == "" {
		reaction.State = model.ReactionStatePending
	}
	err := r.session.Query(
		`UPDATE distcomp.tbl_reaction SET issue_id = ?, content = ?, state = ? WHERE id = ?`,
		reaction.IssueID, reaction.Content, string(reaction.State), id,
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

// stateFromString converts a string to ReactionState, defaulting to PENDING for null/empty.
func stateFromString(s string) model.ReactionState {
	switch model.ReactionState(s) {
	case model.ReactionStateApprove:
		return model.ReactionStateApprove
	case model.ReactionStateDecline:
		return model.ReactionStateDecline
	default:
		return model.ReactionStatePending
	}
}

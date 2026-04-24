package note

import (
	"context"
	"errors"
	"log"

	"github.com/gocql/gocql"
	"github.com/scylladb/gocqlx/v2"
	"github.com/scylladb/gocqlx/v2/qb"

	notemodel "labs/shared/model/note"
)

type noteCassandraRepository struct {
	session gocqlx.Session
}

func NewNoteCassandraRepository(session gocqlx.Session) Repository {
	return &noteCassandraRepository{session: session}
}

// Локальная Cassandra модель для корректного StructScan/BindStruct
type cassandraNote struct {
	ID      int64  `db:"id"`
	IssueID int64  `db:"issue_id"`
	Content string `db:"content"`
}

func toCassandra(note *notemodel.Note) *cassandraNote {
	return &cassandraNote{
		ID:      note.ID,
		IssueID: note.IssueID,
		Content: note.Content,
	}
}

func fromCassandra(note *cassandraNote) *notemodel.Note {
	return &notemodel.Note{
		ID:      note.ID,
		IssueID: note.IssueID,
		Content: note.Content,
	}
}

func (r *noteCassandraRepository) Create(ctx context.Context, note *notemodel.Note) (*notemodel.Note, error) {
	cNote := toCassandra(note)

	stmt, names := qb.Insert("tbl_note").
		Columns("id", "issue_id", "content").
		ToCql()

	err := r.session.Query(stmt, names).
		WithContext(ctx).
		Consistency(gocql.One).
		BindStruct(cNote).
		ExecRelease()

	if err != nil {
		log.Printf("noteCassandraRepository.Create: %v", err)
		return nil, err
	}

	return note, nil
}

func (r *noteCassandraRepository) GetByID(ctx context.Context, id int64) (*notemodel.Note, error) {
	stmt, names := qb.Select("tbl_note").
		Columns("id", "issue_id", "content").
		Where(qb.Eq("id")).
		ToCql()

	var cNote cassandraNote

	err := r.session.Query(stmt, names).
		WithContext(ctx).
		Consistency(gocql.One).
		BindMap(qb.M{"id": id}).
		GetRelease(&cNote)

	if err != nil {
		if errors.Is(err, gocql.ErrNotFound) {
			return nil, notemodel.ErrNotFound
		}

		log.Printf("noteCassandraRepository.GetByID: %v", err)
		return nil, err
	}

	return fromCassandra(&cNote), nil
}

func (r *noteCassandraRepository) Update(ctx context.Context, note *notemodel.Note) error {
	cNote := toCassandra(note)

	stmt, names := qb.Update("tbl_note").
		Set("issue_id", "content").
		Where(qb.Eq("id")).
		ToCql()

	err := r.session.Query(stmt, names).
		WithContext(ctx).
		BindStruct(cNote).
		ExecRelease()

	if err != nil {
		log.Printf("noteCassandraRepository.Update: %v", err)
	}

	return err
}

func (r *noteCassandraRepository) Delete(ctx context.Context, id int64) error {
	note, err := r.GetByID(ctx, id)
	if err != nil {
		if errors.Is(err, notemodel.ErrNotFound) {
			return nil
		}
		return err
	}

	stmt, names := qb.Delete("tbl_note").
		Where(qb.Eq("id")).
		ToCql()

	err = r.session.Query(stmt, names).
		WithContext(ctx).
		BindMap(qb.M{
			"id": note.ID,
		}).
		ExecRelease()

	if err != nil {
		log.Printf("noteCassandraRepository.Delete: %v", err)
	}

	return err
}

func (r *noteCassandraRepository) List(ctx context.Context, limit, offset int) ([]*notemodel.Note, error) {
	totalLimit := uint(limit + offset)

	stmt, names := qb.Select("tbl_note").
		Columns("id", "issue_id", "content").
		Limit(totalLimit).
		ToCql()

	iter := r.session.Query(stmt, names).
		WithContext(ctx).
		Iter()

	var notes []*notemodel.Note
	var cNote cassandraNote
	skipped := 0

	for iter.StructScan(&cNote) {
		if skipped < offset {
			skipped++
			continue
		}

		// создаём новый объект для каждого элемента
		n := fromCassandra(&cNote)
		notes = append(notes, n)

		if len(notes) == limit {
			break
		}
	}

	if err := iter.Close(); err != nil {
		return nil, err
	}

	return notes, nil
}

package repository

import (
	"context"
	"errors"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"note-service/internal/entity"
)

type pgTopicRepo struct {
	db *pgxpool.Pool
}

func NewTopicRepository(db *pgxpool.Pool) NoteRepository[entity.Topic] {
	return &pgTopicRepo{db: db}
}

func (r *pgTopicRepo) Create(ctx context.Context, t entity.Topic) (entity.Topic, error) {
	tx, err := r.db.Begin(ctx)
	if err != nil {
		return t, err
	}
	defer tx.Rollback(ctx)

	query := `INSERT INTO tbl_topic (author_id, title, content, created, modified) VALUES ($1, $2, $3, $4, $5) RETURNING id`
	err = tx.QueryRow(ctx, query, t.AuthorID, t.Title, t.Content, t.Created, t.Modified).Scan(&t.ID)
	if err != nil {
		return t, err
	}

	for _, mID := range t.MarkerIDs {
		_, err = tx.Exec(ctx, `INSERT INTO tbl_topic_marker (topic_id, marker_id) VALUES ($1, $2)`, t.ID, mID)
		if err != nil {
			return t, err
		}
	}
	return t, tx.Commit(ctx)
}

func (r *pgTopicRepo) GetByID(ctx context.Context, id int64) (entity.Topic, error) {
	var t entity.Topic
	query := `
		SELECT t.id, t.author_id, t.title, t.content, t.created, t.modified,
		       COALESCE(array_agg(tm.marker_id) FILTER (WHERE tm.marker_id IS NOT NULL), '{}')
		FROM tbl_topic t
		LEFT JOIN tbl_topic_marker tm ON t.id = tm.topic_id
		WHERE t.id = $1
		GROUP BY t.id`
	err := r.db.QueryRow(ctx, query, id).Scan(&t.ID, &t.AuthorID, &t.Title, &t.Content, &t.Created, &t.Modified, &t.MarkerIDs)
	if errors.Is(err, pgx.ErrNoRows) {
		return t, entity.NewErr(404, "01", "Topic not found")
	}
	return t, err
}

func (r *pgTopicRepo) Update(ctx context.Context, id int64, t entity.Topic) (entity.Topic, error) {
	tx, err := r.db.Begin(ctx)
	if err != nil {
		return t, err
	}
	defer tx.Rollback(ctx)

	query := `UPDATE tbl_topic SET title=$1, content=$2, modified=$3 WHERE id=$4 RETURNING id`
	err = tx.QueryRow(ctx, query, t.Title, t.Content, t.Modified, id).Scan(&t.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		return t, entity.NewErr(404, "01", "Topic not found")
	}

	_, _ = tx.Exec(ctx, `DELETE FROM tbl_topic_marker WHERE topic_id=$1`, id)
	for _, mID := range t.MarkerIDs {
		_, err = tx.Exec(ctx, `INSERT INTO tbl_topic_marker (topic_id, marker_id) VALUES ($1, $2)`, id, mID)
		if err != nil {
			return t, err
		}
	}
	return t, tx.Commit(ctx)
}

func (r *pgTopicRepo) Delete(ctx context.Context, id int64) error {
	tag, err := r.db.Exec(ctx, `DELETE FROM tbl_topic WHERE id = $1`, id)
	if tag.RowsAffected() == 0 {
		return entity.NewErr(404, "01", "Topic not found")
	}
	return err
}

func (r *pgTopicRepo) GetAll(ctx context.Context, limit, offset int) ([]entity.Topic, error) {
	query := `
		SELECT t.id, t.author_id, t.title, t.content, t.created, t.modified,
		       COALESCE(array_agg(tm.marker_id) FILTER (WHERE tm.marker_id IS NOT NULL), '{}')
		FROM tbl_topic t
		LEFT JOIN tbl_topic_marker tm ON t.id = tm.topic_id
		GROUP BY t.id ORDER BY t.id LIMIT $1 OFFSET $2`
	rows, err := r.db.Query(ctx, query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	res := make([]entity.Topic, 0)
	for rows.Next() {
		var t entity.Topic
		if err := rows.Scan(&t.ID, &t.AuthorID, &t.Title, &t.Content, &t.Created, &t.Modified, &t.MarkerIDs); err != nil {
			return nil, err
		}
		if t.MarkerIDs == nil {
			t.MarkerIDs = []int64{}
		}
		res = append(res, t)
	}
	return res, nil
}

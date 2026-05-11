package repository

import (
	"context"
	"errors"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"note-service/internal/entity"
)

type pgCommentRepo struct {
	db *pgxpool.Pool
}

func NewCommentRepository(db *pgxpool.Pool) NoteRepository[entity.Comment] {
	return &pgCommentRepo{db: db}
}

func (r *pgCommentRepo) Create(ctx context.Context, c entity.Comment) (entity.Comment, error) {
	query := `INSERT INTO tbl_comment (topic_id, content) VALUES ($1, $2) RETURNING id`
	err := r.db.QueryRow(ctx, query, c.TopicID, c.Content).Scan(&c.ID)
	return c, err
}

func (r *pgCommentRepo) GetByID(ctx context.Context, id int64) (entity.Comment, error) {
	var c entity.Comment
	query := `SELECT id, topic_id, content FROM tbl_comment WHERE id = $1`
	err := r.db.QueryRow(ctx, query, id).Scan(&c.ID, &c.TopicID, &c.Content)
	if errors.Is(err, pgx.ErrNoRows) {
		return c, entity.NewErr(404, "01", "Comment not found")
	}
	return c, err
}

func (r *pgCommentRepo) Update(ctx context.Context, id int64, c entity.Comment) (entity.Comment, error) {
	query := `UPDATE tbl_comment SET topic_id=$1, content=$2 WHERE id=$3 RETURNING id`
	err := r.db.QueryRow(ctx, query, c.TopicID, c.Content, id).Scan(&c.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		return c, entity.NewErr(404, "01", "Comment not found")
	}
	return c, err
}

func (r *pgCommentRepo) Delete(ctx context.Context, id int64) error {
	tag, err := r.db.Exec(ctx, `DELETE FROM tbl_comment WHERE id = $1`, id)
	if tag.RowsAffected() == 0 {
		return entity.NewErr(404, "01", "Comment not found")
	}
	return err
}

func (r *pgCommentRepo) GetAll(ctx context.Context, limit, offset int) ([]entity.Comment, error) {
	query := `SELECT id, topic_id, content FROM tbl_comment ORDER BY id LIMIT $1 OFFSET $2`
	rows, err := r.db.Query(ctx, query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	res := make([]entity.Comment, 0)
	for rows.Next() {
		var c entity.Comment
		if err := rows.Scan(&c.ID, &c.TopicID, &c.Content); err != nil {
			return nil, err
		}
		res = append(res, c)
	}
	return res, nil
}

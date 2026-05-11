package repository

import (
	"context"
	"errors"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
	"note-service/internal/entity"
)

type pgMarkerRepo struct {
	db *pgxpool.Pool
}

func NewMarkerRepository(db *pgxpool.Pool) NoteRepository[entity.Marker] {
	return &pgMarkerRepo{db: db}
}

func (r *pgMarkerRepo) Create(ctx context.Context, m entity.Marker) (entity.Marker, error) {
	query := `INSERT INTO tbl_marker (name) VALUES ($1) RETURNING id`
	err := r.db.QueryRow(ctx, query, m.Name).Scan(&m.ID)
	return m, err
}

func (r *pgMarkerRepo) GetByID(ctx context.Context, id int64) (entity.Marker, error) {
	var m entity.Marker
	query := `SELECT id, name FROM tbl_marker WHERE id = $1`
	err := r.db.QueryRow(ctx, query, id).Scan(&m.ID, &m.Name)
	if errors.Is(err, pgx.ErrNoRows) {
		return m, entity.NewErr(404, "01", "Marker not found")
	}
	return m, err
}

func (r *pgMarkerRepo) Update(ctx context.Context, id int64, m entity.Marker) (entity.Marker, error) {
	query := `UPDATE tbl_marker SET name=$1 WHERE id=$2 RETURNING id`
	err := r.db.QueryRow(ctx, query, m.Name, id).Scan(&m.ID)
	if errors.Is(err, pgx.ErrNoRows) {
		return m, entity.NewErr(404, "01", "Marker not found")
	}
	return m, err
}

func (r *pgMarkerRepo) Delete(ctx context.Context, id int64) error {
	tag, err := r.db.Exec(ctx, `DELETE FROM tbl_marker WHERE id = $1`, id)
	if tag.RowsAffected() == 0 {
		return entity.NewErr(404, "01", "Marker not found")
	}
	return err
}

func (r *pgMarkerRepo) GetAll(ctx context.Context, limit, offset int) ([]entity.Marker, error) {
	query := `SELECT id, name FROM tbl_marker ORDER BY id LIMIT $1 OFFSET $2`
	rows, err := r.db.Query(ctx, query, limit, offset)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	res := make([]entity.Marker, 0)
	for rows.Next() {
		var m entity.Marker
		if err := rows.Scan(&m.ID, &m.Name); err != nil {
			return nil, err
		}
		res = append(res, m)
	}
	return res, nil
}

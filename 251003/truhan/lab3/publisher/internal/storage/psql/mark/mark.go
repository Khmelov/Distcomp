package mark

import (
	"context"
	"database/sql"
	"fmt"
	"log"

	"github.com/Khmelov/Distcomp/251003/truhan/lab3/publisher/internal/storage/model"
	"github.com/jmoiron/sqlx"
	"github.com/lib/pq"
)

var (
	ErrMessageNotFound  = fmt.Errorf("message not found")
	ErrConstraintsCheck = fmt.Errorf("invalid data passed")
)

type instance struct {
	db *sqlx.DB
}

type Mark interface {
	CreateMark(ctx context.Context, is model.Mark) (model.Mark, error)
	GetMarks(ctx context.Context) ([]model.Mark, error)
	GetMarkByID(ctx context.Context, id int64) (model.Mark, error)
	UpdateMarkByID(ctx context.Context, is model.Mark) (model.Mark, error)
	DeleteMarkByID(ctx context.Context, id int64) error
}

func New(db *sqlx.DB) Mark {
	return &instance{
		db: db,
	}
}

func (i *instance) CreateMark(ctx context.Context, is model.Mark) (model.Mark, error) {
	query := `INSERT INTO tbl_mark (name) 
	          VALUES ($1) RETURNING id`

	var id int64

	err := i.db.QueryRowContext(ctx, query, is.Name).
		Scan(&id)
	if err != nil {
		log.Println(err)

		if pqErr, ok := err.(*pq.Error); ok && (pqErr.Code == "23505" || pqErr.Code == "23514") {
			return model.Mark{}, ErrConstraintsCheck
		}

		return model.Mark{}, fmt.Errorf("failed to create mark: %w", err)
	}

	is.ID = id

	return is, nil
}

func (i *instance) DeleteMarkByID(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_mark WHERE id = $1`
	result, err := i.db.ExecContext(ctx, query, id)
	if err != nil {
		log.Println("Error executing DELETE query:", err)
		return fmt.Errorf("failed to delete mark: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		log.Println("Error getting rows affected:", err)
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		log.Println("No mark found with ID:", id)
		return ErrMessageNotFound
	}

	return nil
}

func (i *instance) GetMarkByID(ctx context.Context, id int64) (model.Mark, error) {
	var mark model.Mark
	query := `SELECT * FROM tbl_mark WHERE id = $1`

	err := i.db.GetContext(ctx, &mark, query, id)
	if err != nil {
		if err == sql.ErrNoRows {
			return mark, ErrMessageNotFound
		}
		return mark, fmt.Errorf("failed to retrieve mark by ID: %w", err)
	}

	return mark, nil
}

func (i *instance) GetMarks(ctx context.Context) ([]model.Mark, error) {
	var marks []model.Mark
	query := `SELECT * FROM tbl_mark`

	err := i.db.SelectContext(ctx, &marks, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve marks: %w", err)
	}

	if len(marks) == 0 {
		return []model.Mark{}, nil
	}

	return marks, nil
}

func (i *instance) UpdateMarkByID(ctx context.Context, is model.Mark) (model.Mark, error) {
	query := `UPDATE tbl_mark SET name = $1
	          WHERE id = $2 RETURNING id, name`
	var updatedMark model.Mark

	err := i.db.QueryRowContext(ctx, query, is.Name, is.ID).
		Scan(&updatedMark.ID, &updatedMark.Name)
	if err != nil {
		if err == sql.ErrNoRows {
			log.Println("mark not found with id:", is.ID)
			return updatedMark, ErrMessageNotFound
		}

		log.Println("error with query:", err)
		return updatedMark, fmt.Errorf("failed to update mark: %w", err)
	}

	return updatedMark, nil
}

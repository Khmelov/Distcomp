package creator

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
	ErrLoginExists        = fmt.Errorf("user with this login already exists")
	ErrCreatorNotFound    = fmt.Errorf("creator not found")
	ErrFailedToCreate     = fmt.Errorf("failed to create creator")
	ErrFailedToUpdate     = fmt.Errorf("failed to update creator")
	ErrFailedToDelete     = fmt.Errorf("failed to delete creator")
	ErrInvalidCreatorData = fmt.Errorf("invalid creator data")
)

type Creator interface {
	CreateCreator(ctx context.Context, cr model.Creator) (model.Creator, error)
	GetCreators(ctx context.Context) ([]model.Creator, error)
	GetCreatorByID(ctx context.Context, id int64) (model.Creator, error)
	UpdateCreatorByID(ctx context.Context, cr model.Creator) (model.Creator, error)
	DeleteCreatorByID(ctx context.Context, id int64) error
}

type instance struct {
	db *sqlx.DB
}

func New(db *sqlx.DB) Creator {
	return &instance{
		db: db,
	}
}

func (i *instance) CreateCreator(ctx context.Context, cr model.Creator) (model.Creator, error) {
	query := `INSERT INTO tbl_creator (login, password, firstname, lastname) 
	          VALUES ($1, $2, $3, $4) RETURNING id`

	var id int64
	err := i.db.QueryRowContext(ctx, query, cr.Login, cr.Password, cr.FirstName, cr.LastName).Scan(&id)
	if err != nil {
		if pqErr, ok := err.(*pq.Error); ok && pqErr.Code == "23505" {
			return model.Creator{}, ErrLoginExists
		}
		return model.Creator{}, fmt.Errorf("failed to create creator: %w", err)
	}

	cr.ID = id

	return cr, nil
}

func (i *instance) GetCreators(ctx context.Context) ([]model.Creator, error) {
	var creators []model.Creator
	query := `SELECT * FROM tbl_creator`

	err := i.db.SelectContext(ctx, &creators, query)
	if err != nil {
		return nil, fmt.Errorf("failed to retrieve creators: %w", err)
	}

	if len(creators) == 0 {
		return []model.Creator{}, nil
	}

	return creators, nil
}

func (i *instance) GetCreatorByID(ctx context.Context, id int64) (model.Creator, error) {
	var creator model.Creator
	query := `SELECT id, login, password, firstname, lastname FROM tbl_creator WHERE id = $1`

	err := i.db.GetContext(ctx, &creator, query, id)
	if err != nil {
		if err == sql.ErrNoRows {
			return creator, ErrCreatorNotFound
		}
		return creator, fmt.Errorf("failed to retrieve creator by ID: %w", err)
	}

	return creator, nil
}

func (i *instance) UpdateCreatorByID(ctx context.Context, cr model.Creator) (model.Creator, error) {
	query := `UPDATE tbl_creator SET login = $1, password = $2, firstname = $3, lastname = $4 WHERE id = $5 RETURNING id, login, password, firstname, lastname`
	var updatedCreator model.Creator

	err := i.db.QueryRowContext(ctx, query, cr.Login, cr.Password, cr.FirstName, cr.LastName, cr.ID).
		Scan(&updatedCreator.ID, &updatedCreator.Login, &updatedCreator.Password, &updatedCreator.FirstName, &updatedCreator.LastName)
	if err != nil {
		if err == sql.ErrNoRows {
			log.Println("creator not found with id:", cr.ID)
			return updatedCreator, ErrCreatorNotFound
		}

		log.Println("error with query:", err)
		return updatedCreator, fmt.Errorf("failed to update creator: %w", err)
	}

	return updatedCreator, nil
}

func (i *instance) DeleteCreatorByID(ctx context.Context, id int64) error {
	query := `DELETE FROM tbl_creator WHERE id = $1`
	result, err := i.db.ExecContext(ctx, query, id)
	if err != nil {
		log.Println("Error executing DELETE query:", err)
		return fmt.Errorf("failed to delete creator: %w", err)
	}

	rowsAffected, err := result.RowsAffected()
	if err != nil {
		log.Println("Error getting rows affected:", err)
		return fmt.Errorf("failed to check rows affected: %w", err)
	}

	if rowsAffected == 0 {
		log.Println("No creator found with ID:", id)
		return ErrCreatorNotFound
	}

	return nil
}

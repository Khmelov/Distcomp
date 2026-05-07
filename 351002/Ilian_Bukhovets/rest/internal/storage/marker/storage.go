package marker

import (
	"database/sql"
	"errors"
	"gridusko_rest/internal/model"

	"github.com/jmoiron/sqlx"
	_ "github.com/lib/pq"
)

type Storage struct {
	db *sqlx.DB
}

func NewStorage(db *sqlx.DB) *Storage {
	return &Storage{db: db}
}

func (s *Storage) CreateMarker(marker *model.Marker) error {
	query := `
        INSERT INTO tbl_marker (id, name)
        VALUES (:id, :name)
    `
	_, err := s.db.NamedExec(query, marker)
	if err != nil {
		return err
	}
	return nil
}

func (s *Storage) GetMarkerByID(id int64) (*model.Marker, error) {
	var marker model.Marker
	query := `
        SELECT id, name
        FROM tbl_marker
        WHERE id = $1
    `
	err := s.db.Get(&marker, query, id)
	if err != nil {
		if errors.Is(err, sql.ErrNoRows) {
			return nil, errors.New("marker not found")
		}
		return nil, err
	}
	return &marker, nil
}

func (s *Storage) GetMarkers() ([]model.Marker, error) {
	var markers []model.Marker
	query := `
		SELECT id, name
		FROM tbl_marker
	`
	err := s.db.Select(&markers, query)
	if err != nil {
		return nil, err
	}
	return markers, nil
}

func (s *Storage) UpdateMarker(marker *model.Marker) error {
	query := `
        UPDATE tbl_marker
        SET name = :name
        WHERE id = :id
    `
	result, err := s.db.NamedExec(query, marker)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("marker not found")
	}
	return nil
}

func (s *Storage) DeleteMarker(id int64) error {
	query := `
        DELETE FROM tbl_marker
        WHERE id = $1
    `
	result, err := s.db.Exec(query, id)
	if err != nil {
		return err
	}
	rowsAffected, _ := result.RowsAffected()
	if rowsAffected == 0 {
		return errors.New("marker not found")
	}
	return nil
}

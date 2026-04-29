package marker

import (
	"errors"
	"gridusko_rest/internal/model"
	"gridusko_rest/internal/storage/marker"
	"math/rand/v2"
)

type Service struct {
	storage *marker.Storage
}

func NewService(storage *marker.Storage) *Service {
	return &Service{storage: storage}
}

var (
	ErrInvalidBody = errors.New("invalid body")
)

func checks(marker *model.Marker) error {
	if marker.Name == "" {
		return ErrInvalidBody
	}

	if len(marker.Name) < 2 || len(marker.Name) > 32 {
		return ErrInvalidBody
	}

	return nil
}

func (s *Service) CreateMarker(marker *model.Marker) error {
	if err := checks(marker); err != nil {
		return err
	}

	marker.ID = int64(rand.Int())

	return s.storage.CreateMarker(marker)
}

func (s *Service) GetMarkerByID(id int64) (*model.Marker, error) {
	return s.storage.GetMarkerByID(id)
}

func (s *Service) GetMarkers() ([]model.Marker, error) {
	return s.storage.GetMarkers()
}

func (s *Service) UpdateMarker(marker *model.Marker) error {
	if marker.ID == 0 {
		return errors.New("marker ID is required")
	}
	_, err := s.storage.GetMarkerByID(marker.ID)
	if err != nil {
		return err
	}

	if err := checks(marker); err != nil {
		return err
	}

	return s.storage.UpdateMarker(marker)
}

func (s *Service) DeleteMarker(id int64) error {
	return s.storage.DeleteMarker(id)
}

package service

import (
	"context"
	"errors"
	"fmt"

	"github.com/jackc/pgx/v5/pgconn"
	"news-board/publisher/internal/domain"
	"news-board/publisher/internal/domain/models"
	"news-board/publisher/internal/dto"
)

type MarkerService struct {
	repo models.MarkerRepository
}

func NewMarkerService(repo models.MarkerRepository) *MarkerService {
	return &MarkerService{repo: repo}
}

func (s *MarkerService) Create(ctx context.Context, req *dto.MarkerRequestTo) (*dto.MarkerResponseTo, error) {
	marker := &models.Marker{
		Name: req.Name,
	}
	if err := s.repo.Create(ctx, marker); err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, domain.ErrMarkerAlreadyExists
		}
		return nil, fmt.Errorf("failed to create marker: %w", err)
	}
	return &dto.MarkerResponseTo{
		ID:   marker.ID,
		Name: marker.Name,
	}, nil
}

func (s *MarkerService) GetAll(ctx context.Context, limit, offset int) ([]dto.MarkerResponseTo, error) {
	markers, err := s.repo.GetAll(ctx, limit, offset)
	if err != nil {
		return nil, fmt.Errorf("failed to get markers: %w", err)
	}
	resp := make([]dto.MarkerResponseTo, 0, len(markers))
	for _, m := range markers {
		resp = append(resp, dto.MarkerResponseTo{
			ID:   m.ID,
			Name: m.Name,
		})
	}
	return resp, nil
}

func (s *MarkerService) GetByID(ctx context.Context, id int64) (*dto.MarkerResponseTo, error) {
	marker, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("failed to get marker: %w", err)
	}
	if marker == nil {
		return nil, domain.ErrMarkerNotFound
	}
	return &dto.MarkerResponseTo{
		ID:   marker.ID,
		Name: marker.Name,
	}, nil
}

func (s *MarkerService) Update(ctx context.Context, id int64, req *dto.MarkerRequestTo) (*dto.MarkerResponseTo, error) {
	marker := &models.Marker{
		ID:   id,
		Name: req.Name,
	}
	updated, err := s.repo.Update(ctx, marker)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, domain.ErrMarkerAlreadyExists
		}
		return nil, fmt.Errorf("failed to update marker: %w", err)
	}
	if !updated {
		return nil, domain.ErrMarkerNotFound
	}
	return &dto.MarkerResponseTo{
		ID:   marker.ID,
		Name: marker.Name,
	}, nil
}

func (s *MarkerService) Delete(ctx context.Context, id int64) error {
	deleted, err := s.repo.Delete(ctx, id)
	if err != nil {
		return fmt.Errorf("failed to delete marker: %w", err)
	}
	if !deleted {
		return domain.ErrMarkerNotFound
	}
	return nil
}

func (s *MarkerService) GetByNewsID(ctx context.Context, newsID int64) ([]dto.MarkerResponseTo, error) {
	markers, err := s.repo.GetByNewsID(ctx, newsID)
	if err != nil {
		return nil, fmt.Errorf("failed to get markers by news id: %w", err)
	}
	resp := make([]dto.MarkerResponseTo, 0, len(markers))
	for _, m := range markers {
		resp = append(resp, dto.MarkerResponseTo{
			ID:   m.ID,
			Name: m.Name,
		})
	}
	return resp, nil
}

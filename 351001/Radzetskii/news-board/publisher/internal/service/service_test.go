package service

import (
	"context"
	"errors"
	"testing"

	"github.com/jackc/pgx/v5/pgconn"
	"news-board/publisher/internal/domain"
	"news-board/publisher/internal/domain/models"
	"news-board/publisher/internal/dto"
)

type stubUserRepo struct {
	createFn func(ctx context.Context, user *models.User) error
}

func (s *stubUserRepo) Create(ctx context.Context, user *models.User) error {
	return s.createFn(ctx, user)
}

func (s *stubUserRepo) GetByID(context.Context, int64) (*models.User, error) { return nil, nil }
func (s *stubUserRepo) GetByLogin(context.Context, string) (*models.User, error) {
	return nil, nil
}
func (s *stubUserRepo) GetAll(context.Context, int, int) ([]models.User, error)  { return nil, nil }
func (s *stubUserRepo) Update(context.Context, *models.User) (bool, error)       { return false, nil }
func (s *stubUserRepo) Delete(context.Context, int64) (bool, error)              { return false, nil }
func (s *stubUserRepo) GetByNewsID(context.Context, int64) (*models.User, error) { return nil, nil }

type stubMarkerRepo struct {
	getByIDFn func(ctx context.Context, id int64) (*models.Marker, error)
}

func (s *stubMarkerRepo) Create(context.Context, *models.Marker) error { return nil }
func (s *stubMarkerRepo) GetByID(ctx context.Context, id int64) (*models.Marker, error) {
	return s.getByIDFn(ctx, id)
}
func (s *stubMarkerRepo) GetByName(context.Context, string) (*models.Marker, error) { return nil, nil }
func (s *stubMarkerRepo) GetAll(context.Context, int, int) ([]models.Marker, error) { return nil, nil }
func (s *stubMarkerRepo) Update(context.Context, *models.Marker) (bool, error)      { return false, nil }
func (s *stubMarkerRepo) Delete(context.Context, int64) (bool, error)               { return false, nil }
func (s *stubMarkerRepo) GetByNewsID(context.Context, int64) ([]models.Marker, error) {
	return nil, nil
}
func (s *stubMarkerRepo) CountNewsByMarker(context.Context, int64) (int, error) { return 0, nil }

type stubNewsRepo struct {
	createWithMarkersFn func(ctx context.Context, news *models.News, markerNames []string) error
	deleteWithCleanupFn func(ctx context.Context, id int64) (bool, error)
	getByIDFn           func(ctx context.Context, id int64) (*models.News, error)
	addMarkerFn         func(ctx context.Context, newsID, markerID int64) error
}

func (s *stubNewsRepo) Create(context.Context, *models.News) error { return nil }
func (s *stubNewsRepo) CreateWithMarkers(ctx context.Context, news *models.News, markerNames []string) error {
	return s.createWithMarkersFn(ctx, news, markerNames)
}
func (s *stubNewsRepo) GetByID(ctx context.Context, id int64) (*models.News, error) {
	return s.getByIDFn(ctx, id)
}
func (s *stubNewsRepo) GetAll(context.Context, int, int, map[string]interface{}, string) ([]models.News, error) {
	return nil, nil
}
func (s *stubNewsRepo) Update(context.Context, *models.News) (bool, error) { return false, nil }
func (s *stubNewsRepo) Delete(context.Context, int64) (bool, error)        { return false, nil }
func (s *stubNewsRepo) DeleteWithCleanup(ctx context.Context, id int64) (bool, error) {
	return s.deleteWithCleanupFn(ctx, id)
}
func (s *stubNewsRepo) AddMarker(ctx context.Context, newsID, markerID int64) error {
	return s.addMarkerFn(ctx, newsID, markerID)
}
func (s *stubNewsRepo) RemoveMarker(context.Context, int64, int64) (bool, error) { return false, nil }
func (s *stubNewsRepo) GetMarkers(context.Context, int64) ([]models.Marker, error) {
	return nil, nil
}
func (s *stubNewsRepo) Search(context.Context, map[string]interface{}, int, int) ([]models.News, error) {
	return nil, nil
}

func TestUserServiceCreateMapsUniqueViolation(t *testing.T) {
	svc := NewUserService(&stubUserRepo{
		createFn: func(context.Context, *models.User) error {
			return &pgconn.PgError{Code: "23505"}
		},
	})

	_, err := svc.Create(context.Background(), &dto.UserRequestTo{
		Login:     "john",
		Password:  "password123",
		Firstname: "John",
		Lastname:  "Doe",
	})

	if !errors.Is(err, domain.ErrUserLoginNotUnique) {
		t.Fatalf("expected ErrUserLoginNotUnique, got %v", err)
	}
}

func TestNewsServiceCreateMapsForeignKeyViolation(t *testing.T) {
	svc := NewNewsService(&stubNewsRepo{
		createWithMarkersFn: func(context.Context, *models.News, []string) error {
			return &pgconn.PgError{Code: "23503"}
		},
	}, &stubMarkerRepo{})

	_, err := svc.Create(context.Background(), &dto.NewsRequestTo{
		UserID:  42,
		Title:   "Title",
		Content: "Content",
	})

	if !errors.Is(err, domain.ErrNewsUserNotFound) {
		t.Fatalf("expected ErrNewsUserNotFound, got %v", err)
	}
}

func TestNewsServiceDeleteReturnsNotFound(t *testing.T) {
	svc := NewNewsService(&stubNewsRepo{
		deleteWithCleanupFn: func(context.Context, int64) (bool, error) {
			return false, nil
		},
	}, &stubMarkerRepo{})

	err := svc.Delete(context.Background(), 10)
	if !errors.Is(err, domain.ErrNewsNotFound) {
		t.Fatalf("expected ErrNewsNotFound, got %v", err)
	}
}

func TestNewsServiceAddMarkerMapsDuplicateRelation(t *testing.T) {
	svc := NewNewsService(&stubNewsRepo{
		getByIDFn: func(context.Context, int64) (*models.News, error) {
			return &models.News{ID: 1}, nil
		},
		addMarkerFn: func(context.Context, int64, int64) error {
			return &pgconn.PgError{Code: "23505"}
		},
	}, &stubMarkerRepo{
		getByIDFn: func(context.Context, int64) (*models.Marker, error) {
			return &models.Marker{ID: 2, Name: "urgent"}, nil
		},
	})

	err := svc.AddMarker(context.Background(), 1, 2)
	if !errors.Is(err, domain.ErrNewsMarkerDuplicate) {
		t.Fatalf("expected ErrNewsMarkerDuplicate, got %v", err)
	}
}

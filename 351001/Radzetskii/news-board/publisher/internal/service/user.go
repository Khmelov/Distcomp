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

type UserService struct {
	repo models.UserRepository
}

func NewUserService(repo models.UserRepository) *UserService {
	return &UserService{repo: repo}
}

func (s *UserService) Create(ctx context.Context, req *dto.UserRequestTo) (*dto.UserResponseTo, error) {
	user := &models.User{
		Login:     req.Login,
		Password:  req.Password,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
	}
	if err := s.repo.Create(ctx, user); err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, domain.ErrUserLoginNotUnique
		}
		return nil, fmt.Errorf("failed to create user: %w", err)
	}
	return &dto.UserResponseTo{
		ID:        user.ID,
		Login:     user.Login,
		Firstname: user.Firstname,
		Lastname:  user.Lastname,
	}, nil
}

func (s *UserService) GetAll(ctx context.Context, limit, offset int) ([]dto.UserResponseTo, error) {
	users, err := s.repo.GetAll(ctx, limit, offset)
	if err != nil {
		return nil, fmt.Errorf("failed to get users: %w", err)
	}
	resp := make([]dto.UserResponseTo, 0, len(users))
	for _, u := range users {
		resp = append(resp, dto.UserResponseTo{
			ID:        u.ID,
			Login:     u.Login,
			Firstname: u.Firstname,
			Lastname:  u.Lastname,
		})
	}
	return resp, nil
}

func (s *UserService) GetByID(ctx context.Context, id int64) (*dto.UserResponseTo, error) {
	user, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("failed to get user: %w", err)
	}
	if user == nil {
		return nil, domain.ErrUserNotFound
	}
	return &dto.UserResponseTo{
		ID:        user.ID,
		Login:     user.Login,
		Firstname: user.Firstname,
		Lastname:  user.Lastname,
	}, nil
}

func (s *UserService) Update(ctx context.Context, id int64, req *dto.UserRequestTo) (*dto.UserResponseTo, error) {
	user := &models.User{
		ID:        id,
		Login:     req.Login,
		Password:  req.Password,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
	}
	updated, err := s.repo.Update(ctx, user)
	if err != nil {
		var pgErr *pgconn.PgError
		if errors.As(err, &pgErr) && pgErr.Code == "23505" {
			return nil, domain.ErrUserLoginNotUnique
		}
		return nil, fmt.Errorf("failed to update user: %w", err)
	}
	if !updated {
		return nil, domain.ErrUserNotFound
	}
	return &dto.UserResponseTo{
		ID:        user.ID,
		Login:     user.Login,
		Firstname: user.Firstname,
		Lastname:  user.Lastname,
	}, nil
}

func (s *UserService) Delete(ctx context.Context, id int64) error {
	deleted, err := s.repo.Delete(ctx, id)
	if err != nil {
		return fmt.Errorf("failed to delete user: %w", err)
	}
	if !deleted {
		return domain.ErrUserNotFound
	}
	return nil
}

func (s *UserService) GetByNewsID(ctx context.Context, newsID int64) (*dto.UserResponseTo, error) {
	user, err := s.repo.GetByNewsID(ctx, newsID)
	if err != nil {
		return nil, fmt.Errorf("failed to get user by news: %w", err)
	}
	if user == nil {
		return nil, domain.ErrUserNotFound
	}
	return &dto.UserResponseTo{
		ID:        user.ID,
		Login:     user.Login,
		Firstname: user.Firstname,
		Lastname:  user.Lastname,
	}, nil
}

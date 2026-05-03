package service

import (
	"context"
	"errors"
	"fmt"

	"news-board/publisher/internal/domain"
	"news-board/publisher/internal/domain/models"
	"news-board/publisher/internal/dto"

	"github.com/jackc/pgx/v5/pgconn"
	"golang.org/x/crypto/bcrypt"
)

type UserService struct {
	repo models.UserRepository
}

func NewUserService(repo models.UserRepository) *UserService {
	return &UserService{repo: repo}
}

func (s *UserService) Create(ctx context.Context, req *dto.UserRequestTo) (*dto.UserResponseTo, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, fmt.Errorf("failed to hash password: %w", err)
	}
	role := req.Role
	if role == "" {
		role = "CUSTOMER"
	}
	user := &models.User{
		Login:     req.Login,
		Password:  string(hashedPassword),
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
		Role:      role,
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
		Role:      user.Role,
	}, nil
}

func (s *UserService) Authenticate(ctx context.Context, login, password string) (*models.User, error) {
	user, err := s.repo.GetByLogin(ctx, login)
	if err != nil {
		return nil, fmt.Errorf("failed to lookup user: %w", err)
	}
	if user == nil {
		return nil, domain.ErrInvalidCredentials
	}
	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(password)); err == nil {
		return user, nil
	}
	if user.Password == password {
		hashedPassword, hashErr := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
		if hashErr == nil {
			user.Password = string(hashedPassword)
			_, _ = s.repo.Update(ctx, user)
		}
		return user, nil
	}
	return nil, domain.ErrInvalidCredentials
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
			Role:      u.Role,
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
		Role:      user.Role,
	}, nil
}

func (s *UserService) GetByLogin(ctx context.Context, login string) (*dto.UserResponseTo, error) {
	user, err := s.repo.GetByLogin(ctx, login)
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
		Role:      user.Role,
	}, nil
}

func (s *UserService) Update(ctx context.Context, id int64, req *dto.UserRequestTo) (*dto.UserResponseTo, error) {
	existing, err := s.repo.GetByID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("failed to lookup user: %w", err)
	}
	if existing == nil {
		return nil, domain.ErrUserNotFound
	}
	role := req.Role
	if role == "" {
		role = existing.Role
	}
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, fmt.Errorf("failed to hash password: %w", err)
	}
	user := &models.User{
		ID:        id,
		Login:     req.Login,
		Password:  string(hashedPassword),
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
		Role:      role,
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
		Role:      user.Role,
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
		Role:      user.Role,
	}, nil
}

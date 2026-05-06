package auth

import (
	"context"
	"fmt"

	apperrors "publisher/internal/errors"
	"publisher/internal/model"
	"publisher/internal/repository"

	"golang.org/x/crypto/bcrypt"
)

type AuthService struct {
	userRepo repository.UserRepository
	jwtSvc   *JWTService
}

func NewAuthService(userRepo repository.UserRepository, jwtSvc *JWTService) *AuthService {
	return &AuthService{userRepo: userRepo, jwtSvc: jwtSvc}
}

type RegisterRequest struct {
	Login     string         `json:"login"`
	Password  string         `json:"password"`
	Firstname string         `json:"firstname"`
	Lastname  string         `json:"lastname"`
	Role      model.UserRole `json:"role"`
}

type LoginRequest struct {
	Login    string `json:"login"`
	Password string `json:"password"`
}

type TokenResponse struct {
	AccessToken string `json:"access_token"`
	TokenType   string `json:"token_type"`
}

func (s *AuthService) Register(ctx context.Context, req *RegisterRequest) (*model.User, error) {
	if len(req.Login) < 2 || len(req.Login) > 64 {
		return nil, apperrors.ErrBadRequest
	}
	if len(req.Password) < 8 || len(req.Password) > 128 {
		return nil, apperrors.ErrBadRequest
	}
	if len(req.Firstname) < 2 || len(req.Firstname) > 64 {
		return nil, apperrors.ErrBadRequest
	}
	if len(req.Lastname) < 2 || len(req.Lastname) > 64 {
		return nil, apperrors.ErrBadRequest
	}
	role := req.Role
	if role != model.RoleAdmin && role != model.RoleCustomer {
		role = model.RoleCustomer
	}
	hashed, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, fmt.Errorf("hash password: %w", err)
	}
	user := &model.User{
		Login:     req.Login,
		Password:  string(hashed),
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
		Role:      role,
	}
	created, err := s.userRepo.Create(ctx, user)
	if err != nil {
		return nil, apperrors.FromDBError(err)
	}
	return created, nil
}

func (s *AuthService) Login(ctx context.Context, req *LoginRequest) (*TokenResponse, error) {
	user, err := s.userRepo.FindByLogin(ctx, req.Login)
	if err != nil {
		return nil, apperrors.ErrUnauthorized
	}
	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password)); err != nil {
		return nil, apperrors.ErrUnauthorized
	}
	token, err := s.jwtSvc.GenerateToken(user)
	if err != nil {
		return nil, fmt.Errorf("generate token: %w", err)
	}
	return &TokenResponse{
		AccessToken: token,
		TokenType:   "Bearer",
	}, nil
}

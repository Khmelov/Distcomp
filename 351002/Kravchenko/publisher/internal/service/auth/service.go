package service

import (
	"context"
	"errors"
	"fmt"
	authrepo "labs/publisher/internal/repository/auth"
	editordto "labs/shared/dto/editorV2"
	editormodel "labs/shared/model/editorV2"
)

type authService struct {
	repo         authrepo.Repository
	tokenManager *TokenManager
}

type Service interface {
	Register(ctx context.Context, req editordto.CreateEditorRequest) (*editormodel.Editor, error)
	Login(ctx context.Context, req editordto.LoginRequest) (editordto.LoginResponse, error)
}

func NewAuthService(repo authrepo.Repository, tm *TokenManager) Service {
	return &authService{
		repo:         repo,
		tokenManager: tm,
	}
}

func (s *authService) Register(ctx context.Context, req editordto.CreateEditorRequest) (*editormodel.Editor, error) {
	hashedPassword, err := HashPassword(req.Password)
	if err != nil {
		return nil, fmt.Errorf("auth_service: failed to hash password: %w", err)
	}

	newEditor := &editormodel.Editor{
		Login:     req.Login,
		Password:  hashedPassword,
		Firstname: req.Firstname,
		Lastname:  req.Lastname,
		Role:      req.Role,
	}

	created, err := s.repo.Register(ctx, newEditor)
	if err != nil {
		return nil, err
	}

	return created, nil
}

func (s *authService) Login(ctx context.Context, req editordto.LoginRequest) (editordto.LoginResponse, error) {
	var res editordto.LoginResponse

	user, err := s.repo.GetByLogin(ctx, req.Login)
	if err != nil {
		if errors.Is(err, editormodel.ErrNotFound) {
			return res, editormodel.ErrInvalidCredentials
		}
		return res, err
	}

	if !CheckPasswordHash(req.Password, user.Password) {
		return res, editormodel.ErrInvalidCredentials
	}

	// 3. Генерируем JWT (включает sub, iat, exp, role согласно ТЗ)
	token, err := s.tokenManager.GenerateJWT(user.Login, user.Role)
	if err != nil {
		return res, fmt.Errorf("auth_service: failed to generate token: %w", err)
	}

	res.AccessToken = token
	res.TypeToken = "Bearer"

	return res, nil
}

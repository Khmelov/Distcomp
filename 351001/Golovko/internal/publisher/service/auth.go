package service

import (
	"context"
	"errors"

	"distcomp/internal/dto"
	"distcomp/internal/publisher/repository/postgres"
	"distcomp/pkg/auth"

	"golang.org/x/crypto/bcrypt"
)

type AuthService interface {
	Login(ctx context.Context, req dto.LoginRequestTo) (dto.LoginResponseTo, error)
}

type authService struct {
	repo postgres.Storage
}

func NewAuthService(repo postgres.Storage) AuthService {
	return &authService{repo: repo}
}

func (s *authService) Login(ctx context.Context, req dto.LoginRequestTo) (dto.LoginResponseTo, error) {
	editor, err := s.repo.GetEditorByLogin(ctx, req.Login)
	if err != nil {
		if errors.Is(err, postgres.ErrNotFound) {
			return dto.LoginResponseTo{}, errors.New("invalid login or password")
		}
		return dto.LoginResponseTo{}, err
	}

	err = bcrypt.CompareHashAndPassword([]byte(editor.Password), []byte(req.Password))
	if err != nil {
		return dto.LoginResponseTo{}, errors.New("invalid login or password")
	}

	token, err := auth.GenerateToken(editor.ID, editor.Login, editor.Role)
	if err != nil {
		return dto.LoginResponseTo{}, err
	}

	return dto.LoginResponseTo{
		AccessToken: token,
		TokenType:   "Bearer",
	}, nil
}
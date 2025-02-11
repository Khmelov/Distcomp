package service

import "github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository"

type Service struct {
	*repository.Repository
}

func New(repo *repository.Repository) *Service {
	return &Service{
		Repository: repo,
	}
}

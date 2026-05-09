package service

import "github.com/Khmelov/Distcomp/351001/Ushakov/labX/discussion/internal/storage"

type service struct {
	PostService
}

func New(repo storage.Repository) Service {
	return service{
		PostService: repo,
	}
}

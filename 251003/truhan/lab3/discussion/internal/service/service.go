package service

import "github.com/Khmelov/Distcomp/251003/truhan/lab3/discussion/internal/storage"

type service struct {
	MessageService
}

func New(repo storage.Repository) Service {
	return service{
		MessageService: repo,
	}
}

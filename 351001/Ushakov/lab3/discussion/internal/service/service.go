package service

import "github.com/Khmelov/Distcomp/351001/Ushakov/lab3/discussion/internal/storage"

type service struct {
	github.com/Khmelov/Distcomp/351001/UshakovService
}

func New(repo storage.Repository) Service {
	return service{
		github.com / Khmelov / Distcomp / 351001 / UshakovService: repo,
	}
}

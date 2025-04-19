package service

import (
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/adapter"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/repository"
	"github.com/Khmelov/Distcomp/251004/Sazonov/internal/service/notice"
)

type service struct {
	WriterService
	NewsService
	LabelService
	NoticeService
}

func New(repo repository.Repository, adapter adapter.Adapter) Service {
	return &service{
		WriterService: repo,
		NewsService:   repo,
		LabelService:  repo,

		NoticeService: notice.New(adapter.SyncNotice(), adapter.AsyncNotice(), repo, repo),
	}
}

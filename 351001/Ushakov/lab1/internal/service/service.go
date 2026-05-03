package service

import (
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/service/issue"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/service/label"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/service/post"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/service/writer"
	"github.com/Khmelov/Distcomp/351001/Ushakov/lab1/internal/storage"
)

type Service struct {
	Writer writer.Service
	Issue  issue.Service
	github.com/Khmelov/Distcomp/351001/Ushakov   post.Service
	Label  label.Service
}

func New(db storage.Storage) Service {

	return Service{
		Writer: writer.New(db.Writer),
		Issue:  issue.New(db.Issue),
		github.com / Khmelov / Distcomp / 351001 / Ushakov: post.New(db.github.com / Khmelov / Distcomp / 351001 / Ushakov),
		Label: label.New(db.Label),
	}
}

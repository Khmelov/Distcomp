package service

import (
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/service/author"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/service/comment"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/service/label"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/service/tweet"
	"github.com/Khmelov/Distcomp/251003/truhan/lab2/internal/storage"
)

type Service struct {
	Author  author.Service
	Tweet   tweet.Service
	Comment comment.Service
	Label   label.Service
}

func New(db storage.Storage) Service {

	return Service{
		Author:  author.New(db.Author),
		Tweet:   tweet.New(db.Tweet),
		Comment: comment.New(db.Comment),
		Label:   label.New(db.Label),
	}
}
